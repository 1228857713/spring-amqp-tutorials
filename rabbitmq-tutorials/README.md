该介绍之主要翻译至官方文档 官方文档
RabbitMQ 是一个消息中间件，你可以把他想象成为一个邮局。当你把邮件投递到邮箱里面去的时候，你可以确认，会有邮递员把你的邮件传递给消息的接受者。
在这个比喻里 RabbitMQ 就是一个邮箱，一个邮局，一个快递员。最大的区别就是，RabbitMQ 处理的不是邮件,而是存储和装发二进制数据。
1.术语介绍
RabbitMQ 通常会使用下面的术语
• Producing  生产仅意味着发送。发送消息的程序是生产者：
• Queue 是一RabbitMQ 中的一个内部邮箱，尽管消息通过了 RabbitMQ 和你的应用程序，但是他只能被存储在Queue中。Queue一般把消息存储在主机的内存和磁盘，它本质上是一个大的消息缓冲器。许多生产者可以发送消息到一个队列中，许多消费者可以尝试从一个队列接收数据。这就是我们表示队列的方式：
• Consuming 消费与接收具有相似的含义。一个消费者是一个程序，主要是等待接收信息。
请注意，生产者，消费者和中间件不需要位于同一主机上。实际上，在大多数应用程序中它们不是。一个应用程序既可以是生产者，也可以是消费者。
2.工作队列
1.当我们有多个消费者同时消费一个queue的时候，默认RabbitMQ 会使用 Round-robin 策略 ，也就是循环策略，每个消费者都会轮询的去 queue 中消费数据。正常情况下，每个消费者消费的数据的负载是一样的。
2.可以通过设置basicQos属性，来为work设计负载，在一个work 满负载时可以将消息分发到不是那么有压力的work上
int prefetchCount = 1;
channel.basicQos(prefetchCount);
3.消息确认
1.当消息一旦被消费者接收，队列中的消息就会被删除。那么问题来了：RabbitMQ怎么知道消息被接收了呢？
这就要通过消息确认机制（Acknowlege）来实现了。当消费者获取消息后，会向RabbitMQ发送回执ACK，告知消息已经被接收。不过这种回执ACK分两种情况：
• 自动ACK：消息一旦被接收，消费者自动发送ACK
• 手动ACK：消息接收后，不会发送ACK，需要手动调用
2.这两ACK要怎么选择呢？这需要看消息的重要性：
• 如果消息不太重要，丢失也没有影响，那么自动ACK会比较方便
• 如果消息非常重要，不容丢失。那么最好在消费完成后手动ACK，否则接收消息后就自动ACK，RabbitMQ就会把消息从队列中删除。如果此时消费者宕机，那么消息就丢失了。
注意如果你的消息忘记了返回ack 可能会导致rabbitmq 消耗很多内存
可以使用下面的命令查看 消息使用的情况
rabbitmqctl list_queues name messages_ready messages_unacknowledged
4.消息持久化
1.设置 durable 属性为true 开启 rabbitmq 的消息持久化
boolean durable = true;
channel.queueDeclare("task_queue", durable, false, false, null);
2.设置发送的消息为持久化 MessageProperties.PERSISTENT_TEXT_PLAIN,
channel.basicPublish("", "task_queue",
            MessageProperties.PERSISTENT_TEXT_PLAIN,
            message.getBytes());
5.exchange
exchange :为了让一个消息的生产者可以被多个消息订阅者所消费，rabbit 有了新的模型 exchange 。exchange 的作用就是接受消息，然后把消息分发到 queue 上。所以一个 exchange 应该知道如何处理一个消息，这个消息是分发到指定的queue 还是分发到很多queue 上，又或者被丢弃掉。这个规则被定义为 exchange type
exchange type 有四种类型分别是 deirect topic headers 和 fanout
• fanout
从字面的意思<扇出；展开；分列>可以看出,fanout 类型的exchange 会将收到信分发给所有的和他绑定的queue。
类似于下面的图
1.使用下面的方式可以声名并定义一个 exchange
channel.exchangeDeclare("logs", "fanout");
2.使用下面的命令可以查看所有的 exchange
rabbitmqctl list_exchanges
• direct
在绑定exchage 和 queue的时候回声名一个routing key ，在投递消息的时候同样会 带上这个routing key 。exchage 会根据这个routing key 将消息分发到对应的 queue 上面去
如图，我们声名一个了 类型为 direct 的Exchage 。相应的我们定义了 3个 日志等级，info warning error ，其中 第一个queue 和 exchange 通过 error 绑定了，第二个 queue 通过 [info,warning,error] 三个routing key 绑定。如果发送的消息是error 等级的，那么 消费者c1 ，和c2 都可以消费到。而如果投递的消息是 info那么只有消费者c2 可以消费。
• topic
1.topic 类型的exchange其实和direct 类似，只是他的routing_变得更复杂了，形如 stock.wang.zhen, orange.a.a这种形式，也就是多个单词中间用 . 号隔开。但是一共不能超过255 个字节。 也就是说我们在发送消息给 topic 类型的exchange 的时候，其routing_key必须使用 com.wangzhen.test 这种形式的。
2.当我们绑定exchange 和 queue 的时候必须要也采用这种形式。不过可以使用两种特殊的符号
  -1.*匹配任意一个单词
  -2.#匹配0个或者多个单词
3.如图，我们交换机 和Q1 的绑定规则为 *.orange.*  ,Q2 和 交换机绑定的规则为 *.*.rabbit 和 lazy.#,在发送消息到交换机的时候  如果是 wang.orange.zhen 则会进入Q1 ,如果是 lazy.orange.wujiahu 那么会键入 Q1 和Q2
4.值得注意的是其实 topic 类型的交换机是最灵活的而且最可以模拟成其他类型的交换机。如果queueb绑定的binding key是 #的话，那么其实就是fanout的 交换机。当特殊字符* 和# 都不能使用成为绑定的key 的话，那么其实就是direct类型的交换机
• headers 
用的太少就不介绍了
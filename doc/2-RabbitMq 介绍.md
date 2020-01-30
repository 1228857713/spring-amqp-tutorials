# RabbitMq 介绍
<该介绍之主要翻译至官方文档> [官方文档](https://www.rabbitmq.com/tutorials/tutorial-one-java.html)

## RabbitM 的基本概念
`RabbitMQ` 是一个消息中间件，你可以把他想象成为一个邮局。当你把邮件投递到邮箱里面去的时候，你可以确认，会有邮递员把你的邮件传递给消息的接受者
在这个比喻里 `RabbitMQ` 就是一个邮箱，一个邮局，一个快递员。最大的区别就是，`RabbitMQ` 处理的不是邮件,而是存储和装发二进制数据。

### 术语介绍
`RabbitMQ` 和消息通常会使用下面的术语
- Producing 一个发送消息的程序就是 一个 producer


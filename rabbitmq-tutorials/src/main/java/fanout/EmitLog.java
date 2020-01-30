package fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import util.FactoryUtil;

public class EmitLog {

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = FactoryUtil.getFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 声名一个 类型为 fanout 名字为 logs 的exchange
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        String message = argv.length < 1 ? "info: Hello World!" :
                    String.join(" ", argv);

        // 发布消息
         channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
         System.out.println(" [x] Sent '" + message + "'");
    }


}


package helloWorld;

import com.rabbitmq.client.*;
import util.FactoryUtil;

import java.io.IOException;

public class Recv {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = FactoryUtil.getFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

//        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//            String message = new String(delivery.getBody(), "UTF-8");
//            System.out.println(" [x] Received '" + message + "'");
//        };
        DeliverCallback deliverCallback = new DeliverCallback() {
          @Override
          public void handle(String consumerTag, Delivery delivery) throws IOException {
              String message = new String(delivery.getBody(), "UTF-8");
              System.out.println(" [x] Received '" + message + "'");
          }
      };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}
package util;

import com.rabbitmq.client.ConnectionFactory;

public class FactoryUtil {
    public static ConnectionFactory factory;
    public static ConnectionFactory getFactory(){
        if(factory == null){
            factory = new ConnectionFactory();
            factory.setHost("127.0.0.1");
            factory.setPort(5672);
            factory.setUsername("admin");
            factory.setPassword("admin");
        }

        return factory;
    }
}

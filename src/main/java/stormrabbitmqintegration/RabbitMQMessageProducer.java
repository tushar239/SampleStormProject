package stormrabbitmqintegration;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tushar Chokshi @ 1/9/17.
 */
public class RabbitMQMessageProducer {
    public static void main(String[] args) {
        // Just setting up exchange and queue in RabbitMQ (Avoiding manual creation of exchange and queue)
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-rabbitmq-context.xml");

        AmqpTemplate aTemplate = (AmqpTemplate) context.getBean("myTemplate");// getting a reference to the sender bean

        List<String> strings = readChangeLogFile();

        String routingKey = "storm.rabbitmq.int.message";

        for (int i = 0; i < 100; i++) {
            for (String msg : strings) {
                aTemplate.convertAndSend(routingKey, msg);
            }

        }

    }

    private static List<String> readChangeLogFile() {
        InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("changelog.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(systemResourceAsStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        List<String> lines = new LinkedList<>();
        String thisLine;
        try {
            while ((thisLine = bufferedReader.readLine()) != null) {
                lines.add(thisLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines.stream().collect(Collectors.toList());
    }
}

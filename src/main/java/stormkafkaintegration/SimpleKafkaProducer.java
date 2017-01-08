package stormkafkaintegration;

import com.google.common.io.Resources;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author Tushar Chokshi @ 12/31/16.
 */
public class SimpleKafkaProducer {

    // Please make sure that you have this Topic created in kafka
    public static String TOPIC = "StormKafkaIntTopic";

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        //Assign topicName to string variable
        String topicName = TOPIC;

        // set up the producer
        KafkaProducer<String, String> producer;
        try (InputStream props = Resources.getResource("producer.properties").openStream()) {
            Properties properties = new Properties();
            properties.load(props);
            producer = new org.apache.kafka.clients.producer.KafkaProducer<>(properties);
        }

        List<String> commits = readChangeLogFile();

        for (String commit : commits) {
            String[] commitSplits = commit.split(" ");
            RecordMetadata recordMetadata = producer.send(new ProducerRecord<>(topicName, commitSplits[0], commit)).get();
        }

        System.out.println("Messages sent successfully");
        producer.close();

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
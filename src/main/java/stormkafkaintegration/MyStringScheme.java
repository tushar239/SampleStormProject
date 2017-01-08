package stormkafkaintegration;

import org.apache.storm.kafka.StringScheme;
import org.apache.storm.tuple.Fields;

/**
 * @author Tushar Chokshi @ 1/7/17.
 */
public class MyStringScheme extends StringScheme {
    public Fields getOutputFields() {
        return new Fields("commit");
    }
}

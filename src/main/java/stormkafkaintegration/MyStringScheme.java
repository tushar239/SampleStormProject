package stormkafkaintegration;

import org.apache.storm.kafka.StringScheme;
import org.apache.storm.tuple.Fields;

/**
 * @author Tushar Chokshi @ 1/7/17.
 */
public class MyStringScheme extends StringScheme {
    // default emitted field name will be 'str'. If you want to change it, you need to override a method as below.
    @Override
    public Fields getOutputFields() {
        return new Fields("commit");
    }
}

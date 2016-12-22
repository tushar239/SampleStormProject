import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Tushar Chokshi @ 12/21/16.
 */
public class EmailCounter extends BaseBasicBolt {
    private Map<String, Integer> counts;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        counts = new HashMap<>();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // this bolt does not emit anything, so does not declare any fields
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        //System.out.println("Inside EmailCounter...");
        String email = input.getStringByField("email");
        Optional.ofNullable(email)
                .ifPresent(emailId -> {
                            counts.computeIfAbsent(emailId, newValue -> 1);
                            counts.computeIfPresent(emailId, (key, oldValue) -> oldValue + 1);
                            System.out.println("count of :"+ emailId+" : "+counts.get(emailId));
                        }
                );


    }
}

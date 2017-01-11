package githubcommittopology;

import org.apache.storm.Config;
import org.apache.storm.Constants;
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

    /*
    You can set each component level configuration by overriding this method.
    Alternatively, you can set the configuration during building of topology. See GithubTpologyBuilder.

    Here, we are setting the tick tuple frequency.
    Storm will emit up a tick tuple (system level tuple) every 60 seconds internally to this bolt.
    When tick tuple arrives, this bolt can do something different as shown in execute method.

    There is no guarantee that this bolt will receive a Tick Tuple exactly after every 60 seconds because there might many input tuples queued up before Storm puts a Tick Tuple.
    */
    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config config = new Config();
        config.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 60);
        return config;
    }

    // It's like an init() method of a servlet It is called only once when an instance of this bolt is created
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
        if(isTickTuple(input)) {
            // take some periodic action like putting in-memory result to database etc.
            return;
        }

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

    /*
    Spout and Bolt can read to multiple streams.
    Storm has inbuilt stream (system stream) and a component (system component) that can emit a Tick Tuple.

    To check whether arrived tuple is a Tick Tuple, you just need to check its source stream and component.
     */
    private boolean isTickTuple(Tuple input) {
        String sourceComponent = input.getSourceComponent();
        String sourceStreamId = input.getSourceStreamId();
        return sourceComponent.equals(Constants.SYSTEM_COMPONENT_ID)
                && sourceStreamId.equals(Constants.SYSTEM_TICK_STREAM_ID);
    }
}

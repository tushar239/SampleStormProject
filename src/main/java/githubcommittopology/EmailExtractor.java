package githubcommittopology;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

/*
If you use BaseRichBolt, you need to anchor, ack/fail a tuple.
If you use BaseBasicBolt, Storm anchors and acks/fails a tuple implicitly.

 */
public class EmailExtractor extends BaseBasicBolt {
    /*
    Indicates the bolt emits a tuple with field named 'email'
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("email"));
    }

    /*
    Gets called when a tuple has been sent (emitted) to this bolt.
     */
    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        if(input == null || input.getFields() == null || input.getFields().size() == 0) {
            System.out.println("input looks empty");
        } else {
            if(input.contains("commit")) {
                //System.out.println("input: " + input.toString());
                // extracts the value for the field named 'commit'
                String commit = input.getStringByField("commit");
                String[] split = commit.split(" ");
                // emits a new tuple containing the field 'email'
                collector.emit(new Values(split[1]));
/*
                Optional.ofNullable(commit)
                        .map(com -> com.split(" ")[1])
                        .ifPresent(emailId -> collector.emit(new Values(emailId)));*/
            }
        }
    }
}

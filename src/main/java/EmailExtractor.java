import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

/**
 * @author Tushar Chokshi @ 12/21/16.
 */
public class EmailExtractor extends BaseBasicBolt {
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("email"));
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        if(input == null || input.getFields() == null || input.getFields().size() == 0) {
            System.out.println("input looks empty");
        } else {
            if(input.contains("commit")) {
                //System.out.println("input: " + input.toString());
                String commit = input.getStringByField("commit");
                String[] split = commit.split(" ");
                collector.emit(new Values(split[1]));
/*
                Optional.ofNullable(commit)
                        .map(com -> com.split(" ")[1])
                        .ifPresent(emailId -> collector.emit(new Values(emailId)));*/
            }
        }
    }
}

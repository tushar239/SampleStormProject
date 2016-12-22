import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Tushar Chokshi @ 12/21/16.
 */
public class CommitFieldListener extends BaseRichSpout {
    private SpoutOutputCollector outputCollector;
    private List<String> commits;

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("commit"));
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {

        System.out.println("Initializing Spout.....");
        this.outputCollector = collector;


        try (Stream<String> stream = Files.lines(Paths.get(ClassLoader.getSystemResource("changelog.txt").toURI()))) {
            commits = stream.collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void nextTuple() {
        //System.out.println("emitting next commit...");

//        outputCollector.emit(new Values(commits.get(0)));

        for (String commit : commits) {
            //System.out.println(commit);
            outputCollector.emit(new Values(commit));
        }

        /*Optional.ofNullable(commits)
                .ifPresent(coms -> coms.stream()
                        .forEach(com ->
                                outputCollector.emit(new Values(com))));*/
    }
}

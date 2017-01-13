package githubcommittopology;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*

Spout can extend either BaseRichSpout or extend its interface IRichSpout.
BaseRichSpout provides default functionality of acknowledgement.
If you use IRichSpout,
you need to override ack(), fail() etc methods and you explicitly need to somehow retry/reject a message like RabbitMQSpout.
If a tuple is anchored properly in bolt(s), failure of a tuple in bolt will be messaged back to a spout and it will call fail() method of a spout.
*/
public class CommitFieldListener extends BaseRichSpout {
    private SpoutOutputCollector outputCollector;
    private List<String> commits;

    /*
    It indicates that this spout will emit a tuple with field(s) with name 'commit'
    It can declare multiple output fields in a tuple.
    Tuple is just an ordered list of values. You can provide names to those values.
        Tuple=[{name1=value1}, {name2=value2}, {name3=value3}]
    Names are not really passed inside tuple, only values are passed.
    */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("commit"));
    }

    /*
    It's like an init() method of a Servlet. It gts called when Storm prepares the spout to be run.
     */
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {

        System.out.println("Initializing Spout.....");
        this.outputCollector = collector;

/*

        String current = null;
        try {
            current = new File( "." ).getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Current dir:"+current);
*/

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
        commits = lines.stream().collect(Collectors.toList());
/*
        try (Stream<String> stream = Files.lines(Paths.get(ClassLoader.getSystemResource("changelog.txt").toURI()))) {
            commits = stream.collect(Collectors.toList());
        } catch (Exception e) {
            try(Stream<String> stream = Files.lines(Paths.get("/Users/chokst/MavenizedProjectEclipseWSNew/SampleStormProject/src/main/resources/changelog.txt"))) {
                commits = stream.collect(Collectors.toList());
            } catch(Exception e1){
                throw new RuntimeException(e1);
            }
        }*/
    }

    /*
    This method is called by Storm when it's ready to read the next tuple for the spout.
     */
    @Override
    public void nextTuple() {
        //System.out.println("emitting next commit...");

//        outputCollector.emit(new Values(commits.get(0)));

        for (String commit : commits) {
            //System.out.println(commit);

            // emits a tuple for each commit message
            outputCollector.emit(new Values(commit));
        }

        /*Optional.ofNullable(commits)
                .ifPresent(coms -> coms.stream()
                        .forEach(com ->
                                outputCollector.emit(new Values(com))));*/
    }
}

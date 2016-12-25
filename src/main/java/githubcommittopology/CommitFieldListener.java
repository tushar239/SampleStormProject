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

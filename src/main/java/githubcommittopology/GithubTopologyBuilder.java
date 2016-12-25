package githubcommittopology;

import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/**
 * @author Tushar Chokshi @ 12/24/16.
 */
public class GithubTopologyBuilder {

    public static StormTopology build(Config config) {

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder
                .setSpout("commit-feed-listener", new CommitFieldListener());

        topologyBuilder
                .setBolt("email-extractor", new EmailExtractor())
                .shuffleGrouping("commit-feed-listener");

        topologyBuilder
                .setBolt("email-counter", new EmailCounter())
                .fieldsGrouping("email-extractor", new Fields("email"));


        StormTopology topology = topologyBuilder.createTopology();

        return topology;

    }
}

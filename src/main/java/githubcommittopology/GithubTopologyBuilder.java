package githubcommittopology;

import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/*

Spout and Bolts are main components of Storm Topology.

Github commits topology
-----------------------

Spout - CommitFieldListener - reads changelog.txt file and emits the lines of it (in the form of tuples) to next component(email-extractor)
Bolt - EmailExtractor - accepts tuples from spout and extracts email id from each tuple and emits those emaild id in the form of new tuples to next component (EmailCounter)
Bolt - EmailCounter - accepts tuples from EmailExtractor bolt and keeps a count of same email ids in memory

 */
public class GithubTopologyBuilder {

    // Config class is used for defining topology-level configuration
    public static StormTopology build(Config config) {

        // This class is used to piece together spouts an bolts, defining the streams and stream groupings between them.
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder
                .setSpout("commit-feed-listener", new CommitFieldListener());

        topologyBuilder
                .setBolt("email-extractor", new EmailExtractor())
                // adding shuffle grouping between a spout and this bolt
                .shuffleGrouping("commit-feed-listener");

        topologyBuilder
                .setBolt("email-counter", new EmailCounter())
                // adding field grouping between a spout and this bolt
                // Why field grouping?
                // you want same email id go to the same instance of email-counter bolt
                .fieldsGrouping("email-extractor", new Fields("email"));


        StormTopology topology = topologyBuilder.createTopology();

        return topology;

    }
}

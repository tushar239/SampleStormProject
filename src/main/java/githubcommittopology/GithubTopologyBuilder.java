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

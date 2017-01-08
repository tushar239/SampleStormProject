package stormkafkaintegration;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.utils.Utils;

/*
Running Storm Topology on Local Cluster using localCluster.submitTopology

Storm UI: localhost:8080/index.html
 */
public class StormKafkaTopologyRunner {
    private static final int TEN_MINUTES = 600000;
    private static final String TOPOLOGY_NAME = "storm-kafka-integration-github-commit-spout-topology";

    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {

        Config config = new Config();
        //config.setDebug(true);

        StormTopology topology = StormKafkaIntegrationTopologyBuilder.build(config);

        // Running topology in local cluster
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(TOPOLOGY_NAME, config, topology);

        Utils.sleep(TEN_MINUTES);

        cluster.killTopology(TOPOLOGY_NAME);
        cluster.shutdown();

        // Running topology on remote cluster
        //System.setProperty("storm.jar", "/Users/chokst/apache-storm-1.0.2/lib/storm-core-1.0.2.jar");
        //StormSubmitter.submitTopology(TOPOLOGY_NAME, config, topology);

    }
}

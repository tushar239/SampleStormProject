package stormrabbitmqintegration;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.utils.Utils;

/**
 * @author Tushar Chokshi @ 1/9/17.
 */
public class StormRabbitMQTopologyRunner {
    private static final int TEN_MINUTES = 600000;
    private static final String TOPOLOGY_NAME = "storm-rabbitmq-integration-github-commit-spout-topology";

    public static void main(String[] args) {

        Config config = new Config();
        //config.setDebug(true);

        StormTopology topology = StormRabbitMQIntegrationTopologyBuilder.build(config);

        // Running topology in local cluster
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(TOPOLOGY_NAME, config, topology);

        Utils.sleep(TEN_MINUTES);

        cluster.killTopology(TOPOLOGY_NAME);
        cluster.shutdown();

        // Running topology on remote cluster
        // You will see a topology in Storm UI, only if you run it on Remote Cluster
        //System.setProperty("storm.jar", "/Users/chokst/apache-storm-1.0.2/lib/storm-core-1.0.2.jar");
        //StormSubmitter.submitTopology(TOPOLOGY_NAME, config, topology);
    }



}

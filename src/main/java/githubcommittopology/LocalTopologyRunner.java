package githubcommittopology;

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
public class LocalTopologyRunner {
    private static final int TEN_MINUTES = 600000;
    private static final String TOPOLOGY_NAME = "github-commit-spout-topology";

    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {

        // Config class is used for defining topology-level configuration
        Config config = new Config();
        //config.setDebug(true);

        // StormTopology class is what TopologyBuilder builds and is what's submitted to the cluster to be run.
        StormTopology topology = GithubTopologyBuilder.build(config);


        // LocalCluster simulates a Storm cluster in-process on our local machine allowing us to easily run our topologies for testing purposes.
        LocalCluster cluster = new LocalCluster();
        // submits the topology and configuration to the local cluster
        cluster.submitTopology(TOPOLOGY_NAME, config, topology);

        // Adding some sleep time, so that topology runs for some time before it is killed in next step.
        // This gives it some time to process some tuples.
        Utils.sleep(TEN_MINUTES);

        // kills the topology
        // remember, topology never stops, it keeps running by default. You need to kill it if you want to stop.
        // In case of Remote Cluster deployment, you can kill it from Storm UI, but for Local Cluster, there is not Storm UI, so you need to kill it from the code.
        // Normally, You do not kill a running topology in prod.
        cluster.killTopology(TOPOLOGY_NAME);
        // shutdown local cluster
        cluster.shutdown();

    }
}

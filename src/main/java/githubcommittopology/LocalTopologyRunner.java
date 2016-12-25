package githubcommittopology;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.utils.Utils;

/**
 * @author Tushar Chokshi @ 12/21/16.
 */
public class LocalTopologyRunner {
    private static final int TEN_MINUTES = 600000;

    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {

        Config config = new Config();
        //config.setDebug(true);

        StormTopology topology = GithubTopologyBuilder.build(config);


        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("github-commit-spout-topology", config, topology);

        Utils.sleep(TEN_MINUTES);

        cluster.killTopology("github-commit-spout-topology");
        cluster.shutdown();

    }
}

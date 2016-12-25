package githubcommittopology;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;

/**
 * @author Tushar Chokshi @ 12/24/16.
 */
public class RemoteTopologyRunner {
    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        Config config = new Config();
        //config.setDebug(true);

        StormTopology topology = GithubTopologyBuilder.build(config);

        //System.setProperty("storm.jar", "/Users/chokst/apache-storm-1.0.2/lib/storm-core-1.0.2.jar");
        StormSubmitter.submitTopology("github-commit-spout-topology", config, topology);

    }
}

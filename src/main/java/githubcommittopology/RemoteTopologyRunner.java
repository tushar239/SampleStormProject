package githubcommittopology;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;

/*
Running Storm Topology on Remote Cluster using StormTopology.submitTopology

and

Run below command to install a jar file in storm cluster
<storm dir>/bin/storm jar <jar file location> <fully named class name that submits a topology>
e.g.
<storm dir>/bin/storm jar /Users/chokst/MavenizedProjectEclipseWSNew/SampleStormProject/target/sample.storm.project-1.0-SNAPSHOT.jar githubcommittopology/RemoteTopologyRunner

Storm UI: localhost:8080/index.html
 */

public class RemoteTopologyRunner {
    private static final String TOPOLOGY_NAME = "github-commit-spout-topology";

    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        Config config = new Config();
        //config.setDebug(true);

        StormTopology topology = GithubTopologyBuilder.build(config);

        //System.setProperty("storm.jar", "/Users/chokst/apache-storm-1.0.2/lib/storm-core-1.0.2.jar");
        StormSubmitter.submitTopology(TOPOLOGY_NAME, config, topology);

    }
}

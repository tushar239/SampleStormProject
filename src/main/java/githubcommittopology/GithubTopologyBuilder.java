package githubcommittopology;

import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.metric.LoggingMetricsConsumer;
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

        // Metrics are used in EmailCounter bolt. Consumer of those metrics are registered here.
        // There are 3rd party tools like Statsd, Riemann available as Metrics Consumer.
        // Here, we have used Storm's in-built LoggingMetricsConsumer that will log the metrics in stdout in LocalCluster. In RemoteCluster, it will write to metric.log under Storm's log directory.
        // You can also set this config in storm.yaml
        // topology.metrics.consumer.register: "org.apache.storm.metrics.LoggingMetricsConsumer"

        // 217324 [Thread-16-__metricsorg.apache.storm.metric.LoggingMetricsConsumer-executor[2 2]] INFO  o.a.s.m.LoggingMetricsConsumer - 1484336694	sea-chokst-m4.ds.ad.adp.com:1024	 11:email-extractor	__execute-count        	{commit-feed-listener:default=62780}
        // 197853 [Thread-16-__metricsorg.apache.storm.metric.LoggingMetricsConsumer-executor[2 2]] INFO  o.a.s.m.LoggingMetricsConsumer - 1484336694	sea-chokst-m4.ds.ad.adp.com:1024	 11:email-extractor	__emit-count           	{default=62800, __metrics=0}
        // 197855 [Thread-16-__metricsorg.apache.storm.metric.LoggingMetricsConsumer-executor[2 2]] INFO  o.a.s.m.LoggingMetricsConsumer - 1484336694	sea-chokst-m4.ds.ad.adp.com:1024	 11:email-extractor	__process-latency      	{commit-feed-listener:default=1.3544585987261146}
        // 197857 [Thread-16-__metricsorg.apache.storm.metric.LoggingMetricsConsumer-executor[2 2]] INFO  o.a.s.m.LoggingMetricsConsumer - 1484336694	sea-chokst-m4.ds.ad.adp.com:1024	 11:email-extractor	__receive              	{arrival_rate_secs=4.856333468231485, overflow=1, read_pos=145863, write_pos=145867, sojourn_time_ms=823.6666666666667, capacity=1024, population=4}
        // 197857 [Thread-16-__metricsorg.apache.storm.metric.LoggingMetricsConsumer-executor[2 2]] INFO  o.a.s.m.LoggingMetricsConsumer - 1484336694	sea-chokst-m4.ds.ad.adp.com:1024	 11:email-extractor	__ack-count            	{commit-feed-listener:default=62800}
        // 197857 [Thread-16-__metricsorg.apache.storm.metric.LoggingMetricsConsumer-executor[2 2]] INFO  o.a.s.m.LoggingMetricsConsumer - 1484336694	sea-chokst-m4.ds.ad.adp.com:1024	 11:email-extractor	__transfer-count       	{default=62800, __metrics=0}
        // 197857 [Thread-16-__metricsorg.apache.storm.metric.LoggingMetricsConsumer-executor[2 2]] INFO  o.a.s.m.LoggingMetricsConsumer - 1484336694	sea-chokst-m4.ds.ad.adp.com:1024	 11:email-extractor	__execute-latency      	{commit-feed-listener:default=0.0022300095571838167}
        // 197857 [Thread-16-__metricsorg.apache.storm.metric.LoggingMetricsConsumer-executor[2 2]] INFO  o.a.s.m.LoggingMetricsConsumer - 1484336694	sea-chokst-m4.ds.ad.adp.com:1024	 11:email-extractor	__fail-count           	{}

        config.registerMetricsConsumer(LoggingMetricsConsumer.class);

        // This class is used to piece together spouts an bolts, defining the streams and stream groupings between them.
        TopologyBuilder topologyBuilder = new TopologyBuilder();

        topologyBuilder
                .setSpout("commit-feed-listener", new CommitFieldListener(), 1) // setting number of executors(threads) to 3. Executor is just a thread that supplies tuple to the tasks. This can be increased dynamically till number of tasks (Chapter 6 of Storm_Applied book). So, it's better to keep number of tasks a bit higher, so that you can adjust number of executors dynamically later on, if needed.
                //.addConfiguration(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS, 60) // If a tuple is acked within 60 seconds, then mark it a failed. Default value is 30 seconds.
                //.setMaxSpoutPending(250) // this number allows you to set max number of tuples that can be unacked(success/failure) at any given time. Even though it is optional, it is recommended to always set it. It's value should be kept higher than number of total tasks of all bolts. Chapter 6 of Storm_Applied book explains it very well.
                .setNumTasks(3); // setting number of tasks (instances of spout) to 3


        topologyBuilder
                .setBolt("email-extractor", new EmailExtractor(), 3)
                // adding shuffle grouping between a spout and this bolt
                .shuffleGrouping("commit-feed-listener")
                .setNumTasks(6);

        topologyBuilder
                // Here, you have defined field grouping.
                // Shuffle Grouping is the best and Global Grouping is the worst.
                // In Global Grouping, increasing number of executors and tasks are not going to give any benefit because all the tuples will be sent to the same task.
                // Field Grouping has a problem too. You cannot change the number of executors/tasks later on. If you do that hashing will be changed and tuple with specific hashcode that used to go to task-x might start going to task-y.
                .setBolt("email-counter", new EmailCounter(), 3)
                .setNumTasks(3)
                // you can add component level configuration like this or inside the class. See EmailCounter bolt class. It has 'getComponentConfiguration' method.
                //.addConfiguration(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 60)

                // adding field grouping between a spout and this bolt
                // Why field grouping?
                // you want same email id go to the same instance of email-counter bolt
                .fieldsGrouping("email-extractor", new Fields("email"));


        StormTopology topology = topologyBuilder.createTopology();

        return topology;

    }
}

package stormkafkaintegration;

import githubcommittopology.EmailCounter;
import githubcommittopology.EmailExtractor;
import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

import static stormkafkaintegration.SimpleKafkaProducer.TOPIC;

/*
https://www.tutorialspoint.com/apache_kafka/apache_kafka_integration_storm.htm
http://storm.apache.org/releases/2.0.0-SNAPSHOT/storm-kafka.html
*/
public class StormKafkaIntegrationTopologyBuilder {
    public static StormTopology build(Config config) {

        TopologyBuilder topologyBuilder = new TopologyBuilder();


        // KafkaSpout is provided by storm-kafka dependency. You don't need to create your own spout.
        topologyBuilder
                .setSpout("kafka-spout", createKafkaSpout());

        topologyBuilder
                .setBolt("email-extractor", new EmailExtractor())
                .shuffleGrouping("kafka-spout");

        topologyBuilder
                .setBolt("email-counter", new EmailCounter())
                .fieldsGrouping("email-extractor", new Fields("email"));


        StormTopology topology = topologyBuilder.createTopology();

        return topology;

    }

    protected static KafkaSpout createKafkaSpout() {

        // Retrieving kafka brokers information from zookeeper
        BrokerHosts brokerHosts = new ZkHosts("localhost:2181");

        String topic = TOPIC;
        String zkRoot = "/"+topic; // The Zkroot will be used as root to store your consumer's offset.
        String spoutId="storm-kafka-integration-spout"; // May be it will be kafka consumer group id also

        SpoutConfig kafkaSpoutConfig = new SpoutConfig(brokerHosts, topic, zkRoot, spoutId);
        kafkaSpoutConfig.bufferSizeBytes = 1024 * 1024 * 4;
        kafkaSpoutConfig.fetchSizeBytes = 1024 * 1024 * 4;
        //kafkaSpoutConfig.forceFromStart = true;


        // Kafka has data in bytes format. You need to use deserializer to convert that bytes to appropriate form. StringScheme is that deserializer that converts bytes to String.
        kafkaSpoutConfig.scheme = new SchemeAsMultiScheme(new MyStringScheme());


        return new KafkaSpout(kafkaSpoutConfig);
    }


}

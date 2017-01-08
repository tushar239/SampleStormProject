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

import java.util.UUID;

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

        SpoutConfig kafkaSpoutConfig = new SpoutConfig(brokerHosts, topic, "/" + topic, UUID.randomUUID().toString());
        kafkaSpoutConfig.bufferSizeBytes = 1024 * 1024 * 4;
        kafkaSpoutConfig.fetchSizeBytes = 1024 * 1024 * 4;
        //kafkaSpoutConfig.forceFromStart = true;

        // Kafka has data in bytes format. You need to use deserializer to convert that bytes to appropriate form. StringScheme is that deserializer that converts bytes to String.
        kafkaSpoutConfig.scheme = new SchemeAsMultiScheme(new MyStringScheme());


        return new KafkaSpout(kafkaSpoutConfig);
    }
}

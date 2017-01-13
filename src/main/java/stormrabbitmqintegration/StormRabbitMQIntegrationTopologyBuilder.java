package stormrabbitmqintegration;

import com.rabbitmq.client.ConnectionFactory;
import githubcommittopology.EmailCounter;
import githubcommittopology.EmailExtractor;
import io.latent.storm.rabbitmq.RabbitMQSpout;
import io.latent.storm.rabbitmq.config.ConnectionConfig;
import io.latent.storm.rabbitmq.config.ConsumerConfig;
import io.latent.storm.rabbitmq.config.ConsumerConfigBuilder;
import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import stormkafkaintegration.MyStringScheme;

/*
https://github.com/ppat/storm-rabbitmq
*/
public class StormRabbitMQIntegrationTopologyBuilder {
    public static StormTopology build(Config config) {

        ConnectionConfig connectionConfig = new ConnectionConfig("localhost", 5672, "guest", "guest", ConnectionFactory.DEFAULT_VHOST, 10); // host, port, username, password, virtualHost, heartBeat
        ConsumerConfig spoutConfig =
                new ConsumerConfigBuilder()
                        .connection(connectionConfig)
                        .queue("storm.rabbitmq.int.queue")
                        .prefetch(200)
                        .requeueOnFail()
                        .build();

        TopologyBuilder topologyBuilder = new TopologyBuilder();

        topologyBuilder
                .setSpout("rabbitmq-spout", createRabbitMQSpout())
                .addConfigurations(spoutConfig.asMap())
                .setMaxSpoutPending(200);

        topologyBuilder
                .setBolt("email-extractor", new EmailExtractor())
                .shuffleGrouping("rabbitmq-spout");

        topologyBuilder
                .setBolt("email-counter", new EmailCounter())
                .fieldsGrouping("email-extractor", new Fields("email"));


        StormTopology topology = topologyBuilder.createTopology();

        return topology;

    }

    protected static RabbitMQSpout createRabbitMQSpout() {

        // MyStringScheme extends StringScheme.
        // You are telling RabbitMQSpout to convert byte message polled from queue into String.
        // RabbitMQSpout calls a consumer class that polls a message from a queue.
        return new RabbitMQSpout(new MyStringScheme());
    }


}

- make sure following process are running
    - As mentioned in githubcommittopology/readme.txt
        zookeeper
        storm nimbus
        storm supervisor
        storm ui

    - kafka brokers
        kafka> bin/kafka-server-start.sh config/server.properties
        you can use kafka tool (http://www.kafkatool.com/) to see broker/partition/messages etc.

- run StormKafkaTopologyRunner - this will deploy a topology on LocalCluster

- run SimpleKafkaProducer - this will put messages in kafka that is read by topology's KafkaSpout configured in StormKafkaIntegrationTopologyBuilder.
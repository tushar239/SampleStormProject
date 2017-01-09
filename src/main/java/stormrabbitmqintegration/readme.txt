- make sure following process are running
    - As mentioned in githubcommittopology/readme.txt
        zookeeper
        storm nimbus
        storm supervisor
        storm ui

    - rabbitmq server
        rabbitmq> sbin/rabbitmq-server

storm ui - localhost:8080/index.html --- if you have deployed a topology on LocalCluster, then your topology won't show up on ui
rabbitmq ui - localhost:15672 ---- username:guest password:guest

- run StormRabbitMQTopologyRunner - this will deploy a topology on LocalCluster

- run RabbitMQMessageProducer - this will put messages in queue that is read by topology's RabbitMQSpout configured in StormRabbitMQIntegrationTopologyBuilder.
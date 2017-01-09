- make sure following process are running
zookeeper
    zookeeper> bin/zkServer.sh start
    zookeeper> bin/zkCli.sh  ---  it is just for zookeeper command line shell (no need to do it)
storm nimbus
    storm> bin/storm nimbus
storm supervisor
    storm> bin/storm supervisor
storm ui
    storm> bin/storm ui
rabbitmq server
    rabbitmq> sbin/rabbitmq-server

storm ui - localhost:8080/index.html --- if you have deployed a topology on LocalCluster, then your topology won't show up on ui
rabbitmq ui - localhost:15672 ---- username:guest password:guest

- run StormRabbitMQTopologyRunner - this will deploy a topology on LocalCluster

- run RabbitMQMessageProducer - this will put messages in queue that is read by topology's RabbitMQSpout configured in StormRabbitMQIntegrationTopologyBuilder.
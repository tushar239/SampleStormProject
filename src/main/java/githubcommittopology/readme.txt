1. How to run a topology using LocalCluster?

run LocalTopologyRunner.

2. How to submit a topology to a REMOTE cluster?

- Creating a storm cluster in your local
https://www.tutorialspoint.com/apache_storm/apache_storm_installation.htm

make sure following processes are running as mentioned in above link.
    - zookeeper
    - nimbus
    - supervisor
    - storm ui

- Do mvn install to create "sample.storm.project-1.0-SNAPSHOT.jar" under target folder.
- Run below command to install a jar file in storm cluster
<storm dir>/bin/storm jar <jar file location> <fully named class name that submits a topology>
e.g.
<storm dir>/bin/storm jar /Users/chokst/MavenizedProjectEclipseWSNew/SampleStormProject/target/sample.storm.project-1.0-SNAPSHOT.jar githubcommittopology/RemoteTopologyRunner

- go to storm ui using "localhost:8080/index.html"
you will see all submitted topologies listed.
you can click a topology to see its components, status
you can deactivate/activate/kill(undeploy) a topology from this UI.
It will show you how many tuples have been emitted, acknowledged, failed etc at spout level, each bolt level.


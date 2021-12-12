# Scala Cassandra Update Streaming

Codebase exploring how to read the contents of a Cassandra DB, and then update records based on a simple condition.

This example is based off of the Baeldung "videos" example linked below. My extension of this is simply to read in the 
contents of the videos table in Cassandra, convert the video titles to lowercase and write them back to the same table (upsert).

### Instructions

1. Start up Cassandra by running `./docker/vanilladockercassandra.sh`from the root of this project.
2. Wait about 20 seconds (until Cassandra is ready)*.
3. Run [Main](src/main/scala/Main.scala) from IntelliJ.

[*] I'm experiencing some sbt issues at the moment, this will be handled in a more elegant manner via the docker-compose healthcheck api or similar.

### Sources

* https://www.baeldung.com/cassandra-datastax-java-driver
* https://docs.datastax.com/en/developer/java-driver/4.13/manual/query_builder/insert/
* https://doc.akka.io/docs/alpakka/current/cassandra.html
* https://doc.akka.io/docs/akka/current/stream/stream-cookbook.html
* https://blog.knoldus.com/streaming-data-from-cassandra-using-alpakka/
* https://blog.colinbreck.com/maximizing-throughput-for-akka-streams/

# Scala Cassandra Update Streaming

Codebase exploring how to read the contents of a Cassandra DB, and then update records based on a simple condition.

This example is based off of the Baeldung "videos" example linked below. My extension of this is simply to read in the 
contents of the videos table in Cassandra, convert the video titles to lowercase and write them back to the same table (upsert).

### Sources

* https://www.baeldung.com/cassandra-datastax-java-driver
* https://docs.datastax.com/en/developer/java-driver/4.13/manual/query_builder/insert/
* https://doc.akka.io/docs/alpakka/current/cassandra.html
* https://doc.akka.io/docs/akka/current/stream/stream-cookbook.html
* https://blog.knoldus.com/streaming-data-from-cassandra-using-alpakka/
* https://blog.colinbreck.com/maximizing-throughput-for-akka-streams/

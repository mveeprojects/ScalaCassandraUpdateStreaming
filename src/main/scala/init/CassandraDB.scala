package init

import akka.Done
import com.datastax.oss.driver.api.core.`type`.DataTypes
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.datastax.oss.driver.api.core.{CqlIdentifier, CqlSession}
import com.datastax.oss.driver.api.querybuilder.QueryBuilder.{insertInto, literal}
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace
import config.AppConfig.appConfig.cassandra._
import model.Video
import utils.Logging

import java.net.InetSocketAddress
import java.time.Instant
import scala.util.{Failure, Success, Try}

object CassandraDB extends Logging {

  private def openDBInitSession: (String, Int, String) => CqlSession = (node: String, port: Int, datacentre: String) =>
    CqlSession.builder
      .addContactPoint(new InetSocketAddress(node, port))
      .withLocalDatacenter(datacentre)
      .build

  private def closeDBInitSession(session: CqlSession): Unit = {
    logger.info("Closing DB initialisation session.")
    session.close()
  }

  private lazy val session: CqlSession = openDBInitSession(host, port, datacentre)

  private val exampleVideoDate = List(
    Video("Mark", "1234", "top gun", Instant.now),
    Video("Sally", "1234", "TOP GUN", Instant.now)
  )

  def init(): Either[String, Done] = {
    logger.info("Configuring Keyspace and Schema in Cassandra...")
    Try {
      createKeyspaceIfNotExists()
      useKeyspace()
      createTableIfNotExists()
      exampleVideoDate.foreach(insertIntoCassandra)
    } match {
      case Success(_) =>
        logger.info("DB initialisation completed successfully.")
        closeDBInitSession(session)
        Right(Done)
      case Failure(exception) =>
        logger.error(s"Exception thrown during DB initialisation => ${exception.getMessage}")
        closeDBInitSession(session)
        Left("DB is toast.")
    }
  }

  private def insertIntoCassandra(video: Video): Unit = {
    val insert: RegularInsert = insertInto("video")
      .value("userid", literal(video.userId))
      .value("videoid", literal(video.videoId))
      .value("title", literal(video.title))
      .value("creationdate", literal(video.creationDate))
    session.execute(insert.build())
  }

  private def createKeyspaceIfNotExists(): Unit = {
    val cks: CreateKeyspace = SchemaBuilder
      .createKeyspace(keyspace)
      .ifNotExists
      .withSimpleStrategy(replicas)
    session.execute(cks.build)
  }

  private def useKeyspace(): Unit =
    session.execute("USE " + CqlIdentifier.fromCql(keyspace))

  private def createTableIfNotExists(): Unit = {
    val statement: SimpleStatement = SchemaBuilder
      .createTable(tablename)
      .ifNotExists
      .withPartitionKey("userId", DataTypes.TEXT)
      .withClusteringColumn("videoId", DataTypes.TEXT)
      .withColumn("title", DataTypes.TEXT)
      .withColumn("creationDate", DataTypes.TIMESTAMP)
      .build
    session.execute(statement)
  }
}

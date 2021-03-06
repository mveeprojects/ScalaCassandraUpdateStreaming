import akka.NotUsed
import akka.stream.alpakka.cassandra.CassandraWriteSettings
import akka.stream.alpakka.cassandra.scaladsl.{CassandraFlow, CassandraSource}
import akka.stream.scaladsl.{Flow, Sink}
import com.datastax.oss.driver.api.core.cql.{Row, SimpleStatement}
import config.AlpakkaCassandra
import config.AppConfig._
import model.Video
import init.CassandraDB
import utils.Logging

object Main extends App with AlpakkaCassandra with Logging {

  CassandraDB.init() match {
    case Left(msg) =>
      logger.error(msg)
      sys.exit(1)
    case Right(_) =>
      logger.info("Cassandra ready.")
  }

  private val selectStmt =
    SimpleStatement.newInstance(s"SELECT * FROM ${appConfig.cassandra.keyspace}.video").setPageSize(20)

  private val transformRowToVideo: Row => Video = (row: Row) =>
    Video(
      row.getString("userid"),
      row.getString("videoid"),
      row.getString("title"),
      row.getInstant("creationdate")
    )

  private val convertTitleToLowercase: Video => Video = (video: Video) =>
    video.copy(
      title = video.title.toLowerCase
    )

  private val alpakkaInsertFlow: Flow[Video, Video, NotUsed] = CassandraFlow.create(
    CassandraWriteSettings.defaults,
    s"INSERT INTO ${appConfig.cassandra.keyspace}.video(userid, videoid, title, creationdate) VALUES(?, ?, ?, ?)",
    insertStatementBinder
  )

  CassandraSource(selectStmt)
    .via(Flow[Row].map(transformRowToVideo))
    .async
    .via(Flow[Video].map(convertTitleToLowercase))
    .via(alpakkaInsertFlow)
    .runWith(Sink.foreach(println))
}

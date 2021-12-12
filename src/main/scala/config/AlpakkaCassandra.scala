package config

import akka.stream.alpakka.cassandra.CassandraSessionSettings
import akka.stream.alpakka.cassandra.scaladsl.{CassandraSession, CassandraSessionRegistry}
import com.datastax.oss.driver.api.core.cql.{BoundStatement, PreparedStatement}
import config.AppConfig.system

trait AlpakkaCassandra {

  lazy val sessionSettings: CassandraSessionSettings = CassandraSessionSettings()

  implicit val cassandraSession: CassandraSession =
    CassandraSessionRegistry.get(system).sessionFor(sessionSettings)

  val insertStatementBinder: (model.Video, PreparedStatement) => BoundStatement =
    (video, preparedStatement) => preparedStatement.bind(video.userId, video.videoId, video.title, video.creationDate)
}

package config

import pureconfig.ConfigSource
import pureconfig.generic.auto._

object AppConfig extends ActorSystemConfig {

  case class CassandraConfig(
      host: String,
      port: Int,
      datacentre: String,
      keyspace: String,
      replicas: Int,
      tablename: String,
      preparedstatementcache: Int
  )

  case class Config(cassandra: CassandraConfig)

  val appConfig: Config = ConfigSource.default.loadOrThrow[Config]
}

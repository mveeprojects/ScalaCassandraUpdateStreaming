import Dependencies._

name := "ScalaCassandraUpdateStreaming"

version := "0.1"

ThisBuild / scalaVersion := "2.13.7"

libraryDependencies ++= (
  cassandraDependencies ++
    loggingDependencies ++
    configDependencies
)

enablePlugins(JavaAppPackaging, DockerComposePlugin)
dockerImageCreationTask := (Docker / publishLocal).value

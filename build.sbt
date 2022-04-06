ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.12"

lazy val root = (project in file("."))
  .settings(
    name := "flink-dmn-integration"
  )

libraryDependencies += "org.apache.flink" %% "flink-clients" % "1.12.0"
libraryDependencies += "org.apache.flink" %% "flink-streaming-scala" % "1.12.0"
libraryDependencies += "org.apache.flink" %% "flink-runtime-web" % "1.12.0"
libraryDependencies += "org.camunda.bpm.dmn" % "camunda-engine-dmn" % "7.15.0"
libraryDependencies += "io.circe" %% "circe-parser" % "0.14.1"
libraryDependencies += "io.circe" %% "circe-generic" % "0.14.1"
libraryDependencies += "org.apache.flink" %% "flink-connector-kafka" % "1.12.0"

libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5")


libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test


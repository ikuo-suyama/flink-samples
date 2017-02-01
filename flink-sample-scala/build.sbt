name := "flink-sample-scala"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val flinkVersion = "1.1.4"
  Seq(
    // Flink
    "org.apache.flink"             %% "flink-clients"                      % flinkVersion,
    "org.apache.flink"             %% "flink-scala"                        % flinkVersion,
    "org.apache.flink"             %% "flink-streaming-scala"              % flinkVersion,

    // Flink twitter connector
    "org.apache.flink"             %% "flink-connector-twitter"            % flinkVersion,
    // Flink Kafka Connecto
    "org.apache.flink"             %% "flink-connector-kafka-0.8"          % flinkVersion,

    // Jackson
    "com.fasterxml.jackson.core"    % "jackson-databind"                   % "2.8.4",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"               % "2.8.4",

    // Twitter4s
    "com.danielasfregola"          %% "twitter4s"                          % "3.0",

    // Typesafe Config
    "com.typesafe"                  % "config"                             % "1.3.1",

    // testing
    "org.specs2"                   %% "specs2-core"                      % "2.4.15"         % "test",
    "org.specs2"                   %% "specs2-mock"                      % "2.4.15"         % "test"

  )
}

assemblyJarName in assembly := "sandbox_v0.1.jar"

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".xml" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".types" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
  case "application.conf"                            => MergeStrategy.concat
  case "unwanted.txt"                                => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

mainClass in assembly := Some("samples.twitter.TwitterConversationToKafka")

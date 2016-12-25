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
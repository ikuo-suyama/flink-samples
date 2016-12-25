Apache Flinkことはじめ

## Document
https://flink.apache.org/downloads.html

Versionによってとっちらかっていて読みづらい。
1.2はSnapShotだが読みやすい。。

[v1.1系] (https://ci.apache.org/projects/flink/flink-docs-release-1.1/)
[v1.2系] (https://ci.apache.org/projects/flink/flink-docs-release-1.2/)

## DataStructure
DataSourceからDataStreamを作って、mapしたりfilfterしたりしたあとSinkする、が基本。

APIリファレンスは以下。
https://ci.apache.org/projects/flink/flink-docs-release-1.1/apis/streaming/index.html

集計したりWindowをあてたりする事ができるが、該当の関数をかますと方が変わるので注意。
以下の図がわかりやすい。

![](https://cwiki.apache.org/confluence/download/attachments/58852962/streams.png?version=1&modificationDate=1435767286000&api=v2)
(https://cwiki.apache.org/confluence/display/FLINK/Streams+and+Operations+on+Streams)

集計処理の基本は

DataStream -(keyBy)-> KeyedDtaStream -(window)-> WindowDataStream -(aggregation)-> DataStream

こんな感じになる。

> NOTE: This document is a design draft and does not reflect the state of a stable release, but the work in progress on the current master, and future considerations.

とか書いてあるけど、v1.3でほぼ実現されている模様


## StreamingでWordCountを動かす

### build.sbt

```scala
name := "flink-sample-scala"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  Seq(
    // Flink
    "org.apache.flink"           %% "flink-clients"                      % "1.1.4",
    "org.apache.flink"           %% "flink-scala"                        % "1.1.4",
    "org.apache.flink"           %% "flink-streaming-scala"              % "1.1.4",

    // testing
    "org.specs2"                 %% "specs2-core"                      % "2.4.15"         % "test",
    "org.specs2"                 %% "specs2-mock"                      % "2.4.15"         % "test"

  )
}
```

ExampleProgramをコピペ
https://ci.apache.org/projects/flink/flink-docs-release-1.1/apis/streaming/index.html

```scala
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time

object WindowWordCount {

  def main(args: Array[String]) {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.socketTextStream("localhost", 9999)

    val counts = text.flatMap { _.toLowerCase.split("\\W+") filter { _.nonEmpty } }
      .map { (_, 1) }
      .keyBy(0)
      .timeWindow(Time.seconds(5))
      .sum(1)

    counts.print

    env.execute("Window Stream WordCount")
  }

}
```


Terminalで

```sh
$ nc -lk 9999
```

して9999ポートを開いた後、InteliJでWindowWordCountを実行

```
12/25/2016 11:39:29	Fast TumblingProcessingTimeWindows(5000) of WindowedStream.aggregate(WindowedStream.scala:428) -> Sink: Unnamed(4/4) switched to RUNNING 
1> (fas,1)
3> (test,3)
```

こんな感じでカウントされる。

## TwitterのStreamAPIからデータを取って、加工してKafkaに保存
ソースをTwitterでやってみる。
なんとConnectorAPIがある。

https://ci.apache.org/projects/flink/flink-docs-release-1.1/apis/streaming/connectors/twitter.html

が、Scalaサンプルはぶっ壊れているので注意。

https://dev.twitter.com/streaming/reference/get/statuses/sample

実行時に以下エラーが出る時

```
Error:(23, 56) could not find implicit value for evidence parameter of type org.apache.flink.api.common.typeinfo.TypeInformation[String]
    val streamSource:DataStream[String] = env.addSource(new TwitterSource(props))
                                                       ^
```

以下のImportし忘れ

```scala
import org.apache.flink.api.scala._
```

メインの処理はだいたい以下の感じ。
getTweetByIdはReplyのIdの元Tweetを取得するためにRESTを叩く関数

```scala
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    :

    val streamSource:DataStream[String] = env.addSource(new TwitterSource(props))
    val tweetsStream = streamSource
      // deleteを削除
      .filter(! _.startsWith("""{"delete":{"""))

      // jsonのパース
      .map(jsonMapper.readValue(_, classOf[Tweet]))

      // ほしいデータでFilter
      .filter(_.lang == "ja")
      .filter(_.inReplyToStatusId isDefined)

      // retweetを取得
      .map(r =>
        r.inReplyToStatusId map { rid =>
          getTweetById(rid) match {
            case Some(t) => (t.text, r.text)
            case _ => None
          }})
      .filter(_ match {
        case Some((t, r)) => true
        case _ => false
      })
      .map(_ match {
        case Some((t, r)) => (t, r).toString
      })

    tweetsStream.print
    tweetsStream.addSink(
      new FlinkKafkaProducer08[String](
        "docker.host:9092",
        "tweet_conversation_topic",
        new SimpleStringSchema()))

    env.execute("Twitter Stream Collect Conversation")

  }
```

コードは

TwitterConversationToKafka.scala

を参照。TypesafeConfigで以下のキーが設定されてないと動かないので注意!

```
twitter.consumer.key = "please input your own"
twitter.consumer.secret = "please input your own"
twitter.access.key = "please input your own"
twitter.access.secret = "please input your own"
```

ぶん回すとすぐTwitterAPIの上限に引っかかってしまう、、、
手加減しながら投げる実装が必要。。
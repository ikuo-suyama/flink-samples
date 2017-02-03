package samples.kinesis

import java.util.Properties

import com.typesafe.config.ConfigFactory
import org.apache.flink.streaming.api.datastream.DataStreamSource
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kinesis.{FlinkKinesisConsumer, FlinkKinesisProducer}
import org.apache.flink.streaming.connectors.kinesis.config.{AWSConfigConstants, ConsumerConfigConstants}
import org.apache.flink.streaming.connectors.twitter.TwitterSource
import org.apache.flink.streaming.util.serialization.SimpleStringSchema


object TwitterConversationToKinesis {

  val conf = ConfigFactory.load()

  def main(args: Array[String]): Unit = {
    args match {
      case Array("consume") => consume
      case Array("produce") => produce
    }
  }

  def produce() = {
    // TwitterのProperties作成
    val twitterProps = new Properties
    twitterProps.setProperty(TwitterSource.CONSUMER_KEY, conf.getString("twitter.consumer.key"))
    twitterProps.setProperty(TwitterSource.CONSUMER_SECRET, conf.getString("twitter.consumer.secret"))
    twitterProps.setProperty(TwitterSource.TOKEN, conf.getString("twitter.access.key"))
    twitterProps.setProperty(TwitterSource.TOKEN_SECRET, conf.getString("twitter.access.secret"))

    // Kinesis(Producer)のProperties作成
    val producerProps = new Properties
    producerProps.put(AWSConfigConstants.AWS_REGION, conf.getString("aws.region"))
    producerProps.put(AWSConfigConstants.AWS_ACCESS_KEY_ID, conf.getString("aws.access_key"))
    producerProps.put(AWSConfigConstants.AWS_SECRET_ACCESS_KEY, conf.getString("aws.secret_key"))

    // Kinesis(Producer)の設定
    val kinesis = new FlinkKinesisProducer[String](new SimpleStringSchema, producerProps)
    kinesis.setFailOnError(true)
    kinesis.setDefaultStream(conf.getString("aws.kinesis.stream_name"))
    kinesis.setDefaultPartition("0")

    // 実行
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val simpleStringStream = env.addSource(new TwitterSource(twitterProps))
    // TwitterのStreamingデータをkinesisにsinkする
    simpleStringStream.addSink(kinesis)
    env.execute("Twitter Stream Collect Conversation")
  }

  def consume() = {
    // TODO: 動いていない、以下のエラーが発生する
    // org.apache.flink.kinesis.shaded.com.amazonaws.AmazonClientException: Unable to marshall request to JSON: com/fasterxml/jackson/dataformat/cbor/CBORFactory
    // .......

    // Kinesis(Consumer)のProperties作成
    val consumerProps = new Properties
    consumerProps.put(AWSConfigConstants.AWS_REGION, conf.getString("aws.region"))
    consumerProps.put(AWSConfigConstants.AWS_ACCESS_KEY_ID, conf.getString("aws.access_key"))
    consumerProps.put(AWSConfigConstants.AWS_SECRET_ACCESS_KEY, conf.getString("aws.secret_key"))
    consumerProps.put(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST")

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val kinesis: DataStreamSource[String] = env.addSource(new FlinkKinesisConsumer[String](
      conf.getString("aws.kinesis.stream_name"), new SimpleStringSchema, consumerProps))
    // consumeしたデータをprintする
    kinesis.print
    env.execute("Twitter Stream Collect Conversation")
  }

}


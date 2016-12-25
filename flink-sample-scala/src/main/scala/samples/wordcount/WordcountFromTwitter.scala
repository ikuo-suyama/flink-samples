package samples.wordcount

import java.util.Properties

import com.danielasfregola.twitter4s.TwitterRestClient
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer08
import org.apache.flink.streaming.connectors.twitter.TwitterSource
import org.apache.flink.streaming.util.serialization.SimpleStringSchema

import scala.concurrent.{ExecutionContext, Await}
import scala.concurrent.duration._

/**
 * Created by Ikuo Suyama on 16/12/25.
 */
object WordcountFromTwitter {
  // Object定義でないと[org.apache.flink.api.common.InvalidProgramException: Task not serializable]
  val jsonMapper = new ObjectMapper
  jsonMapper.registerModule(DefaultScalaModule)
  jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  // RestClient(会話取得用)
  val twitterRestClient = TwitterRestClient()

  /**
   * 指定されたIdのTweetを取得します。
   * 鍵アカウントのデータ等、取得できない場合があるのでその場合はNone
   *
   * @param id
   * @return
   */
  def getTweetById(id: String):Option[Tweet] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val f = (for {
      tweet <- twitterRestClient.getTweet(id.toLong)
    } yield {
      Some(Tweet(
        tweet.id.toString,
        tweet.text,
        tweet.lang.get
      ))
    }) recover {case t => None}
    Await.result(f, 5.seconds)
  }

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // Configの読み込み
    val conf = ConfigFactory.load()

    val props = new Properties()
    props.setProperty(TwitterSource.CONSUMER_KEY,    conf.getString("twitter.consumer.key"))
    props.setProperty(TwitterSource.CONSUMER_SECRET, conf.getString("twitter.consumer.secret"))
    props.setProperty(TwitterSource.TOKEN,           conf.getString("twitter.access.key"))
    props.setProperty(TwitterSource.TOKEN_SECRET,    conf.getString("twitter.access.secret"))

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

  /**
   * Twitterのデータ受領用
   * @param id
   * @param text
   * @param inReplyToStatusId
   * @param place
   * @param coordinates
   */
  case class Tweet(id: String,
                   text: String,
                   lang: String,
                   @JsonProperty("in_reply_to_status_id") inReplyToStatusId: Option[String] = None,
                   place: Option[ObjectNode] = None,
                   coordinates: Option[ObjectNode] = None)

}

import com.danielasfregola.twitter4s.TwitterRestClient
import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Created by Ikuo Suyama on 16/12/25.
 */
class Twitter4sSpec extends Specification {

  "twitter4s" should {
    "test" in {
      val client = TwitterRestClient()
      val t = for {
        tweet <- client.getTweet(812914825240055809L)
//        tweet <- client.getTweet(812862201149071360L)
      } yield tweet

      val result = Await.result(t.recover({case t => com.danielasfregola.twitter4s.entities.Tweet}), Duration.Inf)
      println(result)
      "true" must beEqualTo("true")
    }
  }
}

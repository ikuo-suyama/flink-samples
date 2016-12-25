import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.specs2.mutable.Specification

/**
 * Created by Ikuo Suyama on 16/12/25.
 */
class JacksonSampleSpeck extends Specification {
  val mapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  "Jackson" should {

    "項目が足りないJSONのパース" in {
      val json = """{"created_at":"Sun Dec 25 03:27:05 +0000 2016","id":812862201149071360,"id_str":"812862201149071360","text":"\ud06c\ub9ac\uc2a4\ub9c8\uc2a4 \uc120\ubb3c\uac19\uc740\uac74\uac00","source":"\u003ca href=\"http:\/\/twitter.com\" rel=\"nofollow\"\u003eTwitter Web Client\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":4242866052,"id_str":"4242866052","name":"\ub85c\uc544","screen_name":"kni2985","location":"FUB FREE","url":null,"description":"\ub9de\ud314 \uc6d0\ud558\uc2dc\uba74 \uba58\uc158 | \uae40\ud638\ub4dc\uc758 \uc608\uc05c\uc778\uc7a5 \ubcf4\uace0\uac00\uc790 \ub2d8\ub4e4","protected":false,"verified":false,"followers_count":348,"friends_count":309,"listed_count":2,"favourites_count":4260,"statuses_count":3602,"created_at":"Sun Nov 15 06:53:18 +0000 2015","utc_offset":-28800,"time_zone":"Pacific Time (US & Canada)","geo_enabled":false,"lang":"ko","contributors_enabled":false,"is_translator":false,"profile_background_color":"000000","profile_background_image_url":"http:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png","profile_background_image_url_https":"https:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png","profile_background_tile":false,"profile_link_color":"92D100","profile_sidebar_border_color":"000000","profile_sidebar_fill_color":"000000","profile_text_color":"000000","profile_use_background_image":false,"profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/812849508732149760\/4xvunoTe_normal.jpg","profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/812849508732149760\/4xvunoTe_normal.jpg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/4242866052\/1477926526","default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"is_quote_status":false,"retweet_count":0,"favorite_count":0,"entities":{"hashtags":[],"urls":[],"user_mentions":[],"symbols":[]},"favorited":false,"retweeted":false,"filter_level":"low","lang":"ko","timestamp_ms":"1482636425660"}"""


      mapper.readValue(json, classOf[Tweet]) should beEqualTo(Tweet(
        "812862201149071360",
        "크리스마스 선물같은건가"
      ))

      val json2 = """{"created_at":"Sun Dec 25 03:27:05 +0000 2016","id":812862201149071360,"id_str":"812862201149071360","text":"\ud06c\ub9ac\uc2a4\ub9c8\uc2a4 \uc120\ubb3c\uac19\uc740\uac74\uac00","source":"\u003ca href=\"http:\/\/twitter.com\" rel=\"nofollow\"\u003eTwitter Web Client\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":"in_reply_to_status_id","in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":4242866052,"id_str":"4242866052","name":"\ub85c\uc544","screen_name":"kni2985","location":"FUB FREE","url":null,"description":"\ub9de\ud314 \uc6d0\ud558\uc2dc\uba74 \uba58\uc158 | \uae40\ud638\ub4dc\uc758 \uc608\uc05c\uc778\uc7a5 \ubcf4\uace0\uac00\uc790 \ub2d8\ub4e4","protected":false,"verified":false,"followers_count":348,"friends_count":309,"listed_count":2,"favourites_count":4260,"statuses_count":3602,"created_at":"Sun Nov 15 06:53:18 +0000 2015","utc_offset":-28800,"time_zone":"Pacific Time (US & Canada)","geo_enabled":false,"lang":"ko","contributors_enabled":false,"is_translator":false,"profile_background_color":"000000","profile_background_image_url":"http:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png","profile_background_image_url_https":"https:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png","profile_background_tile":false,"profile_link_color":"92D100","profile_sidebar_border_color":"000000","profile_sidebar_fill_color":"000000","profile_text_color":"000000","profile_use_background_image":false,"profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/812849508732149760\/4xvunoTe_normal.jpg","profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/812849508732149760\/4xvunoTe_normal.jpg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/4242866052\/1477926526","default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"is_quote_status":false,"retweet_count":0,"favorite_count":0,"entities":{"hashtags":[],"urls":[],"user_mentions":[],"symbols":[]},"favorited":false,"retweeted":false,"filter_level":"low","lang":"ko","timestamp_ms":"1482636425660"}"""
      mapper.readValue(json2, classOf[Tweet]) should beEqualTo(Tweet(
        "812862201149071360",
        "크리스마스 선물같은건가",
        Some("in_reply_to_status_id")
      ))

    }

    "place" in {
      // String で受けるとエラーになるので、とりあえずObjectNodeで受ける
      // com.fasterxml.jackson.databind.JsonMappingException: Can not deserialize instance of java.lang.String out of START_OBJECT token
      val jsonWithPlace = """{"created_at":"Sun Dec 25 06:27:36 +0000 2016","id":812907629643083778,"id_str":"812907629643083778","text":"Sampahnya perangai kau :)","source":"\u003ca href=\"http:\/\/twitter.com\/download\/iphone\" rel=\"nofollow\"\u003eTwitter for iPhone\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":501452262,"id_str":"501452262","name":"afh","screen_name":"fifaheffendi20","location":null,"url":null,"description":"selalu sendu","protected":false,"verified":false,"followers_count":1607,"friends_count":1608,"listed_count":0,"favourites_count":2877,"statuses_count":38154,"created_at":"Fri Feb 24 04:42:13 +0000 2012","utc_offset":32400,"time_zone":"Seoul","geo_enabled":true,"lang":"en","contributors_enabled":false,"is_translator":false,"profile_background_color":"642142","profile_background_image_url":"http:\/\/pbs.twimg.com\/profile_background_images\/378800000150116110\/9R_QBmIX.jpeg","profile_background_image_url_https":"https:\/\/pbs.twimg.com\/profile_background_images\/378800000150116110\/9R_QBmIX.jpeg","profile_background_tile":false,"profile_link_color":"B63762","profile_sidebar_border_color":"FFFFFF","profile_sidebar_fill_color":"FF9690","profile_text_color":"DE6670","profile_use_background_image":true,"profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/810769193226244096\/R5eSowfm_normal.jpg","profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/810769193226244096\/R5eSowfm_normal.jpg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/501452262\/1479875485","default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":{"id":"8a27d27ff0b22170","url":"https:\/\/api.twitter.com\/1.1\/geo\/id\/8a27d27ff0b22170.json","place_type":"city","name":"Kuala Kuantan","full_name":"Kuala Kuantan, Pahang","country_code":"MY","country":"Malaysia","bounding_box":{"type":"Polygon","coordinates":[[[103.057762,3.617641],[103.057762,4.039980],[103.375501,4.039980],[103.375501,3.617641]]]},"attributes":{}},"contributors":null,"is_quote_status":false,"retweet_count":0,"favorite_count":0,"entities":{"hashtags":[],"urls":[],"user_mentions":[],"symbols":[]},"favorited":false,"retweeted":false,"filter_level":"low","lang":"in","timestamp_ms":"1482647256657"}"""
      val ret = mapper.readValue(jsonWithPlace, classOf[Tweet])
      ret.id must beEqualTo("812907629643083778")
    }
  }
}
case class Tweet(id: String,
                 text: String,
                 @JsonProperty("in_reply_to_status_id") inReplyToStatusId: Option[String] = None,
                 place: Option[ObjectNode] = None,
                 coordinates: Option[ObjectNode] = None)

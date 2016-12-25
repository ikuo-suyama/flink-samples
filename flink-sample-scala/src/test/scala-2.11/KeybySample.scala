import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time

/**
 * Created by Ikuo Suyama on 16/12/25.
 */
object KeybySample {

  case class Sample(name:String, value:Int)

  def main(args: Array[String]) {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.fromElements(
      "To be, or not to be,--that is the question:--",
      "Whether 'tis nobler in the mind to suffer",
      "The slings and arrows of outrageous fortune",
      "Or to take arms against a sea of troubles,")

    val counts = text
      .flatMap(_.split(" "))
      .map { Sample(_ , 1 )}
      .keyBy("name")
      .countWindow(2)
      .sum(1)

    counts.print

    env.execute("Window Stream WordCount")
  }

}

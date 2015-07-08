package com.mesosphere.oinker

import com.twitter.conversions.time.intToTimeableNumber
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.httpx.{Http, Request, Response, Status}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.{Await, Future}

import scala.util.Random

object Main {

  val handles = Seq(
    "Aaron", "Abel", "Alberta", "Alberto", "Alicia", "Ana", "Andres", "Barry", "Beverly", "Bob", "Bobbie", "Bradford",
    "Cary", "Casey", "Cassandra", "Cecelia", "Cesar", "Chad", "Christina", "Clifton", "Courtney", "Dana", "Danny",
    "Darrell", "Dewey", "Diane", "Dora", "Earnest", "Elsa", "Ernestine", "Eunice", "Francisco", "Franklin", "Fred",
    "Gail", "Gene", "Gerald", "Gladys", "Glenda", "Grace", "Gwen", "Gwendolyn", "Hannah", "Holly", "Ivan", "Jacquelyn",
    "Jeffery", "Jill", "Jo", "John", "Johnathan", "Josephine", "Josh", "Julia", "Karen", "Kate", "Kellie", "Kyle",
    "Lee", "Leroy", "Lora", "Lorena", "Lorene", "Loretta", "Lynn", "Lynne", "Malcolm", "Marco", "Marguerite", "Marion",
    "Mark", "Martin", "Marty", "Melissa", "Michelle", "Mildred", "Nathaniel", "Oscar", "Patricia", "Patti", "Pete",
    "Phil", "Rachel", "Renee", "Rex", "Santos", "Shannon", "Shawn", "Shelia", "Shelley", "Sheri", "Stacy", "Stephen",
    "Tamara", "Todd", "Tyler", "Velma", "Vickie", "Vicky", "Yolanda"
  )

  val contents = Seq(
    "Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live the blind texts. Separated they live in Bookmarksgrove right at the coast of the Semantics, a large language ocean. A small river named Duden flows by their place and supplies it with the necessary regelialia. It is a paradisematic country, in which roasted parts of sentences fly into your mouth. Even the all-powerful Pointing has no control about the blind texts it is an almost unorthographic life One day however a small line of blind text by the name of Lorem Ipsum decided to leave for the far World of Grammar. The Big Oxmox advised her not to do so, because there were thousands of bad Commas, wild Question Marks and devious Semikoli, but the Little Blind Text didn’t listen. She packed her seven versalia, put her initial into the belt and made herself on the way. When she reached the first hills of the Italic Mountains, she had a last view back on the skyline of her hometown Bookmarksgrove, the headline of Alphabet Village and the subline of her own road, the Line Lane. Pityful a rethoric question ran over her cheek, then",
    "A wonderful serenity has taken possession of my entire soul, like these sweet mornings of spring which I enjoy with my whole heart. I am alone, and feel the charm of existence in this spot, which was created for the bliss of souls like mine. I am so happy, my dear friend, so absorbed in the exquisite sense of mere tranquil existence, that I neglect my talents. I should be incapable of drawing a single stroke at the present moment; and yet I feel that I never was a greater artist than now. When, while the lovely valley teems with vapour around me, and the meridian sun strikes the upper surface of the impenetrable foliage of my trees, and but a few stray gleams steal into the inner sanctuary, I throw myself down among the tall grass by the trickling stream; and, as I lie close to the earth, a thousand unknown plants are noticed by me: when I hear the buzz of the little world among the stalks, and grow familiar with the countless indescribable forms of the insects and flies, then I feel the presence of the Almighty, who formed us in his own image, and the breath",
    "One morning, when Gregor Samsa woke from troubled dreams, he found himself transformed in his bed into a horrible vermin. He lay on his armour-like back, and if he lifted his head a little he could see his brown belly, slightly domed and divided by arches into stiff sections. The bedding was hardly able to cover it and seemed ready to slide off any moment. His many legs, pitifully thin compared with the size of the rest of him, waved about helplessly as he looked. \"What's happened to me?\" he thought. It wasn't a dream. His room, a proper human room although a little too small, lay peacefully between its four familiar walls. A collection of textile samples lay spread out on the table - Samsa was a travelling salesman - and above it there hung a picture that he had recently cut out of an illustrated magazine and housed in a nice, gilded frame. It showed a lady fitted out with a fur hat and fur boa who sat upright, raising a heavy fur muff that covered the whole of her lower arm towards the viewer. Gregor then turned to look out the window at the dull weather. Drops",
    "The quick, brown fox jumps over a lazy dog. DJs flock by when MTV ax quiz prog. Junk MTV quiz graced by fox whelps. Bawds jog, flick quartz, vex nymphs. Waltz, bad nymph, for quick jigs vex! Fox nymphs grab quick-jived waltz. Brick quiz whangs jumpy veldt fox. Bright vixens jump; dozy fowl quack. Quick wafting zephyrs vex bold Jim. Quick zephyrs blow, vexing daft Jim. Sex-charged fop blew my junk TV quiz. How quickly daft jumping zebras vex. Two driven jocks help fax my big quiz. Quick, Baz, get my woven flax jodhpurs! \"Now fax quiz Jack!\" my brave ghost pled. Five quacking zephyrs jolt my wax bed. Flummoxed by job, kvetching W. zaps Iraq. Cozy sphinx waves quart jug of bad milk. A very bad quack might jinx zippy fowls. Few quips galvanized the mock jury box. Quick brown dogs jump over the lazy fox. The jay, pig, fox, zebra, and my wolves quack! Blowzy red vixens fight for a quick jump. Joaquin Phoenix was gazed by MTV for luck. A wizard’s job is to vex chumps quickly in fog. Watch \"Jeopardy!\", Alex Trebek's fun TV quiz game. Woven silk pyjamas exchanged for blue quartz. Brawny gods just"
  ).flatMap { p => p.split("\\.") }

  val rand = new Random()

  def main(args: Array[String]) {
    val host = sys.env("OINKER_HOST")
    val port = sys.env("OINKER_PORT").toInt
    val count = sys.env("OINK_COUNT").toInt
    val baseUrl = s"http://$host:$port"
    println(s"Oinking to: $baseUrl")
    val bot = new Bot(baseUrl, service(host, port))
    val fs: Seq[Future[Response]] = (0 until count).map { i =>
      val handle = handles(rand.nextInt(handles.length))
      val content = contents(rand.nextInt(contents.length))
      bot.oink(handle, content)
    }
    try {
      println("Await...")
      val oinks = Await.result(Future.collect(fs))
      println("Done.")
      println("oinks.size = " + oinks.size)
    }
    catch {
      case e: Exception => e.printStackTrace()
    }
    sys.exit(0)
  }

  private def service(host: String, port: Int): Service[Request, Response] = {
    val client = ClientBuilder()
      .codec(Http())
      .hosts(s"$host:$port")
      .tcpConnectTimeout(2.seconds)
      .requestTimeout(5.seconds)
      .hostConnectionLimit(30)
      .failFast(false)
    val service = new HandleErrors andThen client.build()
    service
  }

  class HandleErrors extends SimpleFilter[Request, Response] {
    def apply(request: Request, service: Service[Request, Response]) = {
      service(request) flatMap { response =>
        response.status match {
          case Status.Ok => Future.value(response)
          case Status.Found => Future.value(response)
          case _  => Future.exception(new Exception(s"${response.status.code}"))
        }
      }
    }
  }

}

import akka.actor.{ActorSystem, Props, ActorLogging, Actor}
import akka.event.LoggingReceive
import akka.routing.FromConfig

/**
 *
 * Organization: VerticalScope Inc.
 * User: Tyson Hamilton (thamilton@verticalscope.com)
 * Date: 16/01/13
 * Time: 11:40 AM
 * 
 */

sealed trait GatorMessage
case class GatorResult(output: Either[Throwable, String]) extends GatorMessage

class Gator extends Actor with ActorLogging {
  val router = context.actorOf(Props[GatorWorker].withRouter(FromConfig), name = "router")

  def receive = LoggingReceive {
    case in:String =>
      log.debug("Received message")
      Console.println("Received a message: " + in)

      router.tell(in, sender)

    case _ =>
      log.error("Received unknown message type")
      sender ! GatorResult(Left(new Exception("Gator received unknown message")))
  }
}

object Gator {
  def main(args: Array[String]): Unit = {
    if (args.nonEmpty) System.setProperty("akka.remote.netty.port", args(0))

    val system = ActorSystem("GateCluster")

    system.actorOf(Props[GatorWorker], name = "worker")
    system.actorOf(Props[Gator], name = "gator")
    //start-router-lookup
  }
}



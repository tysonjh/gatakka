import akka.actor._
import akka.event.LoggingReceive
import java.util.concurrent.TimeoutException

/**
 *
 * Organization: VerticalScope Inc.
 * User: Tyson Hamilton (thamilton@verticalscope.com)
 * Date: 16/01/13
 * Time: 12:01 PM
 * 
 */
class GatorWorker extends Actor with ActorLogging {
  val annie = null // TODO: put GATE annie here

  def receive = LoggingReceive {
    case in: String =>
      log.info("Received string to process")
      // TODO: annie processing
      sender ! GatorResult(Right("GatorWorker likes"))

    case ReceiveTimeout =>
      log.error("Received timeout")
      sender ! GatorResult(Left(new TimeoutException("GatorWorker received a timeout")))

    case _ =>
      log.error("Received unknown message")
      sender ! GatorResult(Left(new Exception("GatorWorker no like")))
  }

}

// Lets see if we can add another worker to the router
object GatorWorker {
  def main(args: Array[String]): Unit = {
    if (args.nonEmpty) System.setProperty("akka.remote.netty.port", args(0))

    val system = ActorSystem("GateCluster")

    system.actorOf(Props[GatorWorker], name = "worker")
  }
}
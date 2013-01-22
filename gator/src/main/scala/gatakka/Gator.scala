package gatakka

import akka.actor._
import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.event.LoggingReceive
import akka.routing.FromConfig
import gate.util.GateException
import scala.concurrent.duration._

sealed trait GatorMessage
case class GatorStatus() extends GatorMessage
case class GatorRequest(in: String) extends GatorMessage
case class GatorVerifyPresence(annot: String, in: String) extends GatorMessage
case class GatorResult(output: Either[Throwable, String]) extends GatorMessage

class Gator extends Actor with ActorLogging {
  val router = context.actorOf(Props[GatorWorker].withRouter(FromConfig), name = "router")

  override val supervisorStrategy = OneForOneStrategy (5, 30 seconds) {
    case g: GateException =>
      log.error("GatorWorker can't load GATE resource: {}", g.getMessage)
      Restart

    case e: Exception =>
      log.error("GatorWorker died from {}, stopping", e.getMessage)
      Stop
  }

  def receive = LoggingReceive {
    case in: String => router.tell(in, sender)

    case GatorStatus() =>
      router.tell(GatorStatus(), sender)

    case msg@GatorRequest(in) =>
      router.tell(GatorRequest(in), sender)

    case msg@GatorVerifyPresence(annot, in) =>
      router.tell(GatorVerifyPresence(annot, in), sender)

    case _ =>
      log.error("Received unknown message type")
      sender ! GatorResult(Left(new Exception("Gator received unknown message")))
  }
}

object Gator {
  def main(args: Array[String]): Unit = {
    if (args.nonEmpty) System.setProperty("akka.remote.netty.port", args(0))
    val system = ActorSystem("GateCluster")
    system.actorOf(Props[Gator], name = "gator")
  }
}



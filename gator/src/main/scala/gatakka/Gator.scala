package gatakka

import akka.actor._
import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.event.LoggingReceive
import akka.routing.FromConfig
import gate.creole.ResourceInstantiationException

sealed trait GatorMessage
case class GatorResult(output: Either[Throwable, String]) extends GatorMessage

class Gator extends Actor with ActorLogging {
  val router = context.actorOf(Props[GatorWorker].withRouter(FromConfig), name = "router")

  override val supervisorStrategy = OneForOneStrategy () {
    case g: ResourceInstantiationException =>
      log.error("GatorWorker can't load GATE resource: {}", g.getMessage)
      Stop

    case e: Exception =>
      log.error("GatorWorker died from {}, restarting", e.getMessage)
      Restart
  }

  def receive = LoggingReceive {
    case in:String =>
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
    system.actorOf(Props[Gator], name = "gator")
  }
}



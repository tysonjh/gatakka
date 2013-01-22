package gatakka

import akka.actor._
import akka.event.LoggingReceive
import java.util.concurrent.TimeoutException
import org.junit.runners.model.InitializationError

class GatorWorker extends Actor with ActorLogging {
  private [gatakka] var annie: Option[Annie] = None

  override def preStart() = {
    log.info("Started actor {}", self.path.name)

    GateFactory.init()
    annie = Some(new Annie())

    annie match {
      case Some(a) => a.init()
      case None => throw new IllegalStateException("Could not initialize ANNIE")
    }
  }

  override def postStop() {
    annie match {
      case Some(a) =>
        a.clean()
        annie = None
      case None =>
    }
  }

  def receive = LoggingReceive {
    case GatorStatus() =>
      sender ! GatorResult(Right(annie.isDefined.toString))

    case GatorRequest(in) if annie.isDefined =>
      log.debug("Received '{}' to process in actor {}", Array(in, self.path.name))
      val annots = annie.get.execute(in)
      sender ! GatorResult(Right(GateUtils.annotationListprettyPrint(annots, in)))

    case GatorVerifyPresence(annot, in) if annie.isDefined =>
      log.debug("Received '{}' to process in actor {}", Array(in, self.path.name))

      sender ! GatorResult(Right((annie.get.execute(in)
          .filter(_.getType.toLowerCase.equals(annot.toLowerCase)).size > 0).toString))

    case in: String =>
      log.error("Annie is not initialized")
      sender ! GatorResult(Left(new IllegalStateException("Annie is not initialized")))

    case _ =>
      log.error("Received unknown message")
      sender ! GatorResult(Left(new Exception("GatorWorker no like")))
  }

}
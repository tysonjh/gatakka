package gatakka

import akka.actor._
import akka.event.LoggingReceive
import java.util.concurrent.TimeoutException
import org.junit.runners.model.InitializationError

class GatorWorker extends Actor with ActorLogging {
  private var annie: Option[Annie] = None

  override def preStart() = {
    log.info("Started actor {}", self.path.name)

    GateFactory.init
    annie = Some(new Annie())

    annie match {
      case Some(a) => a.init
      case None => throw new InitializationError("Could not initialize ANNIE")
    }
  }

  def receive = LoggingReceive {
    case in: String if annie.isDefined =>
      log.debug("Received '{}' to process in actor {}", Array(in, self.path.name))
      val annots = annie.get.execute(in)
      sender ! GatorResult(Right(GateUtils.annotationListprettyPrint(annots, in)))

    case in: String =>
      log.error("Annie is not initialized")
      sender ! GatorResult(Left(new InitializationError("Annie is not initialized")))

    case ReceiveTimeout =>
      log.error("Received timeout")
      sender ! GatorResult(Left(new TimeoutException("GatorWorker received a timeout")))

    case _ =>
      log.error("Received unknown message")
      sender ! GatorResult(Left(new Exception("GatorWorker no like")))
  }

}
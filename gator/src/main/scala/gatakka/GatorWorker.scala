package gatakka

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

  override def preStart() = {
    log.info("Started actor {}", self.path.name)


  }

  def receive = LoggingReceive {
    case in: String =>
      log.debug("Received '{}' to process in actor {}", Array(in, self.path.name))

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
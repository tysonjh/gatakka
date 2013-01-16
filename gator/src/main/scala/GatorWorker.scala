import akka.actor.{ActorLogging, ReceiveTimeout, Actor}
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
      log.debug("Received string to process")
      // TODO: annie processing
      sender ! GatorResult(Right("GatorWorker likes"))

    case ReceiveTimeout =>
      log.debug("Received timeout")
      sender ! GatorResult(Left(new TimeoutException("GatorWorker received a timeout")))

    case _ =>
      log.debug("Received unknown message")
      sender ! GatorResult(Left(new Exception("GatorWorker no like")))
  }

}

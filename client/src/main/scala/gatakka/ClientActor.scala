package gatakka
import akka.actor.{Actor, ActorLogging}
import akka.cluster.ClusterEvent._
import akka.cluster.ClusterEvent.CurrentClusterState
import akka.cluster.ClusterEvent.MemberJoined
import akka.cluster.ClusterEvent.MemberUp
import akka.cluster.ClusterEvent.UnreachableMember
import akka.event.LoggingReceive

class ClientActor extends Actor with ActorLogging {
  def receive = LoggingReceive {
    case state: CurrentClusterState ⇒
      log.debug("Current members: {}", state.members)
    case MemberJoined(member) ⇒
      log.debug("Member joined: {}", member)
    case MemberUp(member) ⇒
      log.debug("Member is Up: {}", member)
    case UnreachableMember(member) ⇒
      log.debug("Member detected as unreachable: {}", member)
    case GatorResult(e) =>
      e match {
        case Left(ex) =>
          log.warning("Received error from Gator: {}", ex.getMessage)
        case Right(r) =>
          log.info("Received result from Gator: " + r)
          Console.println("Result received from actor %s: %s".format(sender.path.name, r))
      }
    case _: ClusterDomainEvent ⇒ // ignore

  }
}

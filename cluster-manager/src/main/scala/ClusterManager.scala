import akka.actor.{Props, Actor, ActorLogging, ActorSystem}
import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import org.slf4j.LoggerFactory

/**
 * User: tysonjh
 * Date: 1/15/13
 * Time: 4:49 PM
 */
object ClusterManager {
  val log = LoggerFactory.getLogger("GATEAkka")

  def main(args: Array[String]): Unit = {
    val annotations: Set[String] = Set("Person", "Place", "Organization")

    // Override the configuration of the port
    // when specified as program argument
    if (args.nonEmpty) System.setProperty("akka.remote.netty.port", args(0))

    val system = ActorSystem("ClusterSystem")
    val clusterListener = system.actorOf(Props(new Actor with ActorLogging {
      def receive = {
        case state: CurrentClusterState ⇒
          log.info("Current members: {}", state.members)
        case MemberJoined(member) ⇒
          log.info("Member joined: {}", member)
        case MemberUp(member) ⇒
          log.info("Member is Up: {}", member)
        case UnreachableMember(member) ⇒
          log.info("Member detected as unreachable: {}", member)
        case _: ClusterDomainEvent ⇒ // ignore

      }
    }), name = "clusterListener")

    Cluster(system).subscribe(clusterListener, classOf[ClusterDomainEvent])

    log.info("Enter sentences to be run through the ANNIE pipeline. \nResults will contain a sample set of annotations." +
      "\n(%s)".format(annotations.reduceLeft(_ + "," + _)))
  }
}

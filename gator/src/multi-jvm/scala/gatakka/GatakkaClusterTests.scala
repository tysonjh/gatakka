package gatakka

import akka.remote.testkit.MultiNodeSpec
import akka.testkit.ImplicitSender
import akka.actor.Props
import scala.concurrent.duration._
import akka.routing.RoundRobinRouter
import akka.cluster.Cluster
import akka.cluster.routing.{ClusterRouterConfig, ClusterRouterSettings}


class GatorMultiJvmNode1 extends GatakkaMultiNode
class GatorMultiJvmNode2 extends GatakkaMultiNode


class GatakkaMultiNode extends MultiNodeSpec(GatakkaMultiNodeConfig)
  with STMultiNodeSpec with ImplicitSender {
  import GatakkaMultiNodeConfig._

  def initialParticipants = roles.size
  def cluster = Cluster(system)

  "A Gatakka Gator" must {
    "wait for nodes to enter a barrier" in {
      testConductor.enter("startup")
    }

    "join the cluster" in {
      runOn(client1) {
        cluster join node(client1).address

        system.actorOf(Props[Gator].withRouter(
          ClusterRouterConfig(
            RoundRobinRouter(), ClusterRouterSettings(
              totalInstances = 2,
              routeesPath = "/user/worker",
              allowLocalRoutees = false))),
          name = "router")

        val gator = system.actorOf(Props[Gator], name = "gator")
        testConductor.enter("client-up")

        // Wait for gator to be up
        testConductor.enter("gator-up")
        awaitCond{
          gator ! GatorStatus()
          expectMsgPF(30 seconds) {
            case GatorResult(r) =>
              r must be (Right("true"))
              true
            case _ => false
          }
        }
      }

      runOn(gator1) {
        cluster join node(client1).address

        // Wait for client cluster to be up
        testConductor.enter("client-up")

        system.actorOf(Props[Gator], name = "gator")
        testConductor.enter("gator-up")
      }
    }

    "verify presence of Persons" in {
      runOn(client1){
        cluster join node(client1).address
        val gator = system.actorFor("/user/gator")

        awaitCond{
          gator ! GatorVerifyPresence("Person", "Sometimes Tyson falls asleep sitting up.")
          expectMsgPF(5 seconds) {
            case GatorResult(r) =>
              r must be (Right("true"))
              true
            case _ => false
          }
        }
      }
    }

    "process a sentence" in {
      runOn(client1){
        cluster join node(client1).address
        val gator = system.actorFor("/user/gator")

        awaitCond{
          gator ! GatorRequest("Where's the kaboom? There was supposed to be an earth-shattering kaboom!")
          expectMsgPF(5 seconds) {
            case GatorResult(r)  if r.isRight =>
              true
            case _ => false
          }
        }
      }
    }
  }
}

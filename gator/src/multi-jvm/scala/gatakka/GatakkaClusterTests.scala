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

  "A Gatakka Gator" must {
    "join the cluster" in {

      runOn(client1) {
        Cluster(system) join node(client1).address

        system.actorOf(Props[Gator].withRouter(
          ClusterRouterConfig(
            RoundRobinRouter(), ClusterRouterSettings(
              totalInstances = 2,
              routeesPath = "/user/worker",
              allowLocalRoutees = false))),
          name = "router")

        system.actorOf(Props[Gator], name = "gator")
      }

      testConductor.enter("client-up")

      runOn(gator1) {
        Cluster(system) join node(client1).address
        system.actorOf(Props[Gator], name = "gator")
      }

      testConductor.enter("all-started")

      runOn(client1) {
        val gator = system.actorFor("/user/gator")

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
      testConductor.enter("all-finished")
    }

    "verify presence of Persons" in {
      runOn(client1){
        Cluster(system) join node(client1).address
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
        Cluster(system) join node(client1).address
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

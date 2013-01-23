package gatakka

import akka.remote.testkit.MultiNodeSpec
import akka.testkit.ImplicitSender
import akka.actor.Props
import scala.concurrent.duration._
import akka.routing.RoundRobinRouter
import akka.cluster.{Member, MemberStatus, Cluster}
import akka.cluster.routing.{ClusterRouterConfig, ClusterRouterSettings}
import akka.pattern.ask
import concurrent.Await
import akka.util.Timeout
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}


class GatorMultiJvmNode1 extends GatakkaMultiNode
class GatorMultiJvmNode2 extends GatakkaMultiNode


class GatakkaMultiNode extends MultiNodeSpec(GatakkaMultiNodeConfig)
  with STMultiNodeSpec with ImplicitSender {
  import GatakkaMultiNodeConfig._

  implicit val timeout = Timeout(20 seconds)
  def initialParticipants = roles.size

  "A Gatakka Gator" must {
    "start up the cluster" in {
      Cluster(system).subscribe(testActor, classOf[MemberUp])
      expectMsgClass(classOf[CurrentClusterState])

      val clientAddress = node(client1).address
      val gatorAddress = node(gator1).address

      Cluster(system) join clientAddress

      runOn(client1) {
        system.actorOf(Props[Gator].withRouter(
          ClusterRouterConfig(
            RoundRobinRouter(), ClusterRouterSettings(
              totalInstances = 2,
              routeesPath = "/user/worker",
              allowLocalRoutees = false))),
          name = "router")

        system.actorOf(Props[Gator], name = "gator")
        testConductor.enter("client-up")
      }

      runOn(gator1) {
        testConductor.enter("client-up")
        system.actorOf(Props[Gator], name = "gator")
      }

      expectMsgAllOf(
        MemberUp(Member(clientAddress, MemberStatus.Up)),
        MemberUp(Member(gatorAddress, MemberStatus.Up)))

      Cluster(system).unsubscribe(testActor)

      testConductor.enter("all-up")
    }

    "respond to status queries" in {
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
      testConductor.enter("done-test1")
    }

    "verify presence of Persons" in {
      runOn(client1){
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

      testConductor.enter("done-test2")
    }

    "process a sentence" in {
      runOn(client1){
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

      testConductor.enter("done-test3")
    }

    "process many many sentences quickly" in {
      runOn(client1){
        val gator = system.actorFor("/user/gator")

        val results = for (i <- 0 to (MoPI.mopiallSentences.size - 1)) yield {
          gator ? GatorRequest(MoPI.mopiallSentences(i))
        }

        results.map {
          r => Await.result(r, 20 seconds)
        }
      }
      testConductor.enter("done-test4")
    }
  }
}

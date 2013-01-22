package gatakka

import akka.remote.testkit.MultiNodeConfig
import com.typesafe.config.ConfigFactory

object GatakkaMultiNodeConfig extends MultiNodeConfig {
  val client1 = role("node1")
  val gator1 = role("node2")

  commonConfig(ConfigFactory.parseString("""
    akka {
      loglevel = DEBUG
      stdout-loglevel = DEBUG
      event-handlers = ["akka.event.slf4j.Slf4jEventHandler"]

      actor {
        provider = "akka.cluster.ClusterActorRefProvider"
        debug {
          receive = on
          lifecycle = on
        }
        deployment {
          /gator/router {
            router = round-robin
            nr-of-instances = 2
          }
        }
      }

      cluster {
        auto-join = off
        metrics.collector-class = akka.cluster.JmxMetricsCollector
      }

      remote.log-remote-lifecycle-events = off
    }"""))
}

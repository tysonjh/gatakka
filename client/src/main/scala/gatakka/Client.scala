package gatakka
import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.routing.FromConfig
import java.net.MalformedURLException
import org.slf4j.LoggerFactory

/**
 * User: tysonjh
 * Date: 1/15/13
 * Time: 4:49 PM
 */
object Client {
  def main(args: Array[String]): Unit = {
    val annotations: Set[String] = Set("Person", "Place", "Organization")
    val system = ActorSystem("GateCluster")
    val gator = system.actorOf(Props[Gator].withRouter(FromConfig), name = "router")
    val clientActor = system.actorOf(Props[ClientActor], name = "clientactor")
    Cluster(system).subscribe(clientActor, classOf[ClusterDomainEvent])

    system.actorOf(Props[Gator], name = "gator")
    //start-router-lookup

    // Main loop
    consoleLoop(annotations, gator, clientActor, system)

    system.shutdown()
    sys.exit(0)
  }

  def consoleLoop(annots: Set[String], to: ActorRef, from: ActorRef, system: ActorSystem) = {
    var continue = true
    Console.println("Enter a sentence to run through the ANNIE pipeline.\n" +
      "Results will contain a sample set of annotations.\n" +
      "(%s)\n".format(annots.reduceLeft(_ + "," + _)) +
      "Enter \'q\' to exit")

    while(continue) {
      Console.print("Sentence:\n")
      val command = Console.readLine()

      command match {
        case exit if exit.trim.toLowerCase.equals("q") =>
          continue = false

        case sentence if sentence.trim.size > 0 =>
          try {
            Console.println("Sending to gator")
            to.tell(sentence, from)
          } catch {
            case m: MalformedURLException =>
              Console.println("Gator not started")
            case e: Exception =>
              Console.println("Error {}, if continues exit with \'q\'", e.getMessage)
          }

        case _ =>
          Console.println("Try again")
      }
    }
  }
}

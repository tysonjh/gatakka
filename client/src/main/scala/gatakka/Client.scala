package gatakka
import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.routing.FromConfig
import java.net.MalformedURLException

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
    val verifyAnnotationRegex = """(.*)~~(.*)""".r
    usage
    while(continue) {
      Console.print("Sentence:\n")
      val command = Console.readLine()

      command match {
        case exit if exit.trim.toLowerCase.equals("q") =>
          continue = false

        case sentence if sentence.trim.size > 0 =>
          try {
            sentence match {
              case verifyAnnotationRegex(annot, _) =>
                Console.println("Verifying presence of %s".format(annot))
                to.tell(GatorVerifyPresence(annot, sentence), from)
              case _ =>
                Console.println("Processing sentence")
                to.tell(GatorRequest(sentence), from)
            }
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

  private [gatakka] def usage {
    Console.println("Options:\n" +
      "1. Enter a sentence for ANNIE to process\n" +
      "\tE.g: It is cold in Toronto.\n" +
      "2. Enter an annotation type (A~~) followed by a sentence to verify annotation presence\n" +
      "\tE.g: Person~~Sometimes Tyson throws snowballs at children.\n" +
      "3. Enter `q` to quit\n" +
      "\tE.g.: q\n"
    )

  }
}

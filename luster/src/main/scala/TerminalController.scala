import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import java.net.MalformedURLException
import org.slf4j.LoggerFactory

/**
 * User: tysonjh
 * Date: 1/15/13
 * Time: 4:49 PM
 */
object TerminalController {
  val log = LoggerFactory.getLogger("gatakka")

  def main(args: Array[String]): Unit = {
    val annotations: Set[String] = Set("Person", "Place", "Organization")

    // Override the configuration of the port
    // when specified as program argument
    if (args.nonEmpty) System.setProperty("akka.remote.netty.port", args(0))

    val system = ActorSystem("LusterCluster")
    val terminactor = system.actorOf(Props[Terminactor], name = "terminactor")

    Cluster(system).subscribe(terminactor, classOf[ClusterDomainEvent])

    // Main loop
    consoleLoop(annotations, terminactor, system)
  }

  def consoleLoop(annots: Set[String], from: ActorRef, system: ActorSystem) = {
    var continue = true
    Console.println("Enter a sentence to run through the ANNIE pipeline.\n" +
      "Results will contain a sample set of annotations.\n" +
      "(%s)\n".format(annots.reduceLeft(_ + "," + _)) +
      "(type \'q\' to exit")

    while(continue) {
      Console.print("Sentence: ")
      val command = Console.readLine()

      command match {
        case exit if exit.trim.toLowerCase.equals("q") =>
          continue = false

        case sentence if sentence.trim.size > 0 =>
          try {
            val gator = system.actorFor("/gator/router")
            log.debug("Looked up gator: {}", gator.path)

            Console.println("Sending to gator")
            gator.tell(sentence, from)
          } catch {
            case m: MalformedURLException =>
              Console.println("Gator actor not started")
            case e: Exception =>
              Console.println("Error {}, if continues exit with \'q\'", e.getMessage)
          }

        case _ =>
          Console.println("Try again")
      }
    }


    log.info("Exiting")
    system.shutdown()
    sys.exit(0)
  }
}

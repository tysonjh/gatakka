package gatakka

import gate._
import corpora.DocumentContentImpl
import creole.SerialAnalyserController
import gate.util.persistence.PersistenceManager
import GateUtils._
import org.junit.runners.model.InitializationError

class Annie {
  private [gatakka] val pluginUrl = classOf[Annie].getResource(GateFactory.pluginsDirectory + "/ANNIE")
  private [gatakka] val pluginGappUrl = classOf[Annie].getResource(GateFactory.pluginsDirectory + "/ANNIE/ANNIE_with_defaults.gapp")
  private [gatakka] val corpus: Corpus = Factory.createResource("gate.corpora.CorpusImpl").asInstanceOf[Corpus]
  private [gatakka] val document: Document = Factory.createResource("gate.corpora.DocumentImpl").asInstanceOf[Document]
  private [gatakka] var controller: Option[SerialAnalyserController] = None

  def init() {
    Gate.getCreoleRegister.registerDirectories(pluginUrl)
    controller = Some(PersistenceManager
      .loadObjectFromUrl(pluginGappUrl)
      .asInstanceOf[SerialAnalyserController])
  }

  /**
   * Run the pipeline on the provided string and return a result.
   * @param in String to be processed
   */
  def execute(in: String): List[SimpleAnnotation] = {
    var result: List[SimpleAnnotation] = List.empty[SimpleAnnotation]

    controller match {
      case Some(annie) =>
        try {
          document.setContent(new DocumentContentImpl(in))
          corpus.add(document)
          annie.setCorpus(corpus)
          annie.execute()

          result = document.getAnnotations.toList
        } finally {
          // Free up objects for GC and keep creole register clean
          document.setContent(null)
          corpus.clear()
          annie.setCorpus(null)
        }
      case None => {
        throw new InitializationError("ANNIE not initialized")
      }
    }
    result
  }

  /**
   * Release objects for GC.
   */
  def clean() {
    document.cleanup()
    corpus.cleanup()
    controller match {
      case Some(annie) => annie.cleanup()
      case _ => // do nothing
    }
  }
}

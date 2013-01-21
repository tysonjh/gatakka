package gatakka

import gate.Gate
import java.io.File

object GateFactory {
  private [gatakka] val pluginsDirectory = "/plugins"

  /**
   * Gate must be initialized exactly once.
   */
  def init {
    if (!Gate.isInitialised){
      Gate.runInSandbox(true)
      Gate.init()
    }
  }

}

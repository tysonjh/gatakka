package gatakka

import gate.Gate
import java.io.File
import concurrent.Lock

object GateFactory {
  private [gatakka] val pluginsDirectory = "/plugins"
  private [gatakka] val lock = new Lock

  /**
   * Gate must be initialized exactly once.
   */
  def init() {
    lock.acquire()
    if (!Gate.isInitialised){
      Gate.runInSandbox(true)
      Gate.init()
    }
    lock.release()
  }

}

package gatakka

import akka.remote.testkit.MultiNodeSpecCallbacks
import org.scalatest.matchers.MustMatchers
import org.scalatest.{WordSpec, BeforeAndAfterAll}

/**
 *
 * Organization: VerticalScope Inc.
 * User: Tyson Hamilton (thamilton@verticalscope.com)
 * Date: 21/01/13
 * Time: 5:15 PM
 * 
 */
trait STMultiNodeSpec extends MultiNodeSpecCallbacks with WordSpec
  with MustMatchers with BeforeAndAfterAll {

  override def beforeAll() = multiNodeSpecBeforeAll()

  override def afterAll() = multiNodeSpecAfterAll()

}

package peak.peak.poc

import junit.framework.Test
import junit.framework.TestCase
import junit.framework.TestSuite


/**
  * Unit test for simple App.
  */
object Apptest {
  /**
    * @return the suite of tests being tested
    */
  def suite = new TestSuite(classOf[Apptest])
}

//noinspection ScalaDocUnknownParameter
class Apptest(val testName: String)

/**
  * Create the test case
  *
  * @param testName name of the test case
  */
  extends TestCase(testName) {
  /**
    * Rigourous Test :-)
    */
  def testApp(): Unit = {
    assert(true)
  }
}

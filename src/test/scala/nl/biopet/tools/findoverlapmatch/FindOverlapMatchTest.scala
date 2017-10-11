package nl.biopet.tools.findoverlapmatch

import nl.biopet.test.BiopetTest
import org.testng.annotations.Test

class FindOverlapMatchTest extends BiopetTest {
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      FindOverlapMatch.main(Array())
    }
  }
}

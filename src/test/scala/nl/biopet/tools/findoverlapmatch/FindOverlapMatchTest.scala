package nl.biopet.tools.findoverlapmatch

import nl.biopet.test.BiopetTest
import org.testng.annotations.Test

object FindOverlapMatchTest extends BiopetTest {
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      ToolTemplate.main(Array())
    }
  }
}

package nl.biopet.tools.findoverlapmatch

import java.io.File

import nl.biopet.utils.test.tools.ToolTest
import org.testng.annotations.Test

import scala.io.Source

class FindOverlapMatchTest extends ToolTest[Args] {
  def toolCommand: FindOverlapMatch.type = FindOverlapMatch
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      FindOverlapMatch.main(Array())
    }
  }

  @Test
  def testOverlap(): Unit = {
    val input = new File(resourcePath("/overlapmetrics.txt"))
    val output = File.createTempFile("overlap.", ".txt")
    val shouldBeOutput = new File(resourcePath("/overlapmetrics.default.output"))
    output.deleteOnExit()
    FindOverlapMatch.main(
      Array("-i", input.getAbsolutePath, "-c", "0.9", "-o", output.getAbsolutePath))
    Source.fromFile(output).getLines().toList shouldBe Source
      .fromFile(shouldBeOutput)
      .getLines()
      .toList
  }

  @Test
  def testOverlapSameName(): Unit = {
    val input = new File(resourcePath("/overlapmetrics.txt"))
    val output = File.createTempFile("overlap.", ".txt")
    val shouldBeOutput = new File(resourcePath("/overlapmetrics.same_names.output"))
    output.deleteOnExit()
    FindOverlapMatch.main(
      Array("-i",
        input.getAbsolutePath,
        "-c",
        "0.9",
        "-o",
        output.getAbsolutePath,
        "--use_same_names"))
    Source.fromFile(output).getLines().toList shouldBe Source
      .fromFile(shouldBeOutput)
      .getLines()
      .toList
  }
}

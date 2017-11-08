package nl.biopet.tools.findoverlapmatch

import java.io.{ByteArrayOutputStream, File}

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
  def testStdout(): Unit = {
    val input = new File(resourcePath("/overlapmetrics.txt"))
    val shouldBeOutput = new File(resourcePath("/overlapmetrics.default.output"))
    val stream = new ByteArrayOutputStream
    Console.withOut(stream) {
      FindOverlapMatch.main(
        Array("-i", input.getAbsolutePath, "-c", "0.9"))
    }

    Source.fromRawBytes(stream.toByteArray).getLines().toList shouldBe Source
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

  @Test
  def testBestMatch(): Unit = {
    val input = resourceFile("/overlapmetrics.txt")
    val output = File.createTempFile("overlap.", ".txt")
    val shouldBeOutput = resourceFile("/overlapmetrics.best_match.output")
    output.deleteOnExit()
    FindOverlapMatch.main(
      Array("-i",
        input.getAbsolutePath,
        "-c",
        "1.0",
        "-o",
        output.getAbsolutePath,
        "--showBestMatch"))
    Source.fromFile(output).getLines().toList shouldBe Source
      .fromFile(shouldBeOutput)
      .getLines()
      .toList
  }

  @Test
  def testCorrectMatch(): Unit = {
    val input = resourceFile("/overlapmetrics.txt")
    val output = File.createTempFile("overlap.", ".txt")
    val shouldBeOutput = resourceFile("/overlapmetrics.regex.output")
    val regexFile = resourceFile("/shouldMatchRegexes.tsv")
    output.deleteOnExit()
    FindOverlapMatch.main(
      Array("-i", input.getAbsolutePath,
        "-c", "1.0",
        "-o", output.getAbsolutePath,
        "--shouldMatchRegexFile", regexFile.getAbsolutePath))
    Source.fromFile(output).getLines().toList shouldBe Source
      .fromFile(shouldBeOutput)
      .getLines()
      .toList
  }

  @Test
  def testBestCorrectMatch(): Unit = {
    val input = resourceFile("/overlapmetrics.txt")
    val output = File.createTempFile("overlap.", ".txt")
    val shouldBeOutput = resourceFile("/overlapmetrics.best_match.output")
    val regexFile = resourceFile("/shouldMatchRegexes.tsv")
    output.deleteOnExit()
    FindOverlapMatch.main(
      Array("-i",
        input.getAbsolutePath,
        "-c",
        "1.0",
        "-o",
        output.getAbsolutePath,
        "--showBestMatch",
        "--shouldMatchRegexFile", regexFile.getAbsolutePath))
    Source.fromFile(output).getLines().toList shouldBe Source
      .fromFile(shouldBeOutput)
      .getLines()
      .toList
  }
}

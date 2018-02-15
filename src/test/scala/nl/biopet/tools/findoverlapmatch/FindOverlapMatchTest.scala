/*
 * Copyright (c) 2014 Biopet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
    val shouldBeOutput = new File(
      resourcePath("/overlapmetrics.default.output"))
    output.deleteOnExit()
    FindOverlapMatch.main(
      Array("-i",
            input.getAbsolutePath,
            "-c",
            "0.9",
            "-o",
            output.getAbsolutePath))
    Source.fromFile(output).getLines().toList shouldBe Source
      .fromFile(shouldBeOutput)
      .getLines()
      .toList
  }

  @Test
  def testStdout(): Unit = {
    val input = new File(resourcePath("/overlapmetrics.txt"))
    val shouldBeOutput = new File(
      resourcePath("/overlapmetrics.default.output"))
    val stream = new ByteArrayOutputStream
    Console.withOut(stream) {
      FindOverlapMatch.main(Array("-i", input.getAbsolutePath, "-c", "0.9"))
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
    val shouldBeOutput = new File(
      resourcePath("/overlapmetrics.same_names.output"))
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
      Array("-i",
            input.getAbsolutePath,
            "-c",
            "1.0",
            "-o",
            output.getAbsolutePath,
            "--shouldMatchRegexFile",
            regexFile.getAbsolutePath))
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
            "--shouldMatchRegexFile",
            regexFile.getAbsolutePath))
    Source.fromFile(output).getLines().toList shouldBe Source
      .fromFile(shouldBeOutput)
      .getLines()
      .toList
  }
}

/*
 * Copyright (c) 2014 Sequencing Analysis Support Core - Leiden University Medical Center
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

import java.io.File

import nl.biopet.utils.tool.{AbstractOptParser, ToolCommand}

class ArgsParser(toolCommand: ToolCommand[Args])
    extends AbstractOptParser[Args](toolCommand) {
  opt[File]('i', "input") required () unbounded () valueName "<file>" action {
    (x, c) =>
      c.copy(inputMetrics = x)
  } text "Input should be a table where the first row and column have the ID's, those can be different"
  opt[File]("shouldMatchRegexFile") unbounded () valueName "<file>" action {
    (x, c) =>
      c.copy(shouldMatchRegexFile = Some(x))
  } text "File with regexes what should be the correct matches.\n" +
    "first column is the row samples regex, second column the column regex.\n" +
    "When no second column given first column is used."
  opt[File]('o', "output") unbounded () valueName "<file>" action { (x, c) =>
    c.copy(outputFile = Some(x))
  } text "default to stdout"
  opt[Double]('c', "cutoff") required () unbounded () valueName "<value>" action {
    (x, c) =>
      c.copy(cutoff = x)
  } text "minimum value to report it as pair"
  opt[Unit]("use_same_names") unbounded () valueName "<value>" action {
    (_, c) =>
      c.copy(filterSameNames = false)
  } text "Do not compare samples with the same name"
  opt[Unit]("showBestMatch") unbounded () valueName "<value>" action {
    (_, c) =>
      c.copy(showBestMatch = true)
  } text "Show best match, even when it's below cutoff"
  opt[String]("rowSampleRegex") unbounded () valueName "<regex>" action {
    (x, c) =>
      c.copy(rowSampleRegex = Some(x.r))
  } text "Samples in the row should match this regex"
  opt[String]("columnSampleRegex") unbounded () valueName "<regex>" action {
    (x, c) =>
      c.copy(columnSampleRegex = Some(x.r))
  } text "Samples in the column should match this regex"
}

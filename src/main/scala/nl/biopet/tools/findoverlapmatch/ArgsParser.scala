package nl.biopet.tools.findoverlapmatch

import java.io.File

import nl.biopet.utils.tool.AbstractOptParser

class ArgsParser(cmdName: String) extends AbstractOptParser[Args](cmdName) {
  opt[File]('i', "input") required () unbounded () valueName "<file>" action { (x, c) =>
    c.copy(inputMetrics = x)
  } text "Input should be a table where the first row and column have the ID's, those can be different"
  opt[File]("shouldMatchRegexFile") unbounded () valueName "<file>" action { (x, c) =>
    c.copy(shouldMatchRegexFile = Some(x))
  } text "File with regexes what should be the correct matches.\n" +
    "first column is the row samples regex, second column the column regex.\n" +
    "When no second column given first column is used."
  opt[File]('o', "output") unbounded () valueName "<file>" action { (x, c) =>
    c.copy(outputFile = Some(x))
  } text "default to stdout"
  opt[Double]('c', "cutoff") required () unbounded () valueName "<value>" action { (x, c) =>
    c.copy(cutoff = x)
  } text "minimum value to report it as pair"
  opt[Unit]("use_same_names") unbounded () valueName "<value>" action { (_, c) =>
    c.copy(filterSameNames = false)
  } text "Do not compare samples with the same name"
  opt[Unit]("showBestMatch") unbounded () valueName "<value>" action { (_, c) =>
    c.copy(showBestMatch = true)
  } text "Show best match, even when it's below cutoff"
  opt[String]("rowSampleRegex") unbounded () valueName "<regex>" action { (x, c) =>
    c.copy(rowSampleRegex = Some(x.r))
  } text "Samples in the row should match this regex"
  opt[String]("columnSampleRegex") unbounded () valueName "<regex>" action { (x, c) =>
    c.copy(columnSampleRegex = Some(x.r))
  } text "Samples in the column should match this regex"
}

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

import java.io.PrintStream

import nl.biopet.utils.tool.ToolCommand

import scala.collection.mutable.ListBuffer
import scala.io.Source

object FindOverlapMatch extends ToolCommand[Args] {
  def emptyArgs: Args = Args()
  def argsParser = new ArgsParser(this)
  def main(args: Array[String]): Unit = {
    val cmdArgs = cmdArrayToArgs(args)

    logger.info("Start")

    val reader = Source.fromFile(cmdArgs.inputMetrics)

    logger.info("Reading overlap file - Start")
    val data = reader.getLines().map(_.split("\t")).toArray
    logger.info("Reading overlap file - Done")

    val samplesColumnHeader = {
      val header = data.headOption
        .getOrElse(throw new IllegalArgumentException("File is empty"))
        .zipWithIndex
      require(header.length > 1, "No Samples found in the header")
      header.tail
    }
    val samplesRowHeader = data.map(_.headOption.getOrElse(throw new IllegalArgumentException("first column should always be filled"))).zipWithIndex.tail

    var overlap = 0
    var multiOverlap = 0
    var noOverlap = 0
    var correctMatches = 0
    var incorrectMatches = 0

    val writer = cmdArgs.outputFile match {
      case Some(file) => new PrintStream(file)
      case _          => Console.out
    }

    val matchesRegexes = cmdArgs.shouldMatchRegexFile.map { file =>
      val reader = Source.fromFile(file)
      val regexes = reader
        .getLines()
        .filter(_.nonEmpty)
        .map { line =>
          val values = line.split("\t").map(_.r)
          values.head -> values.lift(1).getOrElse(values.head)
        }
        .toList
      reader.close()
      regexes
    }

    for ((columnSampleName, columnSampleId) <- samplesColumnHeader
         if cmdArgs.columnSampleRegex.forall(
           _.findFirstIn(columnSampleName).isDefined)) {

      val buffer = ListBuffer[(String, Double)]()
      val usedRows = samplesRowHeader
        .filter(!cmdArgs.filterSameNames || _._2 != columnSampleId)
        .filter {
          case (name, id) =>
            cmdArgs.rowSampleRegex.forall(_.findFirstIn(name).isDefined)
        }
      for (rowSample <- usedRows) {
        val value = data(columnSampleId)(rowSample._2).toDouble
        if (value >= cmdArgs.cutoff && (!cmdArgs.filterSameNames || columnSampleId != rowSample._2)) {
          buffer.+=((rowSample._1, value))
        }
      }

      if (buffer.nonEmpty) overlap += 1
      else noOverlap += 1
      if (buffer.size > 1) multiOverlap += 1

      if (buffer.isEmpty && cmdArgs.showBestMatch) {
        val max = usedRows.map(x => data(columnSampleId)(x._2).toDouble).max
        samplesRowHeader
          .filter(x => data(columnSampleId)(x._2).toDouble == max)
          .foreach {
            case (name, _) =>
              buffer.+=((name, max))
          }
      }

      matchesRegexes.foreach { regexes =>
        regexes
          .find(_._1.findFirstMatchIn(columnSampleName).isDefined)
          .foreach {
            case (_, regex2) =>
              val max = if (buffer.isEmpty) 0.0 else buffer.map(_._2).max
              if (buffer
                    .filter(_._2 == max)
                    .exists(x => regex2.findFirstMatchIn(x._1).isDefined)) {
                correctMatches += 1
              } else {
                if (buffer.nonEmpty)
                  logger.warn(
                    s"Incorrect match found, sample: $columnSampleName")
                incorrectMatches += 1
                usedRows
                  .filter(x => regex2.findFirstIn(x._1).isDefined)
                  .foreach(x =>
                    buffer.+=((x._1, data(columnSampleId)(x._2).toDouble)))
              }
          }
      }

      (columnSampleName :: buffer.map(_.toString()).toList).mkString("\t")

      writer.println(
        (columnSampleName :: buffer.map(_.toString()).toList).mkString("\t"))
    }
    cmdArgs.outputFile.foreach(_ => writer.close())
    if (matchesRegexes.isDefined) {
      logger.info(s"$correctMatches correct matches found")
      logger.info(s"$incorrectMatches incorrect matches found")
    }
    logger.info(s"$overlap found")
    logger.info(s"no $noOverlap found")
    logger.info(s"multi $multiOverlap found")
    logger.info("Done")
  }

  def descriptionText: String =
    s"""
       |$toolName looks for overlapping samples in a sample matrix. An example of
       |a tool that creates such a matrix is
       |[VcfStats](https://github.com/biopet/vcfstats).
       |
       |It compares samples and lists similar samples based on a cutoff point.
       |It can also check if columns in a sample matrix match a certain regex.
     """.stripMargin

  def manualText: String =
    s"""
       |
       |Input can be a text file like the following input file:
       |
       |    	      sample1	sample2	sample3
       |    sample1	1.0	    0.5	    0.9
       |    sample2	0.5	    1.0	    0.5
       |    sample3	0.9	    0.5	    1.0
       |
       |
     """.stripMargin

  def exampleText: String =
    s"""
       |To check above example with threshold 0.9:
       |${example("-i", "input.txt", "-c", "0.9", "-o", "output.txt")}
       |
       |Will yield the following file:
       |
       |    sample1	(sample3,0.9)
       |    sample2
       |    sample3	(sample1,0.9)
       |
       |With `--use_same_names` set it should be:
       |
       |
       |    sample1	(sample1,1.0)	(sample3,0.9)
       |    sample2	(sample2,1.0)
       |    sample3	(sample1,0.9)	(sample3,1.0)
       |
     """.stripMargin
}

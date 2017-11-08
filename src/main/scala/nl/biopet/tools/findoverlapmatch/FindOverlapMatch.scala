package nl.biopet.tools.findoverlapmatch

import java.io.PrintStream

import nl.biopet.utils.tool.ToolCommand

import scala.collection.mutable.ListBuffer
import scala.io.Source

object FindOverlapMatch extends ToolCommand[Args] {
  def emptyArgs: Args = Args()
  def argsParser = new ArgsParser(toolName)
  def main(args: Array[String]): Unit = {
    val cmdArgs = cmdArrayToArgs(args)

    logger.info("Start")

    val reader = Source.fromFile(cmdArgs.inputMetrics)

    logger.info("Reading overlap file - Start")
    val data = reader.getLines().map(_.split("\t")).toArray
    logger.info("Reading overlap file - Done")

    val samplesColumnHeader = data.head.zipWithIndex.tail
    val samplesRowHeader = data.map(_.head).zipWithIndex.tail

    var overlap = 0
    var multiOverlap = 0
    var noOverlap = 0
    var correctMatches = 0
    var incorrectMatches = 0

    val writer = cmdArgs.outputFile match {
      case Some(file) => new PrintStream(file)
      case _ => sys.process.stdout
    }

    val matchesRegexes = cmdArgs.shouldMatchRegexFile.map { file =>
      val reader = Source.fromFile(file)
      val regexes = reader
        .getLines()
        .map { line =>
          val values = line.split("\t").map(_.r)
          values.head -> values.lift(1).getOrElse(values.head)
        }
        .toList
      reader.close()
      regexes
    }

    for ((columnSampleName, columnSampleId) <- samplesColumnHeader
         if cmdArgs.columnSampleRegex.forall(_.findFirstIn(columnSampleName).isDefined)) {

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
        regexes.find(_._1.findFirstMatchIn(columnSampleName).isDefined).foreach {
          case (_, regex2) =>
            val max = if (buffer.isEmpty) 0.0 else buffer.map(_._2).max
            if (buffer.filter(_._2 == max).exists(x => regex2.findFirstMatchIn(x._1).isDefined)) {
              correctMatches += 1
            } else {
              if (buffer.nonEmpty) logger.warn(s"Incorrect match found, sample: $columnSampleName")
              incorrectMatches += 1
              usedRows
                .filter(x => regex2.findFirstIn(x._1).isDefined)
                .foreach(x => buffer.+=((x._1, data(columnSampleId)(x._2).toDouble)))
            }
        }
      }

      writer.println(s"$columnSampleName\t${buffer.mkString("\t")}")
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
}

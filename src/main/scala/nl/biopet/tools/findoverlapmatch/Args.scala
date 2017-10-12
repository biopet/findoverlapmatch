package nl.biopet.tools.findoverlapmatch

import java.io.File

import scala.util.matching.Regex

case class Args(inputMetrics: File = null,
                outputFile: Option[File] = None,
                cutoff: Double = 0.0,
                shouldMatchRegexFile: Option[File] = None,
                showBestMatch: Boolean = false,
                filterSameNames: Boolean = true,
                rowSampleRegex: Option[Regex] = None,
                columnSampleRegex: Option[Regex] = None)

package nl.biopet.tools.findoverlapmatch

import nl.biopet.utils.tool.ToolCommand

object FindOverlapMatch extends ToolCommand {
  def main(args: Array[String]): Unit = {
    val parser = new ArgsParser(toolName)
    val cmdArgs =
      parser.parse(args, Args()).getOrElse(throw new IllegalArgumentException)

    logger.info("Start")

    //TODO: Execute code

    logger.info("Done")
  }
}

organization := "com.github.biopet"
organizationName := "Biopet"

startYear := Some(2014)

name := "FindOverlapMatch"
biopetUrlName := "findoverlapmatch"

biopetIsTool := true

mainClass in assembly := Some(
  "nl.biopet.tools.findoverlapmatch.FindOverlapMatch")

developers := List(
  Developer(id = "ffinfo",
            name = "Peter van 't Hof",
            email = "pjrvanthof@gmail.com",
            url = url("https://github.com/ffinfo")),
  Developer(id = "rhpvorderman",
            name = "Ruben Vorderman",
            email = "r.h.p.vorderman@lumc.nl",
            url = url("https://github.com/rhpvorderman"))
)

scalaVersion := "2.11.12"

libraryDependencies += "com.github.biopet" %% "tool-utils" % "0.6"
libraryDependencies += "com.github.biopet" %% "tool-test-utils" % "0.3"

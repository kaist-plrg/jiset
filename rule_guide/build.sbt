lazy val root = (project in file(".")).
  settings(
      name := "NLPjse",
      version := "1.0",
      scalaVersion := "2.12.8",
      libraryDependencies += "com.codecommit" %% "gll-combinators" % "2.3",
      libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test",
      scalacOptions ++= Seq("-feature")
      )

retrieveManaged := true

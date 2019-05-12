lazy val grammarConvert = taskKey[Unit]("Task to convert rule file to scala parser combinator")
lazy val compileGrammar = taskKey[Unit]("Convert grammar and compile")
lazy val root = (project in file(".")).
  settings(
    name := "ASE",
    version := "1.0",
    scalaVersion := "2.12.8",
    grammarConvert := {
        ConvertUtil.run
    },
    compileGrammar in Compile := Def.sequential(
      grammarConvert in Compile,
      compile in Compile
    ).value
  )

libraryDependencies ++= Seq(
  "com.codecommit" %% "gll-combinators" % "2.3",
  "io.spray" %% "spray-json" % "1.3.2",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

scalacOptions in ThisBuild ++= Seq("-deprecation", "-feature",
                                   "-language:postfixOps",
                                   "-language:implicitConversions")

javacOptions ++= Seq("-encoding", "UTF-8")

retrieveManaged := true

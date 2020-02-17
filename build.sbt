import scalariform.formatter.preferences._

ThisBuild / version       := "1.0"
ThisBuild / scalaVersion  := "2.13.1"
ThisBuild / organization  := "kr.ac.kaist.jiset"
ThisBuild / scalacOptions := Seq(
  "-deprecation", "-feature", "-language:postfixOps",
  "-language:implicitConversions"
)
ThisBuild / javacOptions ++= Seq(
  "-encoding", "UTF-8"
)

lazy val grammarDiffTest = taskKey[Unit]("Launch tests for GrammarDiff")
lazy val algoCompilerTest = taskKey[Unit]("Launch tests for AlgoCompiler")
lazy val algoCompilerDiffTest = taskKey[Unit]("Launch tests for AlgoCompilerDiff")

lazy val ires = ProjectRef(file("ires"), "ires")

lazy val jiset = (project in file("."))
  .dependsOn(ires)
  .settings(
    name := "JISET",
    libraryDependencies ++= Seq(
      "io.spray" %% "spray-json" % "1.3.5",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
      "org.scalatest" %% "scalatest" % "3.0.8" % "test",
      "org.jline" % "jline" % "3.13.1"
    ),
    testOptions in Test += Tests.Argument("-fDG", baseDirectory.value + "/tests/detail"),
    retrieveManaged := true,
    scalariformPreferences := scalariformPreferences.value
      .setPreference(DanglingCloseParenthesis, Force)
      .setPreference(DoubleIndentConstructorArguments, true),
    test in assembly := {},
    testOptions in Test += Tests.Argument("-fDG", baseDirectory.value + "/tests/detail"),
    parallelExecution in Test := true,
    grammarDiffTest := (testOnly in Test).toTask(" kr.ac.kaist.jiset.GrammarDiffTest").value,
    algoCompilerTest := (testOnly in Test).toTask(" kr.ac.kaist.jiset.AlgoCompilerTest").value,
    algoCompilerDiffTest := (testOnly in Test).toTask(" kr.ac.kaist.jiset.AlgoCompilerDiffTest").value,
    test in assembly := {}
  )

import scalariform.formatter.preferences._
import sbtassembly.AssemblyPlugin.defaultUniversalScript

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

lazy val largeCompileTest = taskKey[Unit]("Launch tests for compile (large)")

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
      "org.jline" % "jline" % "3.13.3"
    ),
    test in assembly := {},
    testOptions in Test += Tests.Argument("-fDG", baseDirectory.value + "/tests/detail"),
    retrieveManaged := true,
    scalariformPreferences := scalariformPreferences.value
      .setPreference(DanglingCloseParenthesis, Force)
      .setPreference(DoubleIndentConstructorArguments, true),
    parallelExecution in Test := true,
    assemblyOutputPath in assembly := file("bin/jiset"),
    assemblyOption in assembly := (assemblyOption in assembly).value
      .copy(prependShellScript = Some(defaultUniversalScript(shebang = false))),
    largeCompileTest := (testOnly in Test).toTask(" kr.ac.kaist.jiset.LargeCompileTest").value
  )

commands += Command.command("generateModel") { state =>
  s"run gen-model" ::
  "compile" ::
  state
}

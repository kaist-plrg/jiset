import scalariform.formatter.preferences._
import sbtassembly.AssemblyPlugin.defaultUniversalScript

ThisBuild / version       := "1.0"
ThisBuild / scalaVersion  := "2.13.1"
ThisBuild / organization  := "kr.ac.kaist.jiset"
ThisBuild / scalacOptions := Seq(
  "-deprecation", "-feature", "-language:postfixOps",
  "-language:implicitConversions", "-language:existentials", "-language:reflectiveCalls"
)
ThisBuild / javacOptions ++= Seq(
  "-encoding", "UTF-8"
)

// automatic reload
Global / onChangedBuildSource := ReloadOnSourceChanges

// size
lazy val smallTest = taskKey[Unit]("Launch small tests (maybe seconds)")
lazy val middleTest = taskKey[Unit]("Launch middle tests (maybe minutes)")
lazy val largeTest = taskKey[Unit]("Launch large tests (may hours)")

// grammar
lazy val grammarTest = taskKey[Unit]("Launch grammar tests")
lazy val grammarBasicTest = taskKey[Unit]("Launch basic grammar tests (small)")

// compile
lazy val compileTest = taskKey[Unit]("Launch compile tests")
lazy val compileBasicTest = taskKey[Unit]("Launch basic compile tests (middle)")
lazy val compileLegacyTest = taskKey[Unit]("Launch legacy compile tests (small)")
lazy val compileManualTest = taskKey[Unit]("Launch manual compile tests (small)")

// ires
lazy val ires = ProjectRef(file("ires"), "ires")

// jiset
lazy val jiset = (project in file("."))
  .dependsOn(ires)
  .settings(
    name := "JISET",
    libraryDependencies ++= Seq(
      "io.spray" %% "spray-json" % "1.3.5",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
      "org.scalatest" %% "scalatest" % "3.0.8" % "test",
      "org.jsoup" % "jsoup" % "1.13.1",
      "org.jline" % "jline" % "3.13.3",
      "org.apache.commons" % "commons-text" % "1.8",
      "com.chuusai" %% "shapeless" % "2.4.0-M1"
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
    // size
    smallTest := (testOnly in Test).toTask(" *SmallTest").value,
    middleTest := (testOnly in Test).toTask(" *MiddleTest").value,
    largeTest := (testOnly in Test).toTask(" *LargeTest").value,
    // gramamr
    grammarTest := (testOnly in Test).toTask(" *.grammar.*Test").value,
    grammarBasicTest := (testOnly in Test).toTask(" *.grammar.Basic*Test").value,
    // compile
    compileTest := (testOnly in Test).toTask(" *.compile.*Test").value,
    compileBasicTest := (testOnly in Test).toTask(" *.compile.Basic*Test").value,
    compileLegacyTest := (testOnly in Test).toTask(" *.compile.Legacy*Test").value,
    compileManualTest := (testOnly in Test).toTask(" *.compile.Manual*Test").value
  )

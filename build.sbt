import scalariform.formatter.preferences._
import sbtassembly.AssemblyPlugin.defaultUniversalScript

ThisBuild / version       := "1.0"
ThisBuild / scalaVersion  := "2.13.1"
ThisBuild / organization  := "kr.ac.kaist.jiset"
ThisBuild / useSuperShell := false
ThisBuild / scalacOptions := Seq(
  "-deprecation", "-feature", "-language:postfixOps",
  "-language:implicitConversions", "-language:existentials",
  "-language:reflectiveCalls"
)
ThisBuild / javacOptions ++= Seq(
  "-encoding", "UTF-8"
)

// automatic reload
Global / onChangedBuildSource := ReloadOnSourceChanges

// size
lazy val tinyTest = taskKey[Unit]("Launch tiny tests (maybe milliseconds)")
lazy val smallTest = taskKey[Unit]("Launch small tests (maybe seconds)")
lazy val middleTest = taskKey[Unit]("Launch middle tests (maybe minutes)")
lazy val largeTest = taskKey[Unit]("Launch large tests (may hours)")

// extract
lazy val extractTest = taskKey[Unit]("Launch extract tests")
lazy val extractTokenTest = taskKey[Unit]("Launch token extract tests (tiny)")
lazy val extractJsonTest = taskKey[Unit]("Launch json extract tests (small)")
lazy val extractGrammarTest = taskKey[Unit]("Launch grammar extract tests (small)")

// ir
lazy val irTest = taskKey[Unit]("Launch ir tests")
lazy val irParseTest = taskKey[Unit]("Launch parse ir tests (tiny)")
lazy val irBeautifierTest = taskKey[Unit]("Launch beautifier ir tests (tiny)")
lazy val irEvalTest = taskKey[Unit]("Launch eval ir tests (small)")

// js
lazy val jsTest = taskKey[Unit]("Launch js tests")
lazy val jsParseTest = taskKey[Unit]("Launch parse js tests (small)")
lazy val jsEvalTest = taskKey[Unit]("Launch eval js tests (small)")

// test262
lazy val test262ParseTest = taskKey[Unit]("Launch parse test262 tests (large)")
lazy val test262EvalTest = taskKey[Unit]("Launch eval test262 tests (large)")
lazy val test262ManualTest = taskKey[Unit]("Launch manual eval test262 tests (large)")

// jiset
lazy val jiset = (project in file("."))
  .settings(
    name := "JISET",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % "0.14.1",
      "io.circe" %%% "circe-generic" % "0.14.1",
      "io.circe" %%% "circe-parser" % "0.14.1",
      "org.scala-lang.modules" %%% "scala-parser-combinators" % "1.1.2",
      "org.scalatest" %% "scalatest" % "3.0.8" % "test",
      "org.jsoup" % "jsoup" % "1.13.1",
      "org.jline" % "jline" % "3.13.3",
      "org.apache.commons" % "commons-text" % "1.8",
      "org.scala-js" %% "scalajs-stubs" % "1.0.0" % "provided",
    ),
    retrieveManaged := true,
    // test setting
    testOptions in Test += Tests.Argument("-fDG", baseDirectory.value + "/tests/detail"),
    parallelExecution in Test := true,
    // scalariform setting
    scalariformPreferences := scalariformPreferences.value
      .setPreference(DanglingCloseParenthesis, Force)
      .setPreference(DoubleIndentConstructorArguments, false),
    // basic tests
    test := (testOnly in Test).toTask(List(
      "*TinyTest",
      "*SmallTest",
      "*MiddleTest",
    ).mkString(" ", " ", "")).value,
    // assembly setting
    test in assembly := {},
    assemblyOutputPath in assembly := file("bin/jiset"),
    assemblyOption in assembly := (assemblyOption in assembly).value
      .copy(prependShellScript = Some(defaultUniversalScript(shebang = false))),
    // size
    tinyTest := (testOnly in Test).toTask(" *TinyTest").value,
    smallTest := (testOnly in Test).toTask(" *SmallTest").value,
    middleTest := (testOnly in Test).toTask(" *MiddleTest").value,
    largeTest := (testOnly in Test).toTask(" *LargeTest").value,
    // extract
    extractTest := (testOnly in Test).toTask(" *.extract.*Test").value,
    extractTokenTest := (testOnly in Test).toTask(" *.extract.Token*Test").value,
		extractJsonTest := (testOnly in Test).toTask(" *.extract.Json*Test").value,
    extractGrammarTest := (testOnly in Test).toTask(" *.extract.Grammar*Test").value,
    // ir
    irTest := (testOnly in Test).toTask(" *.ir.*Test").value,
    irParseTest := (testOnly in Test).toTask(" *.ir.Parse*Test").value,
		irBeautifierTest := (testOnly in Test).toTask(" *.ir.Beautifier*Test").value,
    irEvalTest := (testOnly in Test).toTask(" *.ir.Eval*Test").value,
    // js
    jsTest := (testOnly in Test).toTask(" *.js.*Test").value,
    jsParseTest := (testOnly in Test).toTask(" *.js.Parse*Test").value,
    jsEvalTest := (testOnly in Test).toTask(" *.js.Eval*Test").value,
    // test262
    test262ParseTest := (testOnly in Test).toTask(" *.test262.Parse*Test").value,
    test262EvalTest := (testOnly in Test).toTask(" *.test262.Eval*Test").value,
    test262ManualTest := (testOnly in Test).toTask(" *.test262.Manual*Test").value
  )

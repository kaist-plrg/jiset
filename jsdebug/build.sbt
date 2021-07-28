import java.io.File
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

// for Scala.js
enablePlugins(ScalaJSPlugin)
// scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) }

// command for create and copy JavaScript files
commands += Command.command("copyJS") { state =>
  val JS_FILE=s"./target/scala-2.13/jiset-fastopt/main.js"
  val WEB_PATH=s"./web/public/jiset.js"
  sbt.io.IO.copyFile(new File(JS_FILE), new File(WEB_PATH))
  state
}
commands += Command.command("dumpJS") { state =>
  "fastLinkJS" ::
  "copyJS" ::
  state
}

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
    // scalariform setting
    scalariformPreferences := scalariformPreferences.value
      .setPreference(DanglingCloseParenthesis, Force)
      .setPreference(DoubleIndentConstructorArguments, false)
  )

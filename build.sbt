import java.io.File

lazy val dummyModel = taskKey[Unit]("Generates a dummy model.")
lazy val coreTest = taskKey[Unit]("Launch core language interpreter tests")
lazy val grammarDiffTest = taskKey[Unit]("Launch tests for GrammarDiff")
lazy val algoCompilerTest = taskKey[Unit]("Launch tests for AlgoCompiler")
lazy val algoCompilerDiffTest = taskKey[Unit]("Launch tests for AlgoCompilerDiff")
lazy val jsTest = taskKey[Unit]("Launch js language interpreter tests")
lazy val test262Test = taskKey[Unit]("Launch test262 tests")
lazy val test262PropTest = taskKey[Unit]("Launch test262 tests")
lazy val test262ParseTest = taskKey[Unit]("Launch test262 parsing tests")
lazy val test262AllParseTest = taskKey[Unit]("Launch test262 parsing tests")

lazy val root = (project in file(".")).
  settings(
    name := "ASE",
    version := "1.0",
    organization := "kr.ac.kaist.ase",
    scalaVersion := "2.12.8",
    dummyModel in Compile := {
      val srcDir = baseDirectory.value + "/src/main"
      val modelPath = s"$srcDir/scala/kr/ac/kaist/ase/model"
      val modelDir = file(modelPath)
      if (!modelDir.exists) {
        IO.createDirectory(modelDir)
        List("ast", "algorithm", "type").foreach(dirname => {
          IO.createDirectory(file(s"$modelPath/$dirname"))
        })
        List("package").foreach(filename => {
          val outFile = file(s"$modelPath/$filename.scala")
          IO.copyFile(
            file(s"$srcDir/resources/dummy/$filename.scala"),
            file(s"$modelPath/$filename.scala")
          )
        })
      }
    },
    testOptions in Test += Tests.Argument("-fDG", baseDirectory.value + "/tests/detail"),
    compile <<= (compile in Compile) dependsOn (dummyModel in Compile),
    test <<= (testOnly in Test).toTask(List(
      "kr.ac.kaist.ase.BasicCoreTest",
      "kr.ac.kaist.ase.JSTest"
    ).mkString(" ", " ", "")) dependsOn compile,
    coreTest <<= (testOnly in Test).toTask(" kr.ac.kaist.ase.BasicCoreTest") dependsOn compile,
    grammarDiffTest <<= (testOnly in Test).toTask(" kr.ac.kaist.ase.GrammarDiffTest") dependsOn compile,
    algoCompilerTest <<= (testOnly in Test).toTask(" kr.ac.kaist.ase.AlgoCompilerTest") dependsOn compile,
    algoCompilerDiffTest <<= (testOnly in Test).toTask(" kr.ac.kaist.ase.AlgoCompilerDiffTest") dependsOn compile,
    jsTest <<= (testOnly in Test).toTask(" kr.ac.kaist.ase.JSTest") dependsOn compile,
    test262Test <<= (testOnly in Test).toTask(" kr.ac.kaist.ase.Test262Test") dependsOn compile,
    test262PropTest <<= (testOnly in Test).toTask(" kr.ac.kaist.ase.Test262PropTest") dependsOn compile,
    test262ParseTest <<= (testOnly in Test).toTask(" kr.ac.kaist.ase.Test262ParseTest") dependsOn compile,
    test262AllParseTest <<= (testOnly in Test).toTask(" kr.ac.kaist.ase.Test262AllParseTest") dependsOn compile
  )

cleanFiles ++= Seq(
  file("src/main/scala/kr/ac/kaist/ase/model"),
  file("src/main/scala/kr/ac/kaist/ase/algorithm/rule")
)

libraryDependencies ++= Seq(
  "com.codecommit" %% "gll-combinators" % "2.3",
  "io.spray" %% "spray-json" % "1.3.5",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.jline" % "jline" % "3.10.0"
)

commands += Command.command("generateModel") { state =>
    "compile" ::
    s"run preprocess" ::
    "compile" ::
    s"run gen-model" ::
    "compile" ::
    state
}

commands += Command.command("updateAndTestModel") { state =>
    s"run gen-model" ::
    "test" ::
    "algoCompilerDiffTest" ::
    state
}

scalacOptions in ThisBuild ++= Seq("-deprecation", "-feature",
                                   "-language:postfixOps",
                                   "-language:implicitConversions")

javacOptions ++= Seq("-encoding", "UTF-8")

retrieveManaged := true

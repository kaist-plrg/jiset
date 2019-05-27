import java.io.File

lazy val dummyModel = taskKey[Unit]("Generates a dummy model.")

lazy val ES_MODEL = "es2018"

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
    compile <<= (compile in Compile) dependsOn (dummyModel in Compile)
  )

cleanFiles ++= Seq(
  file("src/main/scala/kr/ac/kaist/ase/model"),
  file("src/main/scala/kr/ac/kaist/ase/algorithm/rule")
)

libraryDependencies ++= Seq(
  "com.codecommit" %% "gll-combinators" % "2.3",
  "io.spray" %% "spray-json" % "1.3.2",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.jline" % "jline" % "3.10.0"
)

commands += Command.command("generateModel") { state =>
  "clean" ::
    "compile" ::
    s"run gen-model $ES_MODEL" ::
    "compile" ::
    state
}

scalacOptions in ThisBuild ++= Seq("-deprecation", "-feature",
                                   "-language:postfixOps",
                                   "-language:implicitConversions")

javacOptions ++= Seq("-encoding", "UTF-8")

retrieveManaged := true

import java.io.File

lazy val dummyModel = taskKey[Unit]("Generates a dummy model.")

lazy val root = (project in file(".")).
  settings(
    name := "ASE",
    version := "1.0",
    organization := "kr.ac.kaist.ase",
    scalaVersion := "2.12.8",
    dummyModel in Compile := {
      val srcDir = baseDirectory.value + "/src/main"
      val inFile = file(srcDir + "/resources/package.scala")
      val outPath = srcDir + "/scala/kr/ac/kaist/ase/model"
      val outDir = file(outPath)
      if (!outDir.exists) {
        IO.createDirectory(outDir)
        IO.copyFile(inFile, file(outPath + "/package.scala"))
      }
    },
    compile <<= (compile in Compile) dependsOn (dummyModel in Compile)
  )

lazy val model = file("src/main/scala/kr/ac/kaist/ase/model")
cleanFiles ++= Seq(model)

libraryDependencies ++= Seq(
  "com.codecommit" %% "gll-combinators" % "2.3",
  "io.spray" %% "spray-json" % "1.3.2",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.jline" % "jline" % "3.10.0"
)

scalacOptions in ThisBuild ++= Seq("-deprecation", "-feature",
                                   "-language:postfixOps",
                                   "-language:implicitConversions")

javacOptions ++= Seq("-encoding", "UTF-8")

retrieveManaged := true

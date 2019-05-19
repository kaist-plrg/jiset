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
      val outPath = s"$srcDir/scala/kr/ac/kaist/ase"
      def createSrcDir(dirname: String): Boolean = {
        val outDir = file(s"$outPath/$dirname")
        if (!outDir.exists) {
          IO.createDirectory(outDir)
          false
        } else true
      }
      def copySrc(dirname: String, name: String): Unit = if (!createSrcDir(dirname)) {
        val outFile = file(s"$outPath/$dirname/$name.scala")
        IO.copyFile(
          file(s"$srcDir/resources/$name.scala"),
          file(s"$outPath/$dirname/$name.scala")
        )
      }
      copySrc("algorithm/rule", "Stmt")
      copySrc("model", "package")
      createSrcDir("model/algorithm")
      createSrcDir("model/type")
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
    s"run gen-algo-parser $ES_MODEL" ::
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

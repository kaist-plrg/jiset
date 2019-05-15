package kr.ac.kaist.ase.phase

import java.io.File
import kr.ac.kaist.ase.error.NoFileError
import kr.ac.kaist.ase.parser._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.{ ASEConfig, BASE_DIR }
import scala.util.parsing.combinator._

// GenAlgoParser phase
case object GenAlgoParser extends PhaseObj[Unit, GenAlgoParserConfig, Unit] {
  val name: String = "gen-algo-parser"
  val help: String = "generates algorithm parsers."

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: GenAlgoParserConfig
  ): Unit = {
    val filename = getFirstFilename(aseConfig, "gen-algo-parser")
    val ruleFiler = extFilter("rule")
    for (file <- walkTree(new File(s"$BASE_DIR/src/main/resources/$filename/rule/"))) {
      val filename = file.getName
      if (ruleFiler(filename)) {
        val name = filename.substring(0, filename.length - 5)
        val input = file.getAbsolutePath
        val output = s"$BASE_DIR/src/main/scala/kr/ac/kaist/ase/algorithm/rule/${name}.scala"

        val rules = RuleParser.getList(fileReader(input))

        val pw = getPrintWriter(output)
        pw.println("package kr.ac.kaist.ase.algorithm")
        pw.println
        pw.println(s"sealed trait $name")

        // rules.foreach {
        //   case Rule(subname, subrule) =>
        //     val stype = subrule.collect { case NonTermainl(ty, _) => ty }
        //       .zipWithIndex.map { case (t, i) => s"x${i}: $t" }.mkString(", ")
        //     pw.println(s"case class ${subname}($stype) extends $name")
        // }

        pw.println(s"trait ${name}Parsers { this: AlgorithmParsers =>")
        // rules.foreach {
        //   case Rule(subname, subrule) =>
        //     pw.println(s"def ${subname.head.toLower + subname.tail}: Parser[$subname] = {")
        //     val rulelist = subrule.foldLeft(List[String]()) {
        //       case (Nil, Terminal(st)) => List("\"\" " + st)
        //       case (Nil, NonTermainl(_, st)) => List("\"\"", st)
        //       case (rest :+ s, Terminal(st)) => rest :+ (s + st)
        //       case (rest, NonTermainl(_, t)) => rest :+ t
        //     }
        //     val rulestr = rulelist.map { s => s"($s)" }.mkString(" ~ ")
        //     val paramlist = List.tabulate(rulelist.length - 1)(i => s"e${i}")

        //     val argstr = paramlist.mkString(" ~ ")
        //     val paramstr = paramlist.mkString(", ")
        //     pw.println(s"${rulestr} ^^ {")
        //     pw.println(s"case _ ~ ${argstr} => ${subname}(${paramstr})")
        //     pw.println("}")
        //     pw.println("}")
        // }
        pw.println(s"""lazy val ${name.head.toLower + name.tail} = failure("")""")
        // pw.println(rules.map { case Rule(subname, _) => s"${subname.head.toLower + subname.tail}" }.mkString(" |\n"))
        pw.println("}")
        pw.close()
      }
    }
  }

  def defaultConfig: GenAlgoParserConfig = GenAlgoParserConfig()
  val options: List[PhaseOption[GenAlgoParserConfig]] = List()
}

// GenAlgoParser phase config
case class GenAlgoParserConfig() extends Config

package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._

case class NonRecursiveSamplerGenerator(
    packageName: String,
    modelDir: String,
    grammar: Grammar,
    debug: Boolean
) {
  val nf = getPrintWriter(s"$modelDir/NonRecursiveSampler.scala")
  val Grammar(lexProds, prods) = grammar
  val targetProds = prods.filter(!_.lhs.isModule)

  def getSampler(prod: Production): Unit = {
    val Production(lhs, rhsList) = prod
    val Lhs(name, rawParams) = lhs
    val params = rawParams.map("p" + _)
    val paramsStr = params.map(_ + ": Boolean").mkString(", ")
    val argStr = params.mkString(", ")

    // all
    nf.println(s"""  def $name($paramsStr): ScalaSet[$name] = {""")
    nf.println(s"""    if (!visited.contains("$name", List($argStr))) {""")
    nf.println(s"""      visited += (("$name", List($argStr)))""")
    nf.println(s"""      var s = ScalaSet[$name]()""")
    rhsList.zipWithIndex.foreach {
      case (rhs, i) => getCandidates(name, params, rhs, i)
    }
    nf.println(s"""      s""")
    nf.println(s"""    } else {""")
    nf.println(s"""      val str = MinSampler.$name(${params.mkString(", ")})""")
    nf.println(s"""      val sample = JSParser.parse(JSParser.$name(List(${params.mkString(", ")})), str).get""")
    nf.println(s"""      ScalaSet(sample)""")
    nf.println(s"""    }""")
    nf.println(s"""  }""")
  }

  def getCandidates(name: String, bs: List[String], rhs: Rhs, idx: Int): Unit = {
    if (!rhs.containsModuleNT) {
      val Rhs(tokens, cond) = rhs
      val space = if (cond != "") "        " else "      "
      if (cond != "") nf.println(s"      if ($cond) {")
      val tokenInfo = tokens.zipWithIndex.flatMap {
        case (token, i) => tokenSampler(token) match {
          case Some((name, argsStr, optional)) =>
            val id = s"${name}_${idx}_${i}"
            nf.println(space + s"val $id = $name($argsStr).toList")
            Some((id, s"$name($argsStr).head", optional))
          case None => None
        }
      }
      val (tokenIds, tokenDefaults, tokenOptional) = tokenInfo.unzip3
      if (tokenIds.isEmpty)
        nf.println(space + s"s += $name$idx(List(${bs.mkString(", ")}))")
      else {
        nf.println(space + s"val max${idx} = List(${tokenIds.mkString(", ")}).map(_.length).max")
        nf.println(space + s"for (i <- (0 until max${idx})) {")
        tokenIds.zipWithIndex.foreach {
          case (tid, i) => {
            nf.println(space + s"  val arg$i = $tid.applyOrElse(i, (_: Int) => ${tokenDefaults(i)})")
          }
        }

        var argsList = tokenOptional.zipWithIndex.foldLeft(List(List[String]())) {
          case (argsList, (optional, i)) => {
            val argStr = s"arg$i"
            if (optional) argsList.flatMap(l => List(l :+ s"Some($argStr)", l :+ "None"))
            else argsList.map(_ :+ argStr)
          }
        }

        argsList.foreach((args: List[String]) => {
          val argStr = args.mkString(", ")
          nf.println(space + s"  s += $name$idx($argStr, List(${bs.mkString(", ")}))")
        })
        nf.println(space + s"}")
      }

      if (cond != "") nf.println(s"      }")
    }
  }

  lazy val paramMap: Map[String, List[String]] =
    prods.map(prod => prod.lhs.name -> prod.lhs.params).toMap
  def getArgs(name: String, args: List[String]): List[String] = {
    val params = paramMap.getOrElse(name, Nil)
    val argMap = args.foldLeft(Map[String, String]()) {
      case (m, arg) =>
        val dropped = arg.drop(1)
        m + {
          if (arg.startsWith("~")) dropped -> "false"
          else if (arg.startsWith("+")) dropped -> "true"
          else if (arg.startsWith("?")) dropped -> ("p" + dropped)
          else dropped -> "false"
        }
    }
    params.map(p => argMap.getOrElse(p, "false"))
  }
  def tokenSampler(token: Token): Option[(String, String, Boolean)] = token match {
    case NonTerminal(name, params, optional) => {
      val args = getArgs(name, params)
      val argsStr = args.mkString(", ")
      Some((name, argsStr, optional))
    }
    case ButNot(base, _) => tokenSampler(base)
    case _ => None
  }

  nf.println(s"""package $packageName.model""")
  nf.println
  nf.println(s"""import $packageName.Lexical""")
  nf.println(s"""import $packageName.ir._""")
  nf.println(s"""import $packageName.model.{ Parser => JSParser }""")
  nf.println(s"""import $packageName.util.Useful._""")
  nf.println(s"""import scala.collection.immutable.{ Set => ScalaSet }""")
  nf.println
  nf.println(s"""class NonRecursiveSampler {""")
  nf.println(s"""  var visited: ScalaSet[(String, List[Boolean])] = ScalaSet()""")
  nf.println
  nf.println(s"""  private def IdentifierName(): ScalaSet[Lexical] = ScalaSet(Lexical("IdentifierName", MinSampler.IdentifierName()))""")
  nf.println(s"""  private def NullLiteral(): ScalaSet[Lexical] = ScalaSet(Lexical("NullLiteral", MinSampler.NullLiteral()))""")
  nf.println(s"""  private def BooleanLiteral(): ScalaSet[Lexical] = ScalaSet(Lexical("BooleanLiteral", MinSampler.BooleanLiteral()))""")
  nf.println(s"""  private def NumericLiteral(): ScalaSet[Lexical] = ScalaSet(Lexical("NumericLiteral", MinSampler.NumericLiteral()))""")
  nf.println(s"""  private def StringLiteral(): ScalaSet[Lexical] = ScalaSet(Lexical("StringLiteral", MinSampler.StringLiteral()))""")
  nf.println(s"""  private def NoSubstitutionTemplate(): ScalaSet[Lexical] = ScalaSet(Lexical("NoSubstitutionTemplate", MinSampler.NoSubstitutionTemplate()))""")
  nf.println(s"""  private def TemplateHead(): ScalaSet[Lexical] = ScalaSet(Lexical("TemplateHead", MinSampler.TemplateHead()))""")
  nf.println(s"""  private def TemplateMiddle(): ScalaSet[Lexical] = ScalaSet(Lexical("TemplateMiddle", MinSampler.TemplateMiddle()))""")
  nf.println(s"""  private def TemplateTail(): ScalaSet[Lexical] = ScalaSet(Lexical("TemplateTail", MinSampler.TemplateTail()))""")
  targetProds.foreach(getSampler)
  nf.println(s"""}""")
  nf.close()
}

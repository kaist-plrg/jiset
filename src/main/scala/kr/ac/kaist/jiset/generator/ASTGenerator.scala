package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.spec.grammar.token._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._
import scala.util.matching.Regex

case class ASTGenerator(grammar: Grammar) {
  val Grammar(lexProds, prods) = grammar
  val lexNames = lexProds.map(_.lhs.name).toSet

  // generate Scala files for productions
  for (Production(lhs, rhsList) <- prods if !Grammar.isExtNT(lhs.name)) {
    val name = lhs.name
    val nf = getPrintWriter(s"$SRC_DIR/js/ast/$name.scala")
    genTrait(nf, name, rhsList)
    genObj(nf, name, rhsList)
    for ((rhs, i) <- rhsList.zipWithIndex) genClass(nf, name, rhs, i)
    nf.close()
  }

  // generate trait for Lhs
  private def genTrait(nf: PrintWriter, name: String, rhsList: List[Rhs]): Unit = {
    nf.println(s"""package $PACKAGE_NAME.js.ast""")
    nf.println
    nf.println(s"""import $PACKAGE_NAME.ir._""")
    nf.println(s"""import $PACKAGE_NAME.util.Span""")
    nf.println(s"""import $PACKAGE_NAME.util.Useful._""")
    nf.println(s"""import io.circe._, io.circe.syntax._""")
    nf.println
    nf.println(s"""trait $name extends AST { val kind: String = "$name" }""")
  }

  // generate object for Lhs
  private def genObj(nf: PrintWriter, name: String, rhsList: List[Rhs]): Unit = {
    def genCase(rhs: Rhs, idx: Int): Unit = {
      val NTs = for {
        (nt, i) <- rhs.getNTs.zipWithIndex
        ntName = if (lexNames contains nt.name) "Lexical" else nt.name
      } yield (s"x$i", i, ntName, if (nt.optional) "" else ".get")
      val NTArgsStr = NTs match {
        case Nil => ""
        case _ => NTs.map(_._1).mkString(", ") + ", "
      }

      nf.println(s"""      case $idx =>""")
      NTs.foreach {
        case (varName, i, ntName, opt) =>
          nf.println(s"""        val $varName = subs($i).map($ntName(_))$opt""")
      }
      nf.println(s"""        $name$idx(${NTArgsStr}params, span)""")
    }
    nf.println
    nf.println(s"""object $name {""")
    nf.println(s"""  def apply(data: Json): $name = AST(data) match {""")
    nf.println(s"""    case Some(compressed) => $name(compressed)""")
    nf.println(s"""    case None => error("invalid AST data: $$data")""")
    nf.println(s"""  }""")
    nf.println(s"""  def apply(data: AST.Compressed): $name = {""")
    nf.println(s"""    val AST.NormalCompressed(idx, subs, params, span) = data""")
    nf.println(s"""    idx match {""")
    for ((rhs, i) <- rhsList.zipWithIndex) genCase(rhs, i)
    nf.println(s"""    }""")
    nf.println(s"""  }""")
    nf.println(s"""}""")
  }

  // generate case classes for Rhs
  private def genClass(nf: PrintWriter, name: String, rhs: Rhs, i: Int): Unit = {
    val paramTypes = getParamTypes(rhs)
    val params = for (
      (t, i) <- paramTypes.zipWithIndex if t != ""
    ) yield (s"x$i", t)
    val string = getString(rhs)
    val paramPairs = params.map(_._1) zip (handleParams(params.map(_._2)))
    val listString = paramPairs.foldLeft("Nil") {
      case (str, (x, t)) => s"""l("$t", $x, $str)"""
    }
    val maxK = params.foldLeft(0) {
      case (k, (_, t)) => if (t.startsWith("Option[")) k * 2 + 1 else k
    }
    val paramsString = (params ++ List(
      ("parserParams", "List[Boolean]"),
      ("span", "Span"),
    )).map { case (x, t) => s"$x: $t" }.mkString(", ")

    nf.println
    nf.println(s"""case class $name$i($paramsString) extends $name {""")
    params.foreach {
      case (x, t) if (t.startsWith("Option[")) =>
        nf.println(s"  $x.foreach((m) => m.parent = Some(this))")
      case (x, t) =>
        nf.println(s"  $x.parent = Some(this)")
    }
    nf.println(s"""  def idx: Int = $i""")
    nf.println(s"""  def k: Int = ${params.foldLeft("0") { case (str, (x, _)) => s"d($x, $str)" }}""")
    nf.println(s"""  def fullList: List[(String, Value)] = $listString.reverse""")
    nf.println(s"""  def maxK: Int = $maxK""")
    nf.println(s"""  override def toString: String = {""")
    nf.println(s"""    s"$string"""")
    nf.println(s"""  }""")
    nf.println(s"""}""")
  }

  private def handleParams(l: List[String]): List[String] = {
    def aux(scnt: Map[String, Int], lprev: List[String], lnext: List[String]): List[String] = lnext match {
      case Nil => lprev
      case s :: rest => {
        scnt.lift(s) match {
          case Some(n) => aux(scnt + (s -> (n + 1)), s"$s$n" :: lprev, rest)
          case None => if (rest contains s) {
            aux(scnt + (s -> 1), (s + "0") :: lprev, rest)
          } else {
            aux(scnt, s :: lprev, rest)
          }
        }
      }
    }
    aux(Map(), Nil, l).reverse
  }

  private def getString(rhs: Rhs): String = (for {
    (token, i) <- rhs.tokens.zipWithIndex
    strOpt = token match {
      case Terminal(term) => Some(term)
      case NonTerminal(_, _, true) => Some(s"""$${x$i.getOrElse("")}""")
      case NonTerminal(_, _, false) | ButNot(_, _) => Some(s"""$$x$i""")
      case _ => None
    }
    if strOpt.isDefined
  } yield strOpt.get).mkString(" ")

  private def getParamTypes(rhs: Rhs): List[String] = for {
    (token, i) <- rhs.tokens.zipWithIndex
    paramType = getType(token)
  } yield if (lexNames contains paramType) "Lexical" else paramType

  private def getType(token: Token): String = token match {
    case NonTerminal(name, _, optional) => if (optional) s"Option[$name]" else name
    case ButNot(base, cases) => getType(base)
    case _ => ""
  }
}

package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Useful._
import scala.util.matching.Regex

case class ASTGenerator(algos: List[Algo], grammar: Grammar, modelDir: String) {
  val Grammar(lexProds, prods) = grammar
  val lexNames = lexProds.map(_.lhs.name).toSet

  // generate Scala files for productions
  for (Production(lhs, rhsList) <- prods if !Grammar.isExtNT(lhs.name)) {
    val name = lhs.name
    val nf = getPrintWriter(s"$modelDir/ast/$name.scala")
    genTrait(nf, name, rhsList)
    for ((rhs, i) <- rhsList.zipWithIndex) genClass(nf, name, rhs, i)
    nf.close()
  }

  // generate trait for Lhs
  private def genTrait(nf: PrintWriter, name: String, rhsList: List[Rhs]): Unit = {
    nf.println(s"""package $IRES_PACKAGE.model""")
    nf.println
    nf.println(s"""import $IRES_PACKAGE.ir._""")
    nf.println(s"""import $IRES_PACKAGE.error.InvalidAST""")
    nf.println(s"""import $IRES_PACKAGE.util.Span""")
    nf.println(s"""import scala.collection.immutable.{ Set => SSet }""")
    nf.println(s"""import spray.json._""")
    nf.println
    nf.println(s"""trait $name extends AST {""")
    nf.println(s"""  val kind: String = "$name"""")
    nf.println(s"""}""")
    nf.println(s"""object $name extends ASTHelper {""")
    nf.println(s"""  def apply(v: JsValue): $name = v match {""")
    for ((rhs, i) <- rhsList.zipWithIndex) genFromJsonCase(nf, name, rhs, i)
    nf.println(s"""    case _ => throw InvalidAST""")
    nf.println(s"""  }""")
    nf.println(s"""}""")
  }

  // generate fromJson cases
  private def genFromJsonCase(nf: PrintWriter, name: String, rhs: Rhs, i: Int): Unit = {
    val (xs, params) = (for {
      (token, i) <- rhs.tokens.zipWithIndex
      x = s"x$i"
      constructor <- getConstructor(token, x)
    } yield (x, constructor)).unzip
    val xsStr = xs.mkString(", ")
    val args = (params ++ List("params", "span")).mkString(", ")
    nf.println(s"""    case JsSeq(JsInt($i), JsSeq($xsStr), JsBoolSeq(params), JsSpan(span)) =>""")
    nf.println(s"""      $name$i($args)""")
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
    val sems = getSems(name, i)

    nf.println
    nf.println(s"""case class $name$i($paramsString) extends $name {""")
    params.foreach {
      case (x, t) if (t.startsWith("Option[")) =>
        nf.println(s"  $x.foreach((m) => m.parent = Some(this))")
      case (x, t) =>
        nf.println(s"  $x.parent = Some(this)")
    }
    nf.println(s"""  val idx: Int = $i""")
    nf.println(s"""  override def toString: String = {""")
    nf.println(s"""    s"$string"""")
    nf.println(s"""  }""")
    nf.println(s"""  val k: Int = ${params.foldLeft("0") { case (str, (x, _)) => s"d($x, $str)" }}""")
    nf.println(s"""  val fullList: List[(String, Value)] = $listString.reverse""")
    nf.println(s"""  val info: ASTInfo = $name$i""")
    nf.println(s"""}""")
    nf.println(s"""object $name$i extends ASTInfo {""")
    nf.println(s"""  val maxK: Int = $maxK""")
    nf.print(s"""  val semMap: Map[String, Algo] = """)
    if (sems.isEmpty) nf.println("Map()") else {
      nf.println("Map(")
      sems.foreach {
        case (methodName, algoName) =>
          nf.println(s"""    "$methodName" -> `$algoName`,""")
      }
      nf.println(s"""  )""")
    }
    nf.println(s"""}""")
  }

  private def getSems(name: String, i: Int): List[(String, String)] = {
    val pattern = s"${name}\\[$i,(\\d+)\\]\\.(.*)".r
    for {
      algo <- algos
      algoName = algo.name
      methodName <- algoName match {
        case pattern(j, methodName) => Some(methodName + j)
        case _ => None
      }
    } yield (methodName, "AL::" + algoName)
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

  private def getConstructor(token: Token, x: String): Option[String] = optional(token match {
    case NonTerminal(name, _, _) if lexNames contains name => s"""lex("$name", $x)"""
    case NonTerminal(name, _, true) => s"opt($x, $name.apply)"
    case NonTerminal(name, _, false) => s"$name($x)"
    case ButNot(NonTerminal(name, _, _), List(NonTerminal(notName, _, _))) =>
      s"""lex("($name \\\\ ($notName))", $x)"""
  })

  private def getType(token: Token): String = token match {
    case NonTerminal(name, _, optional) => if (optional) s"Option[$name]" else name
    case ButNot(base, cases) => getType(base)
    case _ => ""
  }
}

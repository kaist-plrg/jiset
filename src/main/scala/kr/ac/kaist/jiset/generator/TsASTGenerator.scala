package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.spec.grammar.token._
import kr.ac.kaist.jiset.util.Useful._
import scala.util.matching.Regex

case class TsASTGenerator(grammar: Grammar) {
  val srcPath = s"$TSBASE_DIR/src"
  val Grammar(lexProds, prods) = grammar
  val lexNames = lexProds.map(_.lhs.name).toSet
  val lhsNames = "Lexical" :: (for {
    Production(lhs, _) <- prods if !Grammar.isExtNT(lhs.name)
    name = lhs.name
  } yield name)
  val etcParams = List(
    ("parserParams", "boolean[]"),
    ("span", "Span"),
  )
  val etcParamsStr = etcParams.map { case (x, t) => s"$x: $t" }.mkString(", ")
  val etcParamsOfStr = etcParams.map { case (x, _) => s"$x" }.mkString(", ")

  // generate TypeScript files for productions
  val nfIndex = getPrintWriter(s"$srcPath/js/ast/index.ts")
  nfIndex.println(s"""export const enum ASTType {""")
  lhsNames.foreach(name => nfIndex.println(s"""  $name = "ASTType/$name","""))
  nfIndex.println(s"""}""")
  nfIndex.println(s"""import { Lexical } from "@js/lexical";""")
  nfIndex.println(s"""export { Lexical };""")
  for (Production(lhs, rhsList) <- prods if !Grammar.isExtNT(lhs.name)) {
    val name = lhs.name
    val nf = getPrintWriter(s"$srcPath/js/ast/$name.ts")
    genLhs(nf, name, rhsList)
    for ((rhs, i) <- rhsList.zipWithIndex) genRhs(nf, name, rhs, i)
    val importList = name :: s"${name}Type" :: List.range(0, rhsList.size).map((i) => s"$name$i")
    val importStr = s"{ ${importList.mkString(", ")} }"
    nfIndex.println(s"""import $importStr from "@js/ast/$name";""")
    nfIndex.println(s"""export $importStr;""")
    nf.close()
  }
  // generate AST type
  nfIndex.println(s"""export type AST = ${lhsNames.mkString(" | ")}""")
  nfIndex.close()

  // generate type & abstract class for Lhs
  private def genLhs(nf: PrintWriter, name: String, rhsList: List[Rhs]): Unit = {
    val rhsNames = List.range(0, rhsList.size).map((i) => s"$name$i")
    val typeStr = rhsNames.mkString(" | ")
    val typeName = name + "Type"
    val rhsNTs: Set[String] = (for {
      rhs <- rhsList
      nt <- rhs.getNTs
      ntName = nt match {
        case NonTerminal(ntName, _, _) => ntName
      }
      if ntName != name
    } yield ntName).toSet

    if (rhsNTs.exists(lexNames.contains(_)))
      nf.println(s"""import { Lexical } from "@js/lexical";""")
    nf.println(s"""import { _AST, Span } from "@js/AST";""")
    nf.println(s"""import { ASTType, ${rhsNTs.filter(!lexNames.contains(_)).mkString(", ")} } from "@js/ast/index";""")
    nf.println(s"""import { Option, Some, None } from "@util/option";""")
    nf.println(s"""import { Value } from "@ir/semantics";""")
    nf.println
    nf.println(s"""export type $name = $typeStr""")
    nf.println
    nf.println(s"""export const enum $typeName {""")
    rhsNames.foreach(name => nf.println(s"""  $name = "$typeName/$name","""))
    nf.println(s"""}""")
    nf.println
    nf.println(s"""abstract class _$name extends _AST {""")
    nf.println(s"""  readonly _astType = ASTType.$name""")
    nf.println(s"""  abstract readonly _$typeName: $typeName""")
    nf.println(s"""  constructor($etcParamsStr) {""")
    nf.println(s"""    super($etcParamsOfStr);""")
    nf.println(s"""  }""")
    nf.println(s"""}""")
  }

  // generate case classes for Rhs
  private def genRhs(nf: PrintWriter, name: String, rhs: Rhs, i: Int): Unit = {
    val typeName = name + "Type"
    val paramTypes = getParamTypes(rhs)
    val NTParams = for (
      (t, i) <- paramTypes.zipWithIndex if t != ""
    ) yield (s"x$i", t)
    val string = getString(rhs)
    val paramPairs = NTParams.map(_._1) zip (handleParams(NTParams.map(_._2)))
    val listString = paramPairs.foldLeft("[]") {
      case (str, (x, t)) => s"""this.l("$t", this.$x, $str)"""
    }
    val maxK = NTParams.foldLeft(0) {
      case (k, (_, t)) => if (t.startsWith("Option<")) k * 2 + 1 else k
    }

    val params = NTParams ++ etcParams
    val paramsStr = params.map { case (x, t) => s"$x: $t" }.mkString(", ")
    val paramsOfStr = params.map { case (x, _) => s"$x" }.mkString(", ")

    nf.println
    nf.println(s"""export class $name$i extends _$name {""")
    // properties
    nf.println(s"""  readonly _$typeName = $typeName.$name$i""")
    NTParams.foreach { case (p, t) => nf.println(s"""  readonly $p: $t;""") }
    // constructor
    nf.println(s"""  constructor($paramsStr) {""")
    nf.println(s"""    super($etcParamsOfStr);""")
    NTParams.foreach { case (p, _) => nf.println(s"""    this.$p = $p;""") }
    // set parent
    NTParams.foreach {
      case (x, t) if (t.startsWith("Option<")) =>
        nf.println(s"    $x.foreach(m => { m.parent = Some.of(this); });")
      case (x, t) =>
        nf.println(s"    $x.parent = Some.of(this);")
    }
    nf.println(s"""  }""")
    // of
    nf.println(s"""  static of($paramsStr): $name$i {""")
    nf.println(s"""    return new $name$i($paramsOfStr);""")
    nf.println(s"""  }""")
    // idx
    nf.println(s"""  override idx(): number { return $i; }""")
    // k, maxK
    nf.println(s"""  override k(): number {""")
    nf.println(s"""    return ${NTParams.foldLeft("0") { case (str, (x, _)) => s"this.d(this.$x, $str)" }};""")
    nf.println(s"""  }""")
    nf.println(s"""  override maxK(): number { return $maxK; }""")
    // fullList
    nf.println(s"""  override fullList(): [string, Value][] {""")
    nf.println(s"""    return $listString;""")
    nf.println(s"""  }""")
    // toString
    nf.println(s"""  override toString(): string {""")
    nf.println(s"""    return `$string`;""")
    nf.println(s"""  }""")
    nf.println(s"""}""")

  }

  private def handleParams(l: List[String]): List[String] = {
    def aux(scnt: Map[String, Int], lprev: List[String], lnext: List[String]): List[String] = lnext match {
      case Nil => lprev
      case s :: rest => {
        scnt.lift(s) match {
          case Some(n) => aux(scnt + (s -> (n + 1)), s"this.$s$n" :: lprev, rest)
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
      case NonTerminal(_, _, true) =>
        Some(s"""$${this.x$i.map(_ => _.toString()).getOrElse("")}""")
      case NonTerminal(_, _, false) | ButNot(_, _) => Some(s"""$${this.x$i}""")
      case _ => None
    }
    if strOpt.isDefined
  } yield strOpt.get).mkString(" ")

  private def getParamTypes(rhs: Rhs): List[String] = for {
    (token, i) <- rhs.tokens.zipWithIndex
    paramType = getType(token)
  } yield if (lexNames contains paramType) "Lexical" else paramType

  private def getType(token: Token): String = token match {
    case NonTerminal(name, _, optional) => if (optional) s"Option<$name>" else name
    case ButNot(base, cases) => getType(base)
    case _ => ""
  }
}

package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._

object ASTGenerator {
  def apply(packageName: String, modelDir: String, grammar: Grammar): Unit = {
    val Grammar(lexProds, prods) = grammar
    val lexNames = lexProds.map(_.lhs.name).toSet

    def getAST(prod: Production): Unit = {
      val Production(lhs, rhsList) = prod
      val name = lhs.name
      val nf = getPrintWriter(s"$modelDir/ast/$name.scala")
      nf.println(s"""package $packageName.model""")
      nf.println
      nf.println(s"""import $packageName.{ AST, ASTInfo, Lexical }""")
      nf.println(s"""import $packageName.ir._""")
      nf.println(s"""import $packageName.error.UnexpectedSemantics""")
      nf.println(s"""import scala.collection.immutable.{ Set => SSet }""")
      nf.println
      nf.println(s"""trait $name extends AST {""")
      nf.println(s"""  val kind: String = "$name"""")
      nf.println(s"""}""")
      rhsList.zipWithIndex.foreach {
        case (rhs, i) => {
          val paramTypes = getParamTypes(rhs)
          val params = for (
            (t, i) <- paramTypes.zipWithIndex if t != ""
          ) yield ("x" + i.toString, t)
          val string = getString(rhs)
          val isNTs = rhs.tokens.forall {
            case NonTerminal(_, _, _) => true
            case _ => false
          }

          def handleParams(l: List[String]): List[String] = {
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
          val paramPairs = params.map(_._1) zip (handleParams(params.map(_._2)))
          val listString = paramPairs.foldLeft("Nil") { case (str, (x, t)) => s"""l("$t", $x, $str)""" }

          val maxK = params.foldLeft(0) {
            case (k, (_, t)) => if (t.startsWith("Option[")) k * 2 + 1 else k
          }
          nf.println(s"""case class $name$i(${(params.map { case (x, t) => s"$x: $t, " }).mkString("")}parserParams: List[Boolean]) extends $name {""")
          nf.println(s"""  val name: String = "$name$i"""")
          nf.println(s"""  override def toString: String = {""")
          nf.println(s"""    s"$string"""")
          nf.println(s"""  }""")
          nf.println(s"""  val k: Int = ${params.foldLeft("0") { case (str, (x, _)) => s"d($x, $str)" }}""")
          nf.println(s"""  val fullList: List[(String, Value)] = $listString.reverse""")
          nf.println(s"""  val info: ASTInfo = $name$i""")
          nf.println(s"""}""")
          nf.println(s"""object $name$i extends ASTInfo {""")
          nf.println(s"""  val maxK: Int = $maxK""")
          nf.println(s"""  val semMap: Map[String, Func] = Map(""")
          var sems: List[String] = Nil
          for (file <- walkTree(s"$RESOURCE_DIR/$VERSION/manual/algorithm")) {
            if (scalaFilter(file.getName)) {
              val methodName = removedExt(file.getName)
              val pre = s"$name$i"
              val len = pre.length
              if (methodName.startsWith(pre) && !methodName.charAt(len).isDigit) {
                val semName = methodName.substring(len)
                sems ::= s""""$semName" -> $methodName.func"""
              }
            }
          }
          for (file <- walkTree(s"$RESOURCE_DIR/$VERSION/auto/algorithm")) {
            if (jsonFilter(file.getName)) {
              val methodName = removedExt(file.getName)
              val pre = s"$name$i"
              val len = pre.length
              if (methodName.startsWith(pre) && !methodName.charAt(len).isDigit) {
                val semName = methodName.substring(len)
                sems ::= s""""$semName" -> $methodName.func"""
              }
            }
          }
          nf.println(sems.mkString("," + LINE_SEP))
          nf.println(s"""  )""")
          nf.println(s"""}""")
        }
      }
      nf.close()
    }

    def getParamTypes(rhs: Rhs): List[String] = for {
      (token, i) <- rhs.tokens.zipWithIndex
      paramType = getType(token)
    } yield if (lexNames contains paramType) "Lexical" else paramType

    def getString(rhs: Rhs): String = (for {
      (token, i) <- rhs.tokens.zipWithIndex
      strOpt = token match {
        case Terminal(term) => Some(term)
        case NonTerminal(_, _, true) => Some(s"""$${x$i.getOrElse("")}""")
        case NonTerminal(_, _, false) | ButNot(_, _) => Some(s"""$$x$i""")
        case _ => None
      }
      if strOpt.isDefined
    } yield strOpt.get).mkString(" ")

    def getType(token: Token): String = token match {
      case NonTerminal(name, _, optional) => if (optional) s"Option[$name]" else name
      case ButNot(base, cases) => getType(base)
      case _ => ""
    }

    // Generates AST files
    prods.foreach(getAST)
  }
}

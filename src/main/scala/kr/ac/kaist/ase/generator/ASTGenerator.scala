package kr.ac.kaist.ase.generator

import java.io.PrintWriter
import kr.ac.kaist.ase._
import kr.ac.kaist.ase.spec._
import kr.ac.kaist.ase.util.Useful._

object ASTGenerator {
  def apply(grammar: Grammar): Unit = {
    val Grammar(lexProds, prods) = grammar
    val lexNames = lexProds.map(_.lhs.name).toSet

    def getAST(prod: Production): Unit = {
      val Production(lhs, rhsList) = prod
      val name = lhs.name
      val nf = getPrintWriter(s"$MODEL_DIR/ast/$name.scala")
      nf.println(s"""package kr.ac.kaist.ase.model""")
      nf.println
      nf.println(s"""import kr.ac.kaist.ase.core._""")
      nf.println(s"""import kr.ac.kaist.ase.error.UnexpectedSemantics""")
      nf.println(s"""import scala.collection.immutable.{ Set => SSet }""")
      nf.println
      nf.println(s"""trait $name extends AST""")
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
          val semantics = rhs.semantics

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
          val listString = ("Nil" /: paramPairs) { case (str, (x, t)) => s"""l("$t", $x, $str)""" }

          nf.println(s"""case class $name$i(${(params.map { case (x, t) => s"$x: $t, " }).mkString("")}parserParams: List[Boolean]) extends $name {""")
          nf.println(s"""  def name: String = "$name$i"""")
          nf.println(s"""  override def toString: String = {""")
          nf.println(s"""    s"$string"""")
          nf.println(s"""  }""")
          nf.println(s"""  def getNames: SSet[String] = (list match {""")
          nf.println(s"""    case List((_, ASTVal(ast))) => ast.getNames""")
          nf.println(s"""    case _ => SSet()""")
          nf.println(s"""  }) ++ SSet("$name")""")
          nf.println(s"""  val k: Int = ${("0" /: params) { case (str, (x, _)) => s"d($x, $str)" }}""")
          nf.println(s"""  val list: List[(String, Value)] = $listString.reverse""")
          nf.println(s"""  def semantics(name: String): Option[(Func, List[Value])] = {""")
          nf.println(s"""    $name$i.semMap.get(name + k.toString) match {""")
          nf.println(s"""      case Some(f) => Some((f, list.map(_._2)))""")
          nf.println(s"""      case None => list match {""")
          nf.println(s"""        case List((_, ASTVal(x))) => x.semantics(name)""")
          nf.println(s"""        case _ => None""")
          nf.println(s"""      }""")
          nf.println(s"""    }""")
          nf.println(s"""  }""")
          nf.println(s"""  def subs(name: String): Option[Value] = list.toMap.get(name)""")
          nf.println(s"""}""")
          nf.println(s"""object $name$i {""")
          nf.println(s"""  val semMap: Map[String, Func] = Map(""")
          for (file <- walkTree(s"$RESOURCE_DIR/$VERSION/manual/algorithm")) {
            if (scalaFilter(file.getName)) {
              val methodName = removedExt(file.getName)
              if (methodName startsWith s"$name$i") {
                val semName = methodName.substring(name.length + 1)
                nf.println(s""""$semName" -> $methodName.func""")
              }
            }
          }
          nf.println(rhs.semantics.map(s => s""""$s" -> $name$i$s.func""").mkString("," + LINE_SEP))
          nf.println(s"""  )""")
          nf.println(s"""}""")
        }
      }
      nf.close()
    }

    def getParamTypes(rhs: Rhs): List[String] = for {
      (token, i) <- rhs.tokens.zipWithIndex
      paramType = getType(token)
    } yield if (lexNames contains paramType) "String" else paramType

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

    val nf = getPrintWriter(s"$MODEL_DIR/ast/AST.scala")
    nf.println(s"""package kr.ac.kaist.ase.model""")
    nf.println
    nf.println(s"""import kr.ac.kaist.ase.core._""")
    nf.println(s"""import scala.collection.immutable.{ Set => SSet }""")
    nf.println
    nf.println(s"""trait AST {""")
    nf.println(s"""  def name: String""")
    nf.println(s"""  def semantics(name: String): Option[(Func, List[Value])]""")
    nf.println(s"""  def getNames: SSet[String]""")
    nf.println(s"""  protected def d(x: Any, n: Int): Int = x match {""")
    nf.println(s"""    case Some(_) => 2 * n + 1""")
    nf.println(s"""    case None => 2 * n""")
    nf.println(s"""    case _ => 2 * n""")
    nf.println(s"""  }""")
    nf.println(s"""  protected def l(name: String, x: Any, list: List[(String, Value)]): List[(String, Value)] = x match {""")
    nf.println(s"""    case Some(a: AST) => (name.substring(7, name.length - 1), ASTVal(a)) :: list""")
    nf.println(s"""    case a: AST => (name, ASTVal(a)) :: list""")
    nf.println(s"""    case a: String => (name, Str(a)) :: list""")
    nf.println(s"""    case _ => list""")
    nf.println(s"""  }""")
    nf.println(s"""  val parserParams: List[Boolean]""")
    nf.println(s"""  def subs(name: String): Option[Value]""")
    nf.println(s"""}""")
    nf.println
    prods.foreach(getAST)
    nf.close()
  }
}

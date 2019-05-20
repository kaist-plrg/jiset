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
      nf.println
      nf.println(s"""trait $name extends AST""")
      rhsList.zipWithIndex.foreach {
        case (rhs, i) => {
          val paramTypes = getParamTypes(rhs)
          val params = for (
            (t, i) <- paramTypes.zipWithIndex if t != ""
          ) yield ("x" + i.toString, t)
          val string = getString(rhs)
          val semantics = rhs.semantics
          params match {
            case (x0, t0) :: _ =>
              nf.println(s"""case class $name$i(${(params.map { case (x, t) => s"$x: $t" }).mkString(", ")}) extends $name {""")
              nf.println(s"""  override def toString: String = {""")
              nf.println(s"""    s"$string"""")
              nf.println(s"""  }""")
              nf.println(s"""  val k: Int = ${("0" /: params) { case (str, (x, _)) => s"d($x, $str)" }}""")
              nf.println(s"""  val list: List[Value] = ${("Nil" /: params) { case (str, (x, _)) => s"l($x, $str)" }}""")
              nf.println(s"""  def semantics(name: String): (Func, List[Value]) = {""")
              nf.println(s"""    $name$i.semMap.get(name + k.toString) match {""")
              nf.println(s"""      case Some(f) => (f, list)""")
              nf.println(s"""      case None => """ + (t0 match {
                case "String" => "???"
                case _ if t0 startsWith "Option[" => s"$x0.get.semantics(name)"
                case _ => s"$x0.semantics(name)"
              }))
              nf.println(s"""    }""")
              nf.println(s"""  }""")
              nf.println(s"""}""")
              nf.println(s"""object $name$i {""")
            case Nil =>
              nf.println(s"""case object $name$i extends $name {""")
              nf.println(s"""  override def toString: String = {""")
              nf.println(s"""    s"$string"""")
              nf.println(s"""  }""")
              nf.println(s"""  def semantics(name: String): (Func, List[Value]) = semMap.get(name + "0") match {""")
              nf.println(s"""    case Some(f) => (f, Nil)""")
              nf.println(s"""    case None => ???""")
              nf.println(s"""  }""")
          }
          nf.println(s"""  val semMap: Map[String, Func] = Map(""")
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

    val nf = getPrintWriter(s"$MODEL_DIR/AST.scala")
    nf.println(s"""package kr.ac.kaist.ase.model""")
    nf.println
    nf.println(s"""import kr.ac.kaist.ase.core._""")
    nf.println
    nf.println(s"""trait AST {""")
    nf.println(s"""  def semantics(name: String): (Func, List[Value])""")
    nf.println(s"""  protected def d(x: Any, n: Int): Int = x match {""")
    nf.println(s"""    case Some(_) => 2 * n + 1""")
    nf.println(s"""    case None => 2 * n""")
    nf.println(s"""    case _ => 2 * n""")
    nf.println(s"""  }""")
    nf.println(s"""  protected def l(x: Any, list: List[Value]): List[Value] = x match {""")
    nf.println(s"""    case Some(a: AST) => ASTVal(a) :: list""")
    nf.println(s"""    case a: AST => ASTVal(a) :: list""")
    nf.println(s"""    case a: String => Str(a) :: list""")
    nf.println(s"""    case _ => list""")
    nf.println(s"""  }""")
    nf.println(s"""}""")
    nf.println
    prods.foreach(getAST)
    nf.close()
  }
}

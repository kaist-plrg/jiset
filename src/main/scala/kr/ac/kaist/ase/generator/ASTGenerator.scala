package kr.ac.kaist.ase.generator

import java.io.PrintWriter
import kr.ac.kaist.ase._
import kr.ac.kaist.ase.spec._
import kr.ac.kaist.ase.util.Useful._

object ASTGenerator {
  def apply(grammar: Grammar): Unit = {
    val nf = getPrintWriter(s"$MODEL_DIR/AST.scala")
    val Grammar(lexProds, prods) = grammar
    val lexNames = lexProds.map(_.lhs.name).toSet

    def getAST(prod: Production): Unit = {
      val Production(lhs, rhsList) = prod
      val name = lhs.name
      nf.println(s"""trait $name extends AST""")
      rhsList.zipWithIndex.foreach {
        case (rhs, i) => {
          val params = getParams(rhs)
          val string = getString(rhs)
          val semantics = rhs.semantics
          if (params.length > 0) {
            nf.println(s"""case class $name$i(${params.mkString(", ")}) extends $name {""")
            nf.println(s"""  override def toString: String = {""")
            nf.println(s"""    s"$string"""")
            nf.println(s"""  }""")
            nf.println(s"""  def semantics: Map[String, (Func, List[Value])] = $name$i.semantics""")
            nf.println(s"""}""")
            nf.println(s"""object $name$i {""")
            nf.println(s"""  val semantics: Map[String, (Func, List[Value])] = Map() // TODO""")
            nf.println(s"""}""")
          } else {
            nf.println(s"""case object $name$i extends $name {""")
            nf.println(s"""  override def toString: String = {""")
            nf.println(s"""    s"$string"""")
            nf.println(s"""  }""")
            nf.println(s"""  val semantics: Map[String, (Func, List[Value])] = Map() // TODO""")
            nf.println(s"""}""")
          }
        }
      }
    }

    def getParams(rhs: Rhs): List[String] = for {
      (token, i) <- rhs.tokens.zipWithIndex
      paramType = getType(token)
      if paramType != ""
    } yield {
      s"x$i: " + (if (lexNames contains paramType) "String" else paramType)
    }

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

    nf.println(s"""package kr.ac.kaist.ase.model""")
    nf.println
    nf.println(s"""import kr.ac.kaist.ase.core._""")
    nf.println
    nf.println(s"""trait AST {""")
    nf.println(s"""  def semantics: Map[String, (Func, List[Value])]""")
    nf.println(s"""}""")
    nf.println
    prods.foreach(getAST)
    nf.close()
  }
}

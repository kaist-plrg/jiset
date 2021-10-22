package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.spec.grammar.token._
import kr.ac.kaist.jiset.util.JvmUseful._

object WalkerKind extends Enumeration {
  val ASTWalker, ASTTransformer = Value
}
abstract class WalkerGenerator {
  // walker kind
  type WalkerKind = WalkerKind.Value
  val kind: WalkerKind

  // class name, function name of walker
  private def choose(s0: String, s1: String) = kind match {
    case WalkerKind.ASTWalker => s0
    case WalkerKind.ASTTransformer => s1
  }
  def name: String = kind.toString
  def func: String = choose("walk", "transform")
  def param(p: String): String = choose("_", p)
  def ret(ty: String) = choose("Unit", ty)

  // grammar
  val grammar: Grammar
  val Grammar(lexProds, prods) = grammar
  val lexNames = lexProds.map(_.lhs.name).toSet

  // gen walker
  def dump(): Unit = {
    val nf = getPrintWriter(s"$SRC_DIR/js/$name.scala")
    generate(nf)
    nf.close()
  }

  private def generate(nf: PrintWriter): Unit = {
    val lexBody = choose("{}", "lex")

    nf.println(s"""package $PACKAGE_NAME.js""")
    nf.println
    nf.println(s"""import $PACKAGE_NAME.js.ast._""")
    nf.println
    nf.println(s"""trait $name {""")
    nf.println(s"""  def job(ast: AST): Unit = {}""")
    nf.println(s"""  def $func[T](opt: Option[T], w: T => ${ret("T")}): ${ret("Option[T]")} = opt.map(w)""")
    nf.println(s"""  def $func(lex: Lexical): ${ret("Lexical")} = $lexBody""")
    nf.println
    prods.foreach(genWalker(_, nf))
    nf.println
    nf.println(s"""  def $func(ast: AST): ${ret("AST")} = ast match {""")
    prods.foreach {
      case Production(Lhs(name, _), _) =>
        nf.println(s"""    case ast: $name => $func(ast)""")
    }
    nf.println(s"""  }""")
    nf.println(s"""}""")
  }

  private def genWalker(prod: Production, nf: PrintWriter): Unit = {
    val Production(lhs, rhsList) = prod
    val Lhs(name, rawParams) = lhs
    nf.println(s"""  def $func(ast: $name): ${ret(name)} = ast match {""")
    for ((rhs, k) <- rhsList.zipWithIndex) {
      val xs = for {
        (token, i) <- rhs.tokens.zipWithIndex
        (name, opt) <- getInfo(token)
      } yield (s"x$i", name, opt)
      val argsStr = (xs.map(_._1) ++ List(param("params"), param("span"))).mkString(", ")
      if (xs.isEmpty) {
        val bodyStr = choose("job(ast)", "{ job(ast); ast }")
        nf.println(s"""    case $name$k($argsStr) => $bodyStr""")
      } else {
        nf.println(s"""    case $name$k($argsStr) =>""")
        val walkStr = xs.map {
          case (x, name, true) => s"$func[$name]($x, $func)"
          case (x, _, false) => s"$func($x)"
        }.mkString(choose("; ", ", "))
        val bodyStr = choose(walkStr, s"$name$k($walkStr, params, span)")
        nf.println(s"""      job(ast); $bodyStr""")
      }
    }
    nf.println(s"""  }""")
  }

  private def getInfo(token: Token): Option[(String, Boolean)] = token match {
    case NonTerminal(name, _, optional) => Some((name, optional))
    case ButNot(base, _) => getInfo(base)
    case _ => None
  }
}

// AST walker generator
case class ASTWalkerGenerator(grammar: Grammar) extends WalkerGenerator {
  val kind = WalkerKind.ASTWalker
  dump()
}

// AST transformer generator
case class ASTTransformerGenerator(grammar: Grammar) extends WalkerGenerator {
  val kind = WalkerKind.ASTTransformer
  dump()
}

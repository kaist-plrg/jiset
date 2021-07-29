package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.spec.grammar.token._
import kr.ac.kaist.jiset.util.JvmUseful._

case class WalkerGenerator(grammar: Grammar) {
  val Grammar(lexProds, prods) = grammar
  val lexNames = lexProds.map(_.lhs.name).toSet

  val nf = getPrintWriter(s"$SRC_DIR/js/ASTWalker.scala")
  generate
  nf.close()

  private def generate: Unit = {
    nf.println(s"""package $PACKAGE_NAME.js""")
    nf.println
    nf.println(s"""import $PACKAGE_NAME.js.ast._""")
    nf.println
    nf.println(s"""trait ASTWalker {""")
    nf.println(s"""  def job(ast: AST): Unit = {}""")
    nf.println(s"""  def walk[T](opt: Option[T], w: T => Unit): Unit = opt.map(w)""")
    nf.println(s"""  def walk(lex: Lexical): Unit = {}""")
    nf.println
    prods.foreach(genWalker)
    nf.println(s"""}""")
  }

  private def genWalker(prod: Production): Unit = {
    val Production(lhs, rhsList) = prod
    val Lhs(name, rawParams) = lhs
    nf.println(s"""  def walk(ast: $name): Unit = ast match {""")
    for ((rhs, k) <- rhsList.zipWithIndex) {
      val xs = for {
        (token, i) <- rhs.tokens.zipWithIndex
        (name, opt) <- getInfo(token)
      } yield (s"x$i", name, opt)
      val argsStr = (xs.map(_._1) ++ List("_", "_")).mkString(", ")
      if (xs.isEmpty) {
        nf.println(s"""    case $name$k($argsStr) => job(ast)""")
      } else {
        nf.println(s"""    case $name$k($argsStr) =>""")
        val walkStr = xs.map {
          case (x, name, true) => s"walk[$name]($x, walk)"
          case (x, _, false) => s"walk($x)"
        }.mkString("; ")
        nf.println(s"""      job(ast); $walkStr""")
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

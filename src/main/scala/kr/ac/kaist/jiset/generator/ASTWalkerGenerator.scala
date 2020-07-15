package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._

object ASTWalkerGenerator {
  def apply(
    packageName: String,
    modelDir: String,
    grammar: Grammar,
    debug: Boolean = false
  ): Unit = {
    dumpWalker(packageName, modelDir, grammar, debug)
    dumpUnitWalker(packageName, modelDir, grammar, debug)
  }
  def dumpWalker(
    packageName: String,
    modelDir: String,
    grammar: Grammar,
    debug: Boolean = false
  ): Unit = {
    val nf = getPrintWriter(s"$modelDir/ast/Walker.scala")
    val Grammar(lexProds, prods) = grammar

    def getCase(prod: Production): Unit = {
      val name = prod.lhs.name
      nf.println(s"""    case ast: $name => walk(ast)""")
    }

    def getWalker(prod: Production): Unit = {
      val Production(lhs, rhsList) = prod
      val Lhs(name, _) = lhs
      nf.println(s"""  def walk(ast: $name): $name = ast match {""")
      rhsList.zipWithIndex.foreach({
        case (rhs, idx) =>
          val astName = s"$name$idx"
          val nts = rhs.tokens.flatMap(getNT)
          val ids = (0 until nts.length).map(k => s"x$k, ").mkString + "ps"
          val args = nts.zipWithIndex.map {
            case (NonTerminal(_, _, false), k) => s"walk(x$k), "
            case (NonTerminal(name, _, true), k) => s"walkOpt[$name](x$k, walk), "
          }.mkString + "walkList[Boolean](ps, walk)"
          nf.println(s"""    case $astName($ids) => $astName($args)""")
      })
      nf.println(s"""  }""")
    }

    def getNT(token: Token): Option[NonTerminal] = token match {
      case nt: NonTerminal => Some(nt)
      case ButNot(base, _) => getNT(base)
      case _ => None
    }

    nf.println(s"""package $packageName.model""")
    nf.println
    nf.println(s"""import $packageName.{ AST, Lexical }""")
    nf.println(s"""import $packageName.ir._""")
    nf.println
    nf.println(s"""trait Walker {""")
    nf.println(s"""  def walk(str: String): String = str""")
    nf.println(s"""  def walk(bool: Boolean): Boolean = bool""")
    nf.println(s"""  def walkOpt[T](opt: Option[T], tWalk: T => T): Option[T] = opt.map(tWalk)""")
    nf.println(s"""  def walkList[T](list: List[T], tWalk: T => T): List[T] = list.map(tWalk)""")
    nf.println(s"""  def walk(ast: AST): AST = ast match {""")
    nf.println(s"""    case ast: Lexical => walk(ast)""")
    prods.foreach(getCase)
    nf.println(s"""  }""")
    nf.println(s"""  def walk(ast: Lexical): Lexical = Lexical(walk(ast.kind), walk(ast.str))""")
    prods.foreach(getWalker)
    nf.println(s"""}""")
    nf.close()
  }
  def dumpUnitWalker(
    packageName: String,
    modelDir: String,
    grammar: Grammar,
    debug: Boolean = false
  ): Unit = {
    val nf = getPrintWriter(s"$modelDir/ast/UnitWalker.scala")
    val Grammar(lexProds, prods) = grammar

    def getCase(prod: Production): Unit = {
      val name = prod.lhs.name
      nf.println(s"""    case ast: $name => walk(ast)""")
    }

    def getWalker(prod: Production): Unit = {
      val Production(lhs, rhsList) = prod
      val Lhs(name, _) = lhs
      nf.println(s"""  def walk(ast: $name): Unit = id(ast, beforeWalk) match {""")
      rhsList.zipWithIndex.foreach({
        case (rhs, idx) =>
          val astName = s"$name$idx"
          val nts = rhs.tokens.flatMap(getNT)
          val ids = (0 until nts.length).map(k => s"x$k, ").mkString + "ps"
          val args = nts.zipWithIndex.map {
            case (NonTerminal(_, _, false), k) => s"walk(x$k); "
            case (NonTerminal(name, _, true), k) => s"walkOpt[$name](x$k, walk); "
          }.mkString + "walkList[Boolean](ps, walk)"
          nf.println(s"""    case $astName($ids) => $args""")
      })
      nf.println(s"""  }""")
    }

    def getNT(token: Token): Option[NonTerminal] = token match {
      case nt: NonTerminal => Some(nt)
      case ButNot(base, _) => getNT(base)
      case _ => None
    }

    nf.println(s"""package $packageName.model""")
    nf.println
    nf.println(s"""import $packageName.{ AST, Lexical }""")
    nf.println(s"""import $packageName.ir._""")
    nf.println
    nf.println(s"""trait UnitWalker {""")
    nf.println(s"""  def beforeWalk[T <: AST](ast: T): Unit = {}""")
    nf.println(s"""  private def id[T <: AST](ast: T, hook: T => Unit): T = { hook(ast); ast }""")
    nf.println(s"""  def walk(str: String): Unit = {}""")
    nf.println(s"""  def walk(bool: Boolean): Unit = {}""")
    nf.println(s"""  def walkOpt[T](opt: Option[T], tWalk: T => Unit): Unit = opt.foreach(tWalk)""")
    nf.println(s"""  def walkList[T](list: List[T], tWalk: T => Unit): Unit = list.foreach(tWalk)""")
    nf.println(s"""  def walk(ast: AST): Unit = id(ast, beforeWalk) match {""")
    nf.println(s"""    case ast: Lexical => walk(ast)""")
    prods.foreach(getCase)
    nf.println(s"""  }""")
    nf.println(s"""  def walk(ast: Lexical): Unit = { walk(ast.kind); walk(ast.str) }""")
    prods.foreach(getWalker)
    nf.println(s"""}""")
    nf.close()
  }
}

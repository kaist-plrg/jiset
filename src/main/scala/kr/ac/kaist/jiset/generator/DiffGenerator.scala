package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Useful._

case class DiffGenerator(grammar: Grammar, modelDir: String) {
  val Grammar(lexProds, prods) = grammar
  val lexNames = lexProds.map(_.lhs.name).toSet

  val nf = getPrintWriter(s"$modelDir/ASTDiff.scala")
  generate
  nf.close()

  private def generate: Unit = {
    nf.println(s"""package $IRES_PACKAGE.model""")
    nf.println
    nf.println(s"""import $IRES_PACKAGE.util.Useful.error""")
    nf.println(s"""import kr.ac.kaist.ires.util.Span""")
    nf.println
    nf.println(s"""object ASTDiff {""")
    nf.println(s"""  def diffError(l: Any, r: Any): Unit = error(s"$$l != $$r")""")
    nf.println(s"""  def diff[T](l: Option[T], r: Option[T], d: (T, T) => Unit): Unit = (l, r) match {""")
    nf.println(s"""    case (Some(l), Some(r)) => d(l, r)""")
    nf.println(s"""    case (None, None) =>""")
    nf.println(s"""    case _ => diffError(l, r)""")
    nf.println(s"""  }""")
    nf.println(s"""  def diff(l: Span, r: Span): Unit = if (l != r) diffError(l, r)""")
    nf.println(s"""  def diff(l: List[Boolean], r: List[Boolean]): Unit = if (l != r) diffError(l, r)""")
    nf.println(s"""  def diff(l: Lexical, r: Lexical): Unit = {""")
    nf.println(s"""    if (l.kind != r.kind) diffError(l.kind, r.kind)""")
    nf.println(s"""    if (l.str != r.str) diffError(l.str, r.str)""")
    nf.println(s"""  }""")
    nf.println
    prods.foreach(genDiff)
    nf.println(s"""}""")
  }

  private def genDiff(prod: Production): Unit = {
    val Production(lhs, rhsList) = prod
    val Lhs(name, rawParams) = lhs
    nf.println(s"""  def diff(l: $name, r: $name): Unit = (l, r) match {""")
    for ((rhs, k) <- rhsList.zipWithIndex) {
      val xs = for {
        (token, i) <- rhs.tokens.zipWithIndex
        (name, opt) <- getInfo(token)
      } yield (i, name, opt)
      val ls = (xs.map(t => "l" + t._1) ++ List("lp", "ls")).mkString(", ")
      val rs = (xs.map(t => "r" + t._1) ++ List("rp", "rs")).mkString(", ")
      nf.println(s"""    case ($name$k($ls), $name$k($rs)) =>""")
      val diffStr = (xs.map {
        case (i, name, true) => s"diff[$name](l$i, r$i, diff)"
        case (i, _, false) => s"diff(l$i, r$i)"
      } ++ List("diff(lp, rp)", "diff(ls, rs)")).mkString("; ")
      nf.println(s"""      $diffStr""")
    }
    nf.println(s"""    case _ => diffError(l, r)""")
    nf.println(s"""  }""")
  }

  private def getInfo(token: Token): Option[(String, Boolean)] = token match {
    case NonTerminal(name, _, optional) => Some((name, optional))
    case ButNot(base, _) => getInfo(base)
    case _ => None
  }
}

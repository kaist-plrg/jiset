package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.spec.grammar.token._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.spec.algorithm.Algo
import scala.collection.immutable

case class SyntacticViewSeedGenerator(grammar: Grammar, algos: List[Algo]) {

  def name: String = "SyntacticViewSeed"

  // grammar
  val Grammar(lexProds, prods) = grammar
  val lexNames = lexProds.map(_.lhs.name).toSet

  // gen walker
  def dump(): Unit = {
    val nf = getPrintWriter(s"$SRC_DIR/editor/$name.scala")
    generate(nf)
    nf.close()
  }
  dump()

  // name, k, AST representation
  private def initAAST(name: String, paramLen: Int, k: Int, rhs: Rhs): List[(String, Int, String)] = {
    val xs = for {
      (token, i) <- rhs.tokens.zipWithIndex
      (name, opt) <- getInfo(token)
    } yield (name, opt)
    val initList = List[List[String]](List())
    xs.foldLeft(initList) {
      case (sl, (name, opt)) => sl.flatMap(s => if (lexNames contains name) List() else if (opt) List(s :+ s"Some(Abs$name)", s :+ "None") else List(s :+ s"Abs$name"))
    }.filter(_.length > 1).map(s => s ++ List(s"List(${List.fill(paramLen)("false").mkString(", ")})", "Span()")).map(s => (name, k, s"$name$k(${s.mkString(", ")})"))
  }

  private def injectAAST(name: String, paramLen: Int, k: Int, rhs: Rhs, cname: String, rep: String) = {
    val xs = for {
      (token, i) <- rhs.tokens.zipWithIndex
      (name, opt) <- getInfo(token)
    } yield (name, opt)
    if (xs.length == 1 && xs(0)._1 == cname && xs(0)._2 == false) {
      List((name, k, s"$name$k($rep, List(${List.fill(paramLen)("false").mkString(", ")}), Span())"))
    } else {
      List()
    }
  }

  private def nextAAST(name: String, k: Int, rep: String): (Boolean, List[(String, Int, String)]) = {
    val pattern = s"${name}\\[${k},(\\d+)\\]\\.(.*)".r
    if (algos.exists((algo) => algo.name match { case pattern(_, "Evaluation") => true; case _ => false })) {
      (true, Nil)
    } else {
      (false, prods.flatMap {
        case Production(Lhs(pname, rawParams), rhsList) => rhsList.zipWithIndex.flatMap { case (rhs, k) => injectAAST(pname, rawParams.length, k, rhs, name, rep) }
      })
    }

  }

  def aux(l: List[(String, Int, String)], r: List[(String, Int, String)]): List[(String, Int, String)] = l match {
    case head :: next => nextAAST(head._1, head._2, head._3) match {
      case (true, xs) => aux(next ++ xs, r :+ head)
      case (false, xs) => aux(next ++ xs, r)
    }
    case immutable.Nil => r
  }

  private def generate(nf: PrintWriter): Unit = {
    nf.println(s"""package $PACKAGE_NAME.editor""")
    nf.println
    nf.println(s"""import $PACKAGE_NAME.js.ast._""")
    nf.println(s"""import kr.ac.kaist.jiset.util.Span""")
    nf.println
    nf.println(s"""object SyntacticViewSeed {""")
    nf.println
    val initSeed = prods.flatMap {
      case Production(Lhs(name, rawParams), rhsList) => rhsList.zipWithIndex.flatMap { case (rhs, k) => initAAST(name, rawParams.length, k, rhs) }
    }
    val fixpoint = aux(initSeed, List())
    nf.println(s"""  val syntacticViewSeed: List[SyntacticView] = List(""")
    fixpoint.foreach((s) => nf.println(s"""    ${s._3},"""))
    nf.println("""  ).map(SyntacticView)""")

    //prods.foreach(genWalker(_, nf))
    nf.println
    //nf.println(s"""  }""")
    nf.println(s"""}""")
  }
  /*
  // output이 되어야 되는 것 : List of AST인데, 1) evaluation 함수가 있음 2) recursive하게 child 1개짜리 AST거나 child 여러개짜리 AAST 조합이어야 함
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
  */
  private def getInfo(token: Token): Option[(String, Boolean)] = token match {
    case NonTerminal(name, _, optional) => Some((name, optional))
    case ButNot(base, _) => getInfo(base)
    case _ => None
  }
}

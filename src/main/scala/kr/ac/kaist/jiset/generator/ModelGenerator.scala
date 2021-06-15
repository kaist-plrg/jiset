package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar.Grammar

case class ModelGenerator(spec: ECMAScript, modelDir: String) {
  // make model directories
  mkdir(modelDir)
  mkdir(s"$modelDir/ast")
  mkdir(s"$modelDir/type")
  mkdir(s"$modelDir/algorithm")

  val ECMAScript(grammar, algos, consts, intrinsics, symbols, _, _) = spec

  val targetAlgos = algos.filter(_.head match {
    case (head: SyntaxDirectedHead) if Grammar.isExtNT(head.prod.name) => false
    case _ => true
  })

  // generate algorithm/*.scala
  AlgoGenerator(targetAlgos, modelDir)

  // generate ast/*.scala
  ASTGenerator(targetAlgos, grammar, modelDir)

  // generate Parser.scala
  ParserGenerator(grammar, modelDir)

  // generate ASTWalker.scala
  WalkerGenerator(grammar, modelDir)

  // generate ASTDiff.scala
  DiffGenerator(grammar, modelDir)

  // generate Model.scala
  genModel

  private def genModel: Unit = {
    val nf = getPrintWriter(s"$modelDir/Model.scala")
    nf.println(s"""package $IRES_PACKAGE.model""")
    nf.println
    nf.println(s"""import $IRES_PACKAGE.algorithm._""")
    nf.println(s"""import $IRES_PACKAGE.ModelTrait""")
    nf.println(s"""import $IRES_PACKAGE.ir._""")
    nf.println(s"""import $IRES_PACKAGE.util.Useful._""")
    nf.println
    nf.println(s"""object Model extends ModelTrait {""")
    nf.println(s"""  lazy val consts: List[String] = List(""")
    consts.foreach(const => nf.println(s"""    "$const","""))
    nf.println(s"""  )""")
    nf.println(s"""  lazy val intrinsics: List[String] = List(""")
    intrinsics.foreach(intrinsic => nf.println(s"""    "$intrinsic","""))
    nf.println(s"""  )""")
    nf.println(s"""  lazy val symbols: List[String] = List(""")
    symbols.foreach(symbol => nf.println(s"""    "$symbol","""))
    nf.println(s"""  )""")
    nf.println(s"""  lazy val algos: Map[String, Algo] = Map(""")
    for (algo <- targetAlgos) {
      val name = algo.name
      nf.println(s"""    "$name" -> `AL::$name`,""")
    }
    nf.println(s"""  )""")
    nf.println(s"""}""")
    nf.close()
  }
}

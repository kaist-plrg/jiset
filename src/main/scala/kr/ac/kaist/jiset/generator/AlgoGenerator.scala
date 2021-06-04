package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.ir.JsonProtocol._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec.algorithm.Algo
import java.io.File

case class AlgoGenerator(algos: List[Algo], modelDir: String) {
  algos.foreach(genAlgo)

  private def genAlgo(algo: Algo): Unit = {
    val Algo(head, ids, rawBody, code) = algo
    val name = algo.name

    val nf = getPrintWriter(s"$modelDir/algorithm/$name.scala")
    nf.println(s"""package $IRES_PACKAGE.model""")
    nf.println(s"""""")
    nf.println(s"""import $IRES_PACKAGE.ir._""")
    nf.println(s"""import $IRES_PACKAGE.ir.Parser._""")
    nf.println(s"""import Param.Kind._""")
    nf.println(s"""""")
    nf.println(s"""object `AL::$name` extends Algo {""")
    nf.println(s"""  val head = ${head.toScala}""")
    nf.print(s"""  val ids = """)
    if (ids.isEmpty) nf.println("List()") else {
      nf.println("List(")
      ids.foreach(id => nf.println(s"""    "$id","""))
      nf.println(s"""  )""")
    }
    val inst = rawBody.beautified(index = true)
      .split(LINE_SEP)
      .mkString(LINE_SEP + "  |")
    nf.println(s"""  val rawBody = parseInst($TRIPLE$inst$TRIPLE.stripMargin)""")
    nf.print(s"""  val code = """)
    if (code.isEmpty) nf.println("scala.Array[String]()") else {
      nf.println("scala.Array[String](")
      code.foreach(line => nf.println(s"""    $TRIPLE$line$TRIPLE,"""))
      nf.println(s"""  )""")
    }
    nf.println(s"""}""")
    nf.close()
  }
}

package kr.ac.kaist.jiset.phase

import kr.ac.kaist.ires.ir
import kr.ac.kaist.jiset.{ DIFFLIST, RESOURCE_DIR }
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.{ LINE_SEP, JISETConfig }
import scala.io.Source

// GrammarDiff phase
case object GrammarDiff extends PhaseObj[Unit, GrammarDiffConfig, Unit] {
  val name = "grammar-diff"
  val help = "Show differences among ECMAScript grammars of adjacent versions."

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: GrammarDiffConfig
  ): Unit = {
    val m = Map[String, Production]()
    DIFFLIST.foldLeft((m, m)) {
      case ((prevLexMap, prevSynMap), version) =>
        val spec = Spec(s"$RESOURCE_DIR/$version/auto/spec.json")
        val Grammar(lexProds, synProds) = spec.grammar

        val lexMap = lexProds.map(prod => prod.lhs.name -> prod).toMap
        val synMap = synProds.map(prod => prod.lhs.name -> prod).toMap

        val diffLexSize = getDiffSize(prevLexMap, lexMap)
        val diffSynSize = getDiffSize(prevSynMap, synMap)

        println(s"diff : $diffLexSize, $diffSynSize")
        println(s"$version : ${lexMap.size}, ${synMap.size}")

        (lexMap, synMap)
    }
  }

  def getDiffSize[A, B](old: Map[A, B], cur: Map[A, B]): Int = cur.foldLeft(0) {
    case (count, (k, v)) => old.get(k) match {
      case Some(oldV) => if (oldV == v) count else count + 1
      case None => count + 1
    }
  }

  def defaultConfig: GrammarDiffConfig = GrammarDiffConfig()
  val options: List[PhaseOption[GrammarDiffConfig]] = List()
}

// GrammarDiff phase config
case class GrammarDiffConfig() extends Config

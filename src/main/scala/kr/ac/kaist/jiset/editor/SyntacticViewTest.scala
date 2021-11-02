package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.ir.Inst
import kr.ac.kaist.jiset.ir.UnitWalker
import kr.ac.kaist.jiset.js.algoMap

object InstructionCountWalker extends UnitWalker {
  var count = 0

  override def walk(inst: Inst): Unit = {
    count += 1
    super.walk(inst)
  }
}

object SyntacticViewTest {

  def getInstNum(inst: Inst): Int = {
    InstructionCountWalker.count = 0
    InstructionCountWalker.walk(inst)
    InstructionCountWalker.count
  }

  def apply(): Unit = {
    val evaluated = SyntacticViewSeed.syntacticViewSeed.map(
      (view) => {
        val algos = PartialEval(view)
        val touchedNum = algos.length
        val origInstNum = algos.map((algo) => getInstNum(algoMap(algo.name).rawBody)).sum
        val newInstNum = algos.map((algo) => getInstNum(algo.rawBody)).sum
        (view, touchedNum, origInstNum, newInstNum)
      }
    )
    println("ast : touched Algo Num / original Inst Num / PE-ed Inst Num")
    evaluated.foreach {
      case (view, touchedNum, origInstNum, newInstNum) =>
        println(s"[${view.ast.kind}] : $touchedNum / $origInstNum / $newInstNum || (${view.ast.toString})")
    }
    println(s"Averaged reduced percent : ${evaluated.map((x) => 1.0 - x._4.toDouble / x._3.toDouble).sum / evaluated.length.toDouble}")

  }
}
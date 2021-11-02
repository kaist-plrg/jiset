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
        val beforeT = System.currentTimeMillis()
        val algos = PartialEval(view)
        val execT = System.currentTimeMillis() - beforeT
        val reducedAlgos = algos.filter((algo) => getInstNum(algoMap(algo.name).rawBody) != getInstNum(algo.rawBody))
        val reducedNum = reducedAlgos.length
        val origInstNum = reducedAlgos.map((algo) => getInstNum(algoMap(algo.name).rawBody)).sum
        val newInstNum = reducedAlgos.map((algo) => getInstNum(algo.rawBody)).sum
        (view, reducedNum, origInstNum, newInstNum, execT)
      }
    )
    println("ast : reduced Algo Num / original Inst Num / reduced Inst Num / diff / reduced percent / execution time")
    evaluated.foreach {
      case (view, reducedNum, origInstNum, newInstNum, execT) =>
        println(s"[${view.ast.kind}] : $reducedNum / $origInstNum / $newInstNum / ${origInstNum - newInstNum} / ${if (origInstNum != 0) 1.0 - newInstNum.toDouble / origInstNum.toDouble else 0.0} / ${execT.toDouble / 1000.0} || (${view.ast.toString})")
    }
    println(s"Average execution time : ${evaluated.map((x) => x._5.toDouble / 1000.0).sum / evaluated.length.toDouble}")
    println(s"Average reduced percent : ${evaluated.filter(_._3 > 0).map((x) => 1.0 - x._4.toDouble / x._3.toDouble).sum / evaluated.length.toDouble}")

  }
}
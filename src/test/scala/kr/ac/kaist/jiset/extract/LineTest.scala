package kr.ac.kaist.jiset.extract

import kr.ac.kaist.jiset.JISETTest
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.extractor.ECMAScriptParser
import kr.ac.kaist.jiset.ir.{ UnitWalker, Inst, ISeq }
import kr.ac.kaist.jiset.util.Useful.{ time => mtime }
import org.scalatest._

class LineTest extends ExtractTest {
  val name: String = "extractJsonTest"

  // check all instruction has line info
  class LineChecker extends UnitWalker {
    var targetInst: Option[Inst] = None
    override def walk(inst: Inst): Unit = inst match {
      case iseq: ISeq => super.walk(iseq)
      case _ => inst.line match {
        case None => targetInst = Some(inst)
        case _ => super.walk(inst)
      }
    }
  }

  // registration
  def init: Unit = {
    val (_, spec) = mtime(s"parsing spec.html", {
      ECMAScriptParser(VERSION, "", false)
    })
    for {
      algo <- spec.algos
      body = algo.rawBody
      checker = new LineChecker
      _ = checker.walk(body)
    } checker.targetInst match {
      case Some(i) =>
        fail(s"${algo.name} has an instruction with no line (${i.beautified})")
      case None =>
    }
  }
  init
}

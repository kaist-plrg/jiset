package kr.ac.kaist.jiset.checker

import io.circe._, io.circe.syntax._
import kr.ac.kaist.jiset.checker.NativeHelper._
import kr.ac.kaist.jiset.cfg.CFG
import kr.ac.kaist.jiset.util.Useful.{ time => showTime }
import kr.ac.kaist.jiset.{ JISETTest, VERSION, LOG_DIR }
import org.scalatest._

class JsonMiddleTest extends CheckerTest {
  val name: String = "checkerJsonTest"

  // registration
  def init: Unit = check(VERSION, {
    val sem = JISETTest.sem

    // import json protocols
    import jsonProtocol._

    // json check
    val (_, json) = showTime("encode abstract semantics in a JSON format", {
      sem.asJson
    })
    val (_, newSem) = showTime("decode abstract semantics in a JSON format", {
      json.as[AbsSemantics].getOrElse(AbsSemantics())
    })
    assert(sem == newSem)

    // dump and load check
    val dirname = s"$LOG_DIR/semantics"
    showTime("dump abstract semantics", {
      dumpSem(sem, dirname)
    })
    val (_, loadedSem) = showTime("load abstract semantics", {
      loadSem(dirname)
    })
    assert(sem == loadedSem)
  })
  init
}

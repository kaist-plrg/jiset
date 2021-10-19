package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.error.{ InterpTimeout, AnalysisTimeout, AnalysisImprecise, NotSupported }
import kr.ac.kaist.jiset.js.JSTest
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.Test262._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._
import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._

trait Test262Test extends JSTest {
  override val category: String = "test262"

  // directory name
  val logDir = s"$LOG_DIR/test262_$dateStr"

  // test kinds
  type TestKind = TestKind.Value
  object TestKind extends Enumeration {
    val Eval, EvalManual, Analyze = Value
    def getName(kind: Value) = kind match {
      case Eval => "eval"
      case EvalManual => "eval-manual"
      case Analyze => "analyze"
    }
  }

  type MaxIJK = AbsSemantics.MaxIJK

  // analyze result for jsaver
  case class AnalyzeResult(test: String, jsaver: String, jsaverTime: Double)
  implicit lazy val AnalyzeResultDecoder: Decoder[AnalyzeResult] = deriveDecoder
  implicit lazy val AnalyzeResultEncoder: Encoder[AnalyzeResult] = deriveEncoder

  // get analyze result
  def getAnalyzeResult(kind: TestKind, result: String) = kind match {
    case TestKind.Analyze => Some(result)
    case _ => None
  }

  // test 262 tests
  def test262Test(
    targets: List[NormalTestConfig],
    kind: TestKind
  ): Unit = {
    val name = TestKind.getName(kind)
    val progress = ProgressBar(s"test262 $name test", targets)
    val summary = progress.summary
    var ijkInfo: Map[String, (MaxIJK, MaxIJK)] = Map()
    var analyzeResult: List[AnalyzeResult] = List()
    mkdir(logDir)
    if (LOG) mkdir(VISITED_LOG_DIR)
    dumpFile(JISETTest.spec.version, s"$logDir/ecma262-version")
    dumpFile(currentVersion(BASE_DIR), s"$logDir/jiset-version")
    summary.timeouts.setPath(s"$logDir/$name-timeout.log")
    summary.yets.setPath(s"$logDir/$name-yet.log")
    summary.fails.setPath(s"$logDir/$name-fail.log")
    summary.passes.setPath(s"$logDir/$name-pass.log")
    for (config <- progress) {
      val NormalTestConfig(name, includes) = config
      val jsName = s"$TEST262_TEST_DIR/$name"

      val start = System.currentTimeMillis
      val testResult = try {
        val includeStmts = includes.foldLeft(basicStmts) {
          case (li, s) => for {
            x <- li
            y <- getInclude(s)
          } yield x ++ y
        } match {
          case Right(l) => l
          case Left(msg) => throw NotSupported(msg)
        }
        val stmts = includeStmts ++ flattenStmt(parseFile(jsName))
        val merged = mergeStmt(stmts)
        kind match {
          case TestKind.Analyze =>
            val absSem = analyzeTest(merged)
            ijkInfo += name -> ((absSem.irIJK, absSem.jsIJK))
            Some(absSem.finalResult.value.isAbruptCompletion.getSingle match {
              case FlatBot => { summary.fails += s"$name"; "B" }
              case FlatElem(Bool(false)) => { summary.passes += name; "P" }
              case FlatElem(Bool(true)) => { summary.fails += s"$name"; "F" }
              case FlatTop => { summary.fails += s"$name"; "T" }
            })
          case _ =>
            evalTest(merged, jsName)
            summary.passes += name
            None
        }
      } catch {
        case InterpTimeout | AnalysisTimeout =>
          summary.timeouts += name
          getAnalyzeResult(kind, "B")
        case AnalysisImprecise(msg) =>
          summary.fails += s"$name: $msg"
          getAnalyzeResult(kind, "T")
        case NotSupported(msg) =>
          summary.yets += s"$name: $msg"
          getAnalyzeResult(kind, "B")
        case e: Throwable =>
          summary.fails += s"$name: ${e.getMessage}"
          getAnalyzeResult(kind, "B")
      }
      val elapsed = (System.currentTimeMillis - start) / 1000.0d
      // record analysis result
      testResult match {
        case Some(result) => analyzeResult :+= AnalyzeResult(name, result, elapsed)
        case None =>
      }
    }
    summary.close

    kind match {
      case TestKind.Analyze =>
        // dump ijk info
        val data = (ijkInfo.map {
          case (k, (ir, js)) => k -> (ir.get, js.get)
        }).toMap
        dumpJson(data, s"$logDir/$name-ijk", true)
        val (irIJK, jsIJK) = (AbsSemantics.MaxIJK(), AbsSemantics.MaxIJK())
        ijkInfo.values.foreach {
          case (ir, js) =>
            irIJK.update(ir.get)
            jsIJK.update(js.get)
        }
        dumpFile(s"[IR] $irIJK\n[JS] $jsIJK", s"$logDir/$name-ijk-summary")

        // dump jsaver result
        dumpJson(analyzeResult, s"$logDir/jsaver-result.json", true)
        val P = analyzeResult.count(_.jsaver === "P")
        val T = analyzeResult.count(_.jsaver === "T")
        val F = analyzeResult.count(_.jsaver === "F")
        val B = analyzeResult.count(_.jsaver === "B")
        val total = analyzeResult.map(_.jsaverTime).sum
        val avg = total / analyzeResult.length
        dumpFile(
          s"[jsaver] P/T/F/B = $P/$T/$F/$B in $total seconds (avg: $avg)",
          s"$logDir/jsaver-result-summary"
        )
      case _ =>
        // dump IR logger
        IRLogger.dumpTo(s"$logDir/$name-logger")
    }

    // dump logs
    dumpFile(summary, s"$logDir/$name-summary")
    if (summary.timeout > 0) println(s"${summary.timeout} tests are timeout.")
    if (summary.yet > 0) println(s"${summary.yet} tests are not yet supported.")
    if (summary.fail > 0) fail(s"${summary.fail} tests are failed.")
  }
}

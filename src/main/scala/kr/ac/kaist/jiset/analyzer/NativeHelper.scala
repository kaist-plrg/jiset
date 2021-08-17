package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.cfg.{ DotPrinter => _, _ }
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._
import scala.Console._

object NativeHelper {
  // path in CFG
  val CFG_PATH = s"$ANALYZE_LOG_DIR/cfg"
  mkdir(CFG_PATH)

  // TODO dump CFG in DOT/PDF format
  def dumpCFG(
    cp: Option[ControlPoint] = None,
    pdf: Boolean = true,
    depth: Option[Int] = None,
    path: Option[Path] = None
  ): Unit = ???

  // dump CFG in DOT/PDF format
  def dumpFunc(
    func: Function,
    pdf: Boolean = true
  ): Unit = try {
    val dot = func.toDot
    dumpFile(dot, s"$CFG_PATH.dot")
    if (pdf) {
      executeCmd(s"""unflatten -l 10 -o ${CFG_PATH}_trans.dot $CFG_PATH.dot""")
      executeCmd(s"""dot -Tpdf "${CFG_PATH}_trans.dot" -o "$CFG_PATH.pdf"""")
      println(s"Dumped CFG to $CFG_PATH.pdf")
    } else println(s"Dumped CFG to $CFG_PATH.dot")
  } catch {
    case _: Throwable => printlnColor(RED)(s"Cannot dump CFG")
  }
}

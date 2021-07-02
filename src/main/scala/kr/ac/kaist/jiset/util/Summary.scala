package kr.ac.kaist.jiset.util

import java.io.PrintWriter
import kr.ac.kaist.jiset.LINE_SEP

class Summary {
  // not yet supported
  val yets: SummaryElem = new SummaryElem
  def yet: Int = yets.size

  // fail
  val fails: SummaryElem = new SummaryElem
  def fail: Int = fails.size

  // pass
  val passes: SummaryElem = new SummaryElem
  def pass: Int = passes.size

  // close all print writers
  def close: Unit = { yets.close; fails.close; passes.close }

  // time
  var timeMillis: Long = 0L

  // total cases
  def total: Int = yet + fail + pass

  // supported total cases
  def supported: Int = fail + pass

  // success rate
  def successRate: Double = pass.toDouble / supported

  // get simple string
  def simpleString: String =
    if (yet > 0) s"Y/F/P = $yet/$fail/$pass"
    else s"F/P = $fail/$pass"

  // conversion to string
  override def toString: String = {
    val app = new Appender
    app >> f"time: $timeMillis%,d ms" >> LINE_SEP
    app >> f"total: $total%,d" >> LINE_SEP
    if (yet > 0) app >> f"- yet: $yet%,d" >> LINE_SEP
    app >> f"- fail: $fail%,d" >> LINE_SEP
    app >> f"- pass: $pass%,d" >> LINE_SEP
    app >> f"pass-rate: $pass%,d/$supported%,d ($successRate%2.2f%%)"
    app.toString
  }
}

class SummaryElem {
  // vector
  private var vector: Vector[String] = Vector()

  // print writer
  var nfOpt: Option[PrintWriter] = None
  def setPath(nf: PrintWriter): Unit = nfOpt = Some(nf)
  def setPath(filename: String): Unit = setPath(Useful.getPrintWriter(filename))
  def close: Unit = nfOpt.map(_.close())

  // size
  def size: Int = vector.size

  // add data
  def +=(data: String): Unit = {
    nfOpt.map(_.println(data))
    vector :+= data
  }
}

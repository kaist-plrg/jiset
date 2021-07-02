package kr.ac.kaist.jiset.util

import kr.ac.kaist.jiset.LINE_SEP

class Summary {
  // not yet supported
  var yets: Vector[String] = Vector()
  def yet: Int = yets.length

  // fail
  var fails: Vector[String] = Vector()
  def fail: Int = fails.length

  // pass
  var passes: Vector[String] = Vector()
  def pass: Int = passes.length

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

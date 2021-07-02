package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._

// IREval phase
case object IREval extends PhaseObj[State, IREvalConfig, State] {
  val name: String = "eval-ir"
  val help: String = "evaluates a given IR state."

  def apply(
    st: State,
    jisetConfig: JISETConfig,
    config: IREvalConfig
  ): State = {
    val filename = getFirstFilename(jisetConfig, "eval-ir")
    Interp(st, filename, config.timeout)
  }

  def defaultConfig: IREvalConfig = IREvalConfig()
  val options: List[PhaseOption[IREvalConfig]] = List(
    ("timeout", NumOption((c, i) => c.timeout = if (i == 0) None else Some(i)),
      "set timeout of interpreter(second), 0 for unlimited.")
  )
}

// IREval phase config
case class IREvalConfig(
  var timeout: Option[Long] = Some(TIMEOUT)
) extends Config

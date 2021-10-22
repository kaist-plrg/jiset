package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.editor._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.NativeHelper._
import kr.ac.kaist.jiset.js.{ Parser => ESParser }

// IREval phase
case object IRPEval extends Phase[Unit, IRPEvalConfig, Algo] {
  val name: String = "peval-ir"
  val help: String = "partial evaluates a given Script."

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: IRPEvalConfig
  ): Algo = {
    val filename = getFirstFilename(jisetConfig, "peval")
    val f = fileReader(filename)
    setSpec(loadSpec(s"$VERSION_DIR/generated"))
    val p = ESParser.rules(config.target)
    val result = ESParser.parse(p(config.parseOption.toList.map(_ == 'T')), f)
    if (result.successful) BasePartialEvalImpl(SyntacticView(result.get.checkSupported))
    else throw new Error("re-parse fail")
  }

  def defaultConfig: IRPEvalConfig = IRPEvalConfig()
  val options: List[PhaseOption[IRPEvalConfig]] = List(
    ("timeout", NumOption((c, i) => c.timeout = if (i == 0) None else Some(i)),
      "set timeout of interpreter(second), 0 for unlimited."),
    ("target", StrOption((c, i) => c.target = i),
      "set parse target (default = ScriptBody)."),
    ("parse-option", StrOption((c, i) => c.parseOption = i),
      "set parse option (default = empty)."),
  )
}

// IREval phase config
case class IRPEvalConfig(
  var timeout: Option[Long] = Some(TIMEOUT),
  var target: String = "ScriptBody",
  var parseOption: String = ""
) extends Config

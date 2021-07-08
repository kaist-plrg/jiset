package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._

// BuildCFG phase
case object BuildCFG extends PhaseObj[ECMAScript, BuildCFGConfig, CFG] {
  val name = "build-cfg"
  val help = "builds control flow graph (CFG)."

  def apply(
    spec: ECMAScript,
    jisetConfig: JISETConfig,
    config: BuildCFGConfig
  ): CFG = {
    val (cfgTime, cfg) = time("build CFG", new CFG(spec))

    if (config.dot) {
      mkdir(CFG_DIR)
      val format = if (config.pdf) "DOT/PDF" else "DOT"
      ProgressBar(s"dump CFG in a $format format", cfg.funcs).foreach(f => {
        val name = s"${CFG_DIR}/${f.name}"
        dumpFile(f.toDot, s"$name.dot")
        if (config.pdf) {
          // check whether dot is available
          if (isNormalExit("dot -V")) {
            try executeCmd(s"dot -Tpdf $name.dot -o $name.pdf") catch {
              case ex: Exception => println(s"[ERROR] $name: exception occur while converting to pdf")
            }
          } else println("Dot is not installed!")
        }
      })
    }

    cfg
  }

  def defaultConfig: BuildCFGConfig = BuildCFGConfig()
  val options: List[PhaseOption[BuildCFGConfig]] = List(
    ("dot", BoolOption(c => c.dot = true),
      "dump the cfg in a dot format."),
    ("pdf", BoolOption(c => { c.dot = true; c.pdf = true }),
      "dump the cfg in a dot and pdf format.")
  )
}

// BuildCFG config
case class BuildCFGConfig(
  var dot: Boolean = false,
  var pdf: Boolean = false
) extends Config

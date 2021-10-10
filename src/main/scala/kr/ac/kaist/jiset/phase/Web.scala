package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.web.WebServer
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.js

// Web phase
case object Web extends Phase[ECMAScript, WebConfig, Unit] {
  val name = "web"
  val help = "Run JISET web for interactive ECMAScript execution."

  def apply(
    spec: ECMAScript,
    jisetConfig: JISETConfig,
    config: WebConfig
  ): Unit = {
    // set spec
    js.setSpec(spec);
    // run web server
    WebServer.run(config.port)
  }
  def defaultConfig: WebConfig = WebConfig()
  val options: List[PhaseOption[WebConfig]] = List(
    ("web", NumOption((c, i) => c.port = i),
      "the JISET web server port (default: 8080)."),
  )
}

// Web phase config
case class WebConfig(var port: Int = 8080) extends Config

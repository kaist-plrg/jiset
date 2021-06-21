package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.extractor.ECMAScriptParser
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util.Useful._

package object js {
  // current ECMAScript
  private var targetSpec: Option[ECMAScript] = None
  def setTarget(spec: ECMAScript): Unit = targetSpec = Some(spec)
  lazy val spec: ECMAScript = optional(targetSpec.getOrElse {
    readJson[ECMAScript](MODEL_PATH)
  }).getOrElse {
    Console.err.println(s"[WARNING] no cache in $MODEL_PATH!!")
    val (_, spec) = time(s"  - parsing ECMAScript ($DEFAULT_VERSION)", {
      ECMAScriptParser(DEFAULT_VERSION, "", false)
    })
    dumpJson(spec, MODEL_PATH)
    spec
  }

  // ECMAScript components
  lazy val consts: Set[String] = spec.consts
  lazy val intrinsics: Set[String] = spec.intrinsics
  lazy val symbols: Set[String] = spec.symbols
  lazy val algos: Map[String, Algo] =
    spec.algos.map(algo => algo.name -> algo).toMap

  // conversion intrinsics to address
  def intrinsicToAddr(name: String): Addr =
    NamedAddr(GLOBAL + "." + name.replaceAll("_", "."))

  // constants
  val AGENT = "AGENT"
  val ALGORITHM = "ALGORITHM"
  val CONTEXT = "CONTEXT"
  val EXECUTION_STACK = "EXECUTION_STACK"
  val FILENAME = "FILENAME"
  val GLOBAL = "GLOBAL"
  val HOST_DEFINED = "HOST_DEFINED"
  val INTRINSICS = "INTRINSICS"
  val JOB_QUEUE = "JOB_QUEUE"
  val PRIMITIVE = "PRIMITIVE"
  val REALM = "REALM"
  val RESULT = "RESULT"
  val RETURN = "RETURN"
  val SCRIPT_BODY = "SCRIPT_BODY"
  val SYMBOL_REGISTRY = "SYMBOL_REGISTRY"
  val TOP_LEVEL = "TOP_LEVEL"
  val TYPED_ARRAY_INFO = "TYPED_ARRAY_INFO"

  // prefixes
  val CONST_PREFIX = "CONST_"
  val INTRINSIC_PREFIX = "INTRINSIC_"
  val SYMBOL_PREFIX = "SYMBOL_"
}

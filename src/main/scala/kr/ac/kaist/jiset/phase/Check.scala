package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.checker._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.spec.algorithm.Algo
import scala.annotation.unused

// Check phase
case object Check extends PhaseObj[ECMAScript, CheckConfig, List[Bug]] {
  val name = "check"
  val help = "performs static checkers for specifications."

  def apply(
    spec: ECMAScript,
    jisetConfig: JISETConfig,
    config: CheckConfig
  ): List[Bug] = {
    println(s"--------------------------------------------------")
    val completeAlgos = spec.completedAlgos
    val targets =
      if (config.target.isEmpty) completeAlgos
      else completeAlgos.filter(config.target contains _.name)
    println(s"checking ${targets.size} algorithms...")

    println
    val refErrors = time(s"check variable reference", {
      ReferenceChecker(spec, targets)
    })
    refErrors.foreach(println _)
    println(s"${refErrors.length} algorithms have reference errors.")

    println
    val missingRets = time(s"check missing return branch", {
      MissingRetChecker(spec, targets)
    })
    missingRets.foreach(println _)
    println(s"${missingRets.length} algorithms have missing return branch errors.")

    println
    val arityErrors = time(s"check arity", {
      ArityChecker(spec, targets)
    })
    arityErrors.foreach(println _)
    println(s"# of arity mismatch : ${arityErrors.length}")

    println
    val bugs = refErrors ++ missingRets ++ arityErrors
    println(s"Total ${bugs.length} bugs detected.")
    bugs
  }

  def defaultConfig: CheckConfig = CheckConfig()
  val options: List[PhaseOption[CheckConfig]] = List(
    ("target", ListOption((c, l) => c.target = l),
      "target algorithms to check")
  )
}

// Check phase config
case class CheckConfig(
  var target: List[String] = Nil
) extends Config

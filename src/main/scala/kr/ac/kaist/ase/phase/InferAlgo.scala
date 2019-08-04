package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.algorithm._
import kr.ac.kaist.ase.core
import kr.ac.kaist.ase.core.Inst
import kr.ac.kaist.ase.model.AlgoCompiler
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import scala.util.parsing.combinator._
import scala.io.Source

// InferAlgo phase
case object InferAlgo extends PhaseObj[Algorithm, InferAlgoConfig, Unit] {
  val name = "infer-algo"
  val help = "Infers algorithm files."

  def apply(
    algo: Algorithm,
    aseConfig: ASEConfig,
    config: InferAlgoConfig
  ): Unit = {
    val name = getScalaName(removedExt(getSimpleFilename(getFirstFilename(aseConfig, "parse"))))
    val ac = AlgoCompiler(name, algo)
    val (func, failed) = ac.result
    failed.toSeq.sortBy(_._1) foreach {
      case (k, tokens) => {
        println(s"[FailedLine]:$k: ${Token.getString(tokens)}")
        val inferList = inferStmt(ac, tokens)
        inferList.zipWithIndex.foreach {
          case (inst, i) => println(s"[Infer]: $i: ${core.beautify(inst)}")
        }
      }
    }
  }

  def inferStmt(ac: AlgoCompiler, tokens: List[Token]): List[Inst] = {
    (0 until tokens.length - 1).map((i) =>
      (i + 1 until tokens.length).map((j) => {
        val newTokens = tokens.slice(0, i) ++ List(Star(tokens.slice(i, j).mkString(" "))) ++ tokens.slice(j, tokens.length)
        ac.parseAll(ac.stmt, newTokens) match {
          case ac.Success(res, _) => Some(res)
          case ac.NoSuccess(msg, _) => None
        }
      }).collect {
        case Some(inst) => inst
      }).foldLeft(List[Inst]())(_ ++ _)
  }

  def defaultConfig: InferAlgoConfig = InferAlgoConfig()
  val options: List[PhaseOption[InferAlgoConfig]] = List()
}

// InferAlgo phase config
case class InferAlgoConfig() extends Config

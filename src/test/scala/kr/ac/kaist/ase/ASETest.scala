package kr.ac.kaist.ase

import java.io._
import kr.ac.kaist.ase.phase._
import kr.ac.kaist.ase.util._
import org.scalatest._
import scala.io.Source

abstract class ASETest extends FunSuite {
  // ase configuration
  lazy val aseConfig: ASEConfig = ASEConfig(CmdBase, Nil)
}

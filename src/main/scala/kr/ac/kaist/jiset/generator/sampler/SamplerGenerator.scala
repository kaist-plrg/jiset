package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._

object SamplerGenerator {
  def apply(packageName: String, modelDir: String, grammar: Grammar, debug: Boolean = false): Unit = {
    LimitedDepthSamplerGenerator(packageName, modelDir, grammar, debug)
    NonRecursiveSamplerGenerator(packageName, modelDir, grammar, debug)
    MinSamplerGenerator(grammar).generate(packageName, modelDir)
  }
}

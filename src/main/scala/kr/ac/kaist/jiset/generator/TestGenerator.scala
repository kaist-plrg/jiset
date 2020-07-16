package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec._

object TestGenerator {
  def apply(
    packageName: String = "kr.ac.kaist.ires",
    modelDir: String = "../ires/src/main/scala/kr/ac/kaist/ires/model",
    resourceDir: String = "../ires/src/main/resources",
    grammar: Grammar = Spec(s"$RESOURCE_DIR/$VERSION/auto/spec.json").grammar
  ): Unit = {
    DepthCounter(grammar).generate(packageName, modelDir)
    TargetRHSGenerator(grammar).generate(resourceDir)
    SamplerGenerator(packageName, modelDir, grammar)
  }
}

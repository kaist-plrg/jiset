package kr.ac.kaist.jiset.analyzer.domain.ast

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

object SimpleDomain extends generator.SimpleDomain[ASTVal]
  with ast.Domain

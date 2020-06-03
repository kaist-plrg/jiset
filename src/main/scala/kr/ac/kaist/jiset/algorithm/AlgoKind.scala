package kr.ac.kaist.jiset.algorithm

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.parser
import kr.ac.kaist.jiset.util.Useful.readFile
import spray.json._

// algorithm kinds
trait AlgoKind
case object RuntimeSemantics extends AlgoKind
case object StaticSemantics extends AlgoKind
case object Method extends AlgoKind

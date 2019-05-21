package kr.ac.kaist.ase.algorithm

import kr.ac.kaist.ase.LINE_SEP

// tokens
trait Token
case class Value(value: String) extends Token
case class Id(id: String) extends Token
case class StepList(steps: List[Step]) extends Token
case class Text(text: String) extends Token

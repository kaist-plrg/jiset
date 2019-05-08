package kr.ac.kaist.ase

sealed trait Condition
case class Condition0(e0: A0Condition, e1: A0Condition) extends Condition // $Condition1 or opt(if) $Condition1
case class Condition1(e0: A0Condition, e1: A0Condition) extends Condition // $Condition1 and $Condition1
case class Condition2(e0: A0Condition) extends Condition // $Condition1

sealed trait A0Condition
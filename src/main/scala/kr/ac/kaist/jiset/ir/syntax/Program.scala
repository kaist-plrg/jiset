package kr.ac.kaist.jiset.ir

// IR Programs
case class Program(insts: List[Inst]) extends IRElem
object Program extends Parser[Program]

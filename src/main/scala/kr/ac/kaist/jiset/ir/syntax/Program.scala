package kr.ac.kaist.jiset.ir

// IR Programs
case class Program(insts: List[Inst]) extends IRComponent
object Program extends Parser[Program]

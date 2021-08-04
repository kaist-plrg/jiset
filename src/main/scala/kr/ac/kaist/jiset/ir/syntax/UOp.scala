package kr.ac.kaist.jiset.ir

// IR Unary Operators
sealed trait UOp extends IRElem
object UOp extends Parser[UOp]
case object ONeg extends UOp
case object ONot extends UOp
case object OBNot extends UOp

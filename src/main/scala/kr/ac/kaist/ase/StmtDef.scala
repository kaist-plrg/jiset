package kr.ac.kaist.ase

sealed trait Stmt
case class Stmt0(e0: Var, e1: Expr) extends Stmt // Let $Var be $Expr
case class Stmt1(e0: Condition, e1: Stmt, e2: Stmt) extends Stmt // If $Condition , $Stmt (.|;) (Otherwise|otherwise) opt(,) $Stmt
case class Stmt2(e0: Condition, e1: Stmt) extends Stmt // If $Condition, opt(then) $Stmt
case class Stmt3(e0: Stmt) extends Stmt // (Otherwise | Else) , $Stmt
case class Stmt4(e0: Condition, e1: Stmt) extends Stmt // Else opt(if) $Condition, $Stmt
case class Stmt5(e0: Expr) extends Stmt // (Return|return) $Expr
case class Stmt6(e0: Condition) extends Stmt // Assert : $Condition
case class Stmt7(e0: Value) extends Stmt // throw a $Value exception
case class Stmt8(e0: Expr) extends Stmt // (Perform|perform) $Expr
case class Stmt9(e0: Settable, e1: Expr) extends Stmt // (Set|set) $Settable to $Expr
case class Stmt10(e0: Expr) extends Stmt // Call $Expr
case class Stmt11(e0: Stmt) extends Stmt // Repeat, $Stmt
case class Stmt12(e0: Expr, e1: Var) extends Stmt // Append $Expr to $Var
case class Stmt13(e0: Expr) extends Stmt // change its bound value to $Expr
case class Stmt14() extends Stmt // line-list

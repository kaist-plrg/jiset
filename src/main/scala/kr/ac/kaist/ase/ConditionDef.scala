package kr.ac.kaist.ase

sealed trait Condition
case class Condition0(e0: A0Condition, e1: A0Condition) extends Condition // $Condition1 or opt(if) $Condition1
case class Condition1(e0: A0Condition, e1: A0Condition) extends Condition // $Condition1 and $Condition1
case class Condition2(e0: A0Condition) extends Condition // $Condition1

sealed trait A0Condition

case class A0Condition0(e0: Var, e1: Var) extends A0Condition // $Var has a binding for the name that is the value of $Var
case class A0Condition1(e0: Var, e1: Var) extends A0Condition // $Var does not (opt(already) have a binding for | contain) $Var
case class A0Condition2(e0: Var, e1: Var) extends A0Condition // $Var must have an uninitialized binding for $Var
case class A0Condition3(e0: Var) extends A0Condition // $Var is the source code of a module
case class A0Condition4(e0: Expr, e1: Value) extends A0Condition // $Expr (is | has) the value $Value
case class A0Condition5(e0: Expr) extends A0Condition //  $Expr is not present
case class A0Condition6(e0: Expr, e1: Expr) extends A0Condition // $Expr is not $Expr
case class A0Condition7(e0: Expr, e1: TypeV, e2: TypeV) extends A0Condition //  $Expr is $Type or $Type
case class A0Condition8(e0: Expr, e1: TypeV) extends A0Condition // $Expr is $Type
case class A0Condition9(e0: Expr, e1: Expr) extends A0Condition // $Expr is $Expr
case class A0Condition10(e0: Expr, e1: Expr) extends A0Condition //  $Expr contains $Expr
case class A0Condition11(e0: Expr) extends A0Condition // $Expr cannot be deleted
case class A0Condition12(e0: Binding) extends A0Condition // $Binding is a strict binding
case class A0Condition13(e0: Binding) extends A0Condition // $Binding has not yet been initialized
case class A0Condition14(e0: Binding) extends A0Condition // $Binding is a mutable binding
case class A0Condition15(e0: Binding) extends A0Condition // $Binding is an indirect binding
case class A0Condition16(e0: Binding) extends A0Condition //  $Binding is an uninitialized binding
case class A0Condition17() extends A0Condition // This is an attempt to change the value of an immutable binding
case class A0Condition18(e0: Var, e1: Var) extends A0Condition // $Var has a binding for $Var
case class A0Condition19() extends A0Condition // (The|the) execution context stack is opt(now) empty
case class A0Condition20() extends A0Condition // no such execution context exists
case class A0Condition21(e0: Var) extends A0Condition // $Var is an abrupt completion
case class A0Condition22(e0: Var) extends A0Condition // $Var is the source code of a script
case class A0Condition23(e0: Var, e1: Field) extends A0Condition // $Var has a [ [ $Field ] ] field
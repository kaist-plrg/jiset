package kr.ac.kaist.ase

sealed trait Expr
case class Expr0(e0: A0Expr) extends Expr // ? $A1Expr
case class Expr1(e0: A0Expr) extends Expr // $A1Expr

sealed trait A0Expr
case class A0Expr0(e0: A1Expr, e1: List[Argument]) extends A0Expr // $AExpr ( $Arguments )
case class A0Expr1(e0: A1Expr) extends A0Expr // opt("the" | "a") $AExpr

sealed trait A1Expr
case class A1Expr0(e0: ValueV) extends A1Expr // opt("value") $Value
case class A1Expr1(e0: CodeV) extends A1Expr // $Code
case class A1Expr2(e0: ConstV) extends A1Expr // $Const
case class A1Expr3(e0: RecordV) extends A1Expr // $RecordV
case class A1Expr4(e0: EnvironmentV) extends A1Expr // $EnvironmentV
case class A1Expr5(e0: ExecContextV) extends A1Expr // $ExecContextV
case class A1Expr6(e0: ListV) extends A1Expr // $ListV
case class A1Expr7(e0: ObjectV) extends A1Expr // $ObjectV
case class A1Expr8(e0: SymbolV) extends A1Expr // $SymbolV
case class A1Expr9() extends A1Expr // the String value of the property name
case class A1Expr10(e0: Var, e1: AMERName) extends A1Expr // $Var . $AMERName
case class A1Expr11(e0: Var, e1: Var) extends A1Expr // value currently bound to $Var in $Var
case class A1Expr12(e0: Binding) extends A1Expr // $Binding
case class A1Expr13(e0: A0Settable) extends A1Expr // $Settable
case class A1Expr14(e0: TypeV, e1: List[InitExpr]) extends A1Expr // value of type $Type $InitializeType
case class A1Expr15(e0: AMERName) extends A1Expr // $AMERName

sealed trait Argument
case class Argument0(e0: Expr) extends Argument
case class Argument1(e0: Flag) extends Argument

case class Flag(e0: Field, e1: Expr)

case class ValueV(s: String)
case class CodeV(s: String)
case class ConstV(s: String)
case class Var(s: String)
sealed trait RecordV

case class RecordV0() extends RecordV // declarative Environment Record for which the method was invoked
case class RecordV1() extends RecordV // object Environment Record for which the method was invoked
case class RecordV2() extends RecordV // global Environment Record for which the method was invoked
case class RecordV3() extends RecordV // function Environment Record for which the method was invoked
case class RecordV4() extends RecordV // module Environment Record for which the method was invoked
case class RecordV5() extends RecordV // Agent Record of the surrounding agent
case class RecordV6() extends RecordV // new Record
case class RecordV7() extends RecordV // new global Environment Record
case class RecordV8() extends RecordV // new declarative Environment Record containing no bindings
case class RecordV9() extends RecordV // new module Environment Record containing no bindings
case class RecordV10(e0: Var) extends RecordV // new object Environment Record containing $Var as the binding object
case class RecordV11() extends RecordV // new function Environment Record containing no bindings
case class RecordV12() extends RecordV // new Realm Record
case class RecordV13(e0: RecordDescriptor, e1: List[Flag]) extends RecordV // $RecordDescriptor { $FlagArgs }

case class RecordDescriptor(s: String)

sealed trait EnvironmentV
case class EnvironmentV0() extends EnvironmentV // new Lexical Environment
case class EnvironmentV1(e0: ExecContextV) extends EnvironmentV // $ExecContextV ' s LexicalEnvironment

sealed trait ExecContextV
case class ExecContextV0() extends ExecContextV // new execution context
case class ExecContextV1() extends ExecContextV // running execution context

sealed trait ListV
case class ListV0() extends ListV // new empty List, « »
case class ListV1(e0: List[Argument]) extends ListV // « $Arguments »

sealed trait ObjectV
case class ObjectV0(e0: Var) extends ObjectV // binding object for $Var

sealed trait SymbolV
case class SymbolV0(e0: SymbolName) extends SymbolV // @ @ $SymbolName

case class SymbolName(s: String)

case class AMERName(s: String)

trait Binding
case class Binding0(e0: Var, e1: Var) extends Binding // the binding for $Var in $Var
case class Binding1(e0: Var) extends Binding // the binding for $Var

sealed trait Settable
case class Settable0(e0: A0Settable) extends Settable // opt(the) $Settable1

sealed trait A0Settable
case class A0Settable0(e0: Var, e1: Field) extends A0Settable // $Var. [ [ $Field ] ]
case class A0Settable1(e0: Var, e1: Var) extends A0Settable // $Var flag of $Var
case class A0Settable2(e0: Var, e1: Field) extends A0Settable // $Var ' s $Field opt(component)
case class A0Settable3(e0: Var) extends A0Settable
case class A0Settable4(e0: Var, e1: Field) extends A0Settable // $Field of $Var
case class A0Settable5(e0: Var) extends A0Settable // outer lexical environment reference of $Var
case class A0Settable6(e0: Var) extends A0Settable // value of $Var ' s outer environment reference
case class A0Settable7(e0: Var, e1: Var) extends A0Settable // bound value for $Var in $Var

case class TypeV(s: String)

sealed trait InitExpr
case class InitExpr0(e0: ComponentName, e1: Expr) extends InitExpr
case class InitExpr1(e0: FlagName, e1: Expr) extends InitExpr
case class ComponentName(s: String)
case class FlagName(s: String)
case class Field(s: String)

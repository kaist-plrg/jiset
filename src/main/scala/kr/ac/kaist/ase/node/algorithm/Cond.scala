package kr.ac.kaist.ase.node.algorithm

sealed trait Cond
case class Cond0(e0: A0Cond, e1: A0Cond) extends Cond // $Cond1 or opt(if) $Cond1
case class Cond1(e0: A0Cond, e1: A0Cond) extends Cond // $Cond1 and $Cond1
case class Cond2(e0: A0Cond) extends Cond // $Cond1

sealed trait A0Cond

case class A0Cond0(e0: Id, e1: Id) extends A0Cond // $Id has a binding for the name that is the value of $Id
case class A0Cond1(e0: Id, e1: Id) extends A0Cond // $Id does not (opt(already) have a binding for | contain) $Id
case class A0Cond2(e0: Id, e1: Id) extends A0Cond // $Id must have an uninitialized binding for $Id
case class A0Cond3(e0: Id) extends A0Cond // $Id is the source code of a module
case class A0Cond4(e0: Expr, e1: Value) extends A0Cond // $Expr (is | has) the value $Value
case class A0Cond5(e0: Expr) extends A0Cond //  $Expr is not present
case class A0Cond6(e0: Expr, e1: Expr) extends A0Cond // $Expr is not $Expr
case class A0Cond7(e0: Expr, e1: TypeV, e2: TypeV) extends A0Cond //  $Expr is $Type or $Type
case class A0Cond8(e0: Expr, e1: TypeV) extends A0Cond // $Expr is $Type
case class A0Cond9(e0: Expr, e1: Expr) extends A0Cond // $Expr is $Expr
case class A0Cond10(e0: Expr, e1: Expr) extends A0Cond //  $Expr contains $Expr
case class A0Cond11(e0: Expr) extends A0Cond // $Expr cannot be deleted
case class A0Cond12(e0: Binding) extends A0Cond // $Binding is a strict binding
case class A0Cond13(e0: Binding) extends A0Cond // $Binding has not yet been initialized
case class A0Cond14(e0: Binding) extends A0Cond // $Binding is a mutable binding
case class A0Cond15(e0: Binding) extends A0Cond // $Binding is an indirect binding
case class A0Cond16(e0: Binding) extends A0Cond //  $Binding is an uninitialized binding
case class A0Cond17() extends A0Cond // This is an attempt to change the value of an immutable binding
case class A0Cond18(e0: Id, e1: Id) extends A0Cond // $Id has a binding for $Id
case class A0Cond19() extends A0Cond // (The|the) execution context stack is opt(now) empty
case class A0Cond20() extends A0Cond // no such execution context exists
case class A0Cond21(e0: Id) extends A0Cond // $Id is an abrupt completion
case class A0Cond22(e0: Id) extends A0Cond // $Id is the source code of a script
case class A0Cond23(e0: Id, e1: Field) extends A0Cond // $Id has a [ [ $Field ] ] field

trait CondParsers { this: AlgorithmParsers =>

  def condition0: Parser[Cond0] = {
    a0condition ~ "or" ~ opt("if") ~ a0condition ^^ {
      case e0 ~ _ ~ _ ~ e1 => Cond0(e0, e1)
    }
  }
  def condition1: Parser[Cond1] = {
    a0condition ~ "and" ~ a0condition ^^ {
      case e0 ~ _ ~ e1 => Cond1(e0, e1)
    }
  }
  def condition2: Parser[Cond2] = {
    a0condition ^^ {
      case e0 => Cond2(e0)
    }
  }
  lazy val condition: Parser[Cond] = condition0 | condition1 | condition2
  def a0condition0: Parser[A0Cond0] = {
    id ~ "has" ~ "a" ~ "binding" ~ "for" ~ "the" ~ "name" ~ "that" ~ "is" ~ "the" ~ "value" ~ "of" ~ id ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ e1 => A0Cond0(e0, e1)
    }
  }
  def a0condition1: Parser[A0Cond1] = {
    id ~ "does" ~ "not" ~ ((opt("already") ~ "have" ~ "a" ~ "binding" ~ "for") | "contain") ~ id ^^ {
      case e0 ~ _ ~ _ ~ _ ~ e1 => A0Cond1(e0, e1)
    }
  }
  def a0condition2: Parser[A0Cond2] = {
    id ~ "must" ~ "have" ~ "an" ~ "uninitialized" ~ "binding" ~ "for" ~ id ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ e1 => A0Cond2(e0, e1)
    }
  }
  def a0condition3: Parser[A0Cond3] = {
    id ~ "is" ~ "the" ~ "source" ~ "code" ~ "of" ~ "a" ~ "module" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ => A0Cond3(e0)
    }
  }
  def a0condition4: Parser[A0Cond4] = {
    expr ~ ("is" | "has") ~ "the" ~ "value" ~ value ^^ {
      case e0 ~ _ ~ _ ~ _ ~ e1 => A0Cond4(e0, e1)
    }
  }
  def a0condition5: Parser[A0Cond5] = {
    expr ~ "is" ~ "not" ~ "present" ^^ {
      case e0 ~ _ ~ _ ~ _ => A0Cond5(e0)
    }
  }
  def a0condition6: Parser[A0Cond6] = {
    expr ~ "is" ~ "not" ~ expr ^^ {
      case e0 ~ _ ~ _ ~ e1 => A0Cond6(e0, e1)
    }
  }
  def a0condition7: Parser[A0Cond7] = {
    expr ~ "is" ~ typev ~ "or" ~ typev ^^ {
      case e0 ~ _ ~ e1 ~ _ ~ e2 => A0Cond7(e0, e1, e2)
    }
  }
  def a0condition8: Parser[A0Cond8] = {
    expr ~ "is" ~ typev ^^ {
      case e0 ~ _ ~ e1 => A0Cond8(e0, e1)
    }
  }
  def a0condition9: Parser[A0Cond9] = {
    expr ~ "is" ~ expr ^^ {
      case e0 ~ _ ~ e1 => A0Cond9(e0, e1)
    }
  }
  def a0condition10: Parser[A0Cond10] = {
    expr ~ "contains" ~ expr ^^ {
      case e0 ~ _ ~ e1 => A0Cond10(e0, e1)
    }
  }
  def a0condition11: Parser[A0Cond11] = {
    expr ~ "cannot" ~ "be" ~ "deleted" ^^ {
      case e0 ~ _ ~ _ ~ _ => A0Cond11(e0)
    }
  }
  def a0condition12: Parser[A0Cond12] = {
    binding ~ "is" ~ "a" ~ "strict" ~ "binding" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ => A0Cond12(e0)
    }
  }
  def a0condition13: Parser[A0Cond13] = {
    binding ~ "has" ~ "not" ~ "yet" ~ "been" ~ "initialized" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ ~ _ => A0Cond13(e0)
    }
  }
  def a0condition14: Parser[A0Cond14] = {
    binding ~ "is" ~ "a" ~ "mutable" ~ "binding" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ => A0Cond14(e0)
    }
  }
  def a0condition15: Parser[A0Cond15] = {
    binding ~ "is" ~ "an" ~ "indirect" ~ "binding" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ => A0Cond15(e0)
    }
  }
  def a0condition16: Parser[A0Cond16] = {
    binding ~ "is" ~ "an" ~ "uninitialized" ~ "binding" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ => A0Cond16(e0)
    }
  }
  def a0condition17: Parser[A0Cond17] = {
    "This" ~ "is" ~ "an" ~ "attempt" ~ "to" ~ "change" ~ "the" ~ "value" ~ "of" ~ "an" ~ "immutable" ~ "binding" ^^ {
      case _ => A0Cond17()
    }
  }
  def a0condition18: Parser[A0Cond18] = {
    id ~ "has" ~ "a" ~ "binding" ~ "for" ~ id ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ ~ e1 => A0Cond18(e0, e1)
    }
  }
  def a0condition19: Parser[A0Cond19] = {
    ("The" | "the") ~ "execution" ~ "context" ~ "stack" ~ "is" ~ opt("now") ~ "empty" ^^ {
      case _ => A0Cond19()
    }
  }
  def a0condition20: Parser[A0Cond20] = {
    "no" ~ "such" ~ "execution" ~ "context" ~ "exists" ^^ {
      case _ => A0Cond20()
    }
  }
  def a0condition21: Parser[A0Cond21] = {
    id ~ "is" ~ "an" ~ "abrupt" ~ "completion" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ => A0Cond21(e0)
    }
  }
  def a0condition22: Parser[A0Cond22] = {
    id ~ "is" ~ "the" ~ "source" ~ "code" ~ "of" ~ "a" ~ "script" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ => A0Cond22(e0)
    }
  }
  def a0condition23: Parser[A0Cond23] = {
    id ~ "has" ~ "a" ~ "[" ~ "[" ~ field ~ "]" ~ "]" ~ "field" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ ~ e1 ~ _ ~ _ ~ _ => A0Cond23(e0, e1)
    }
  }
  lazy val a0condition: Parser[A0Cond] = a0condition0 |
    a0condition1 | a0condition2 | a0condition3 | a0condition4 |
    a0condition5 | a0condition6 | a0condition7 | a0condition8 |
    a0condition9 | a0condition10 | a0condition11 | a0condition12 |
    a0condition13 | a0condition14 | a0condition15 | a0condition16 |
    a0condition17 | a0condition18 | a0condition19 | a0condition20 |
    a0condition21 | a0condition22 | a0condition23

}

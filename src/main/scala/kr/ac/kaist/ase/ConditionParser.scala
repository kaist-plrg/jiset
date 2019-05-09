package kr.ac.kaist.ase

class ConditionParser extends ExpressionParser {

  def condition0: Parser[Condition0] = {
    a0condition ~ "or" ~ opt("if") ~ a0condition ^^ {
      case e0 ~ _ ~ _ ~ e1 => Condition0(e0, e1)
    }
  }
  def condition1: Parser[Condition1] = {
    a0condition ~ "and" ~ a0condition ^^ {
      case e0 ~ _ ~ e1 => Condition1(e0, e1)
    }
  }
  def condition2: Parser[Condition2] = {
    a0condition ^^ {
      case e0 => Condition2(e0)
    }
  }
  lazy val condition: Parser[Condition] = condition0 | condition1 | condition2
  def a0condition0: Parser[A0Condition0] = {
    id ~ "has" ~ "a" ~ "binding" ~ "for" ~ "the" ~ "name" ~ "that" ~ "is" ~ "the" ~ "value" ~ "of" ~ id ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ e1 => A0Condition0(Var(e0), Var(e1))
    }
  }
  def a0condition1: Parser[A0Condition1] = {
    id ~ "does" ~ "not" ~ ((opt("already") ~ "have" ~ "a" ~ "binding" ~ "for") | "contain") ~ id ^^ {
      case e0 ~ _ ~ _ ~ _ ~ e1 => A0Condition1(Var(e0), Var(e1))
    }
  }
  def a0condition2: Parser[A0Condition2] = {
    id ~ "must" ~ "have" ~ "an" ~ "uninitialized" ~ "binding" ~ "for" ~ id ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ e1 => A0Condition2(Var(e0), Var(e1))
    }
  }
  def a0condition3: Parser[A0Condition3] = {
    id ~ "is" ~ "the" ~ "source" ~ "code" ~ "of" ~ "a" ~ "module" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ => A0Condition3(Var(e0))
    }
  }
  def a0condition4: Parser[A0Condition4] = {
    expr ~ ("is" | "has") ~ "the" ~ "value" ~ value ^^ {
      case e0 ~ _ ~ _ ~ _ ~ e1 => A0Condition4(e0, Value(e1))
    }
  }
  def a0condition5: Parser[A0Condition5] = {
    expr ~ "is" ~ "not" ~ "present" ^^ {
      case e0 ~ _ ~ _ ~ _ => A0Condition5(e0)
    }
  }
  def a0condition6: Parser[A0Condition6] = {
    expr ~ "is" ~ "not" ~ expr ^^ {
      case e0 ~ _ ~ _ ~ e1 => A0Condition6(e0, e1)
    }
  }
  def a0condition7: Parser[A0Condition7] = {
    expr ~ "is" ~ typev ~ "or" ~ typev ^^ {
      case e0 ~ _ ~ e1 ~ _ ~ e2 => A0Condition7(e0, e1, e2)
    }
  }
  def a0condition8: Parser[A0Condition8] = {
    expr ~ "is" ~ typev ^^ {
      case e0 ~ _ ~ e1 => A0Condition8(e0, e1)
    }
  }
  def a0condition9: Parser[A0Condition9] = {
    expr ~ "is" ~ expr ^^ {
      case e0 ~ _ ~ e1 => A0Condition9(e0, e1)
    }
  }
  def a0condition10: Parser[A0Condition10] = {
    expr ~ "contains" ~ expr ^^ {
      case e0 ~ _ ~ e1 => A0Condition10(e0, e1)
    }
  }
  def a0condition11: Parser[A0Condition11] = {
    expr ~ "cannot" ~ "be" ~ "deleted" ^^ {
      case e0 ~ _ ~ _ ~ _ => A0Condition11(e0)
    }
  }
  def a0condition12: Parser[A0Condition12] = {
    binding ~ "is" ~ "a" ~ "strict" ~ "binding" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ => A0Condition12(e0)
    }
  }
  def a0condition13: Parser[A0Condition13] = {
    binding ~ "has" ~ "not" ~ "yet" ~ "been" ~ "initialized" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ ~ _ => A0Condition13(e0)
    }
  }
  def a0condition14: Parser[A0Condition14] = {
    binding ~ "is" ~ "a" ~ "mutable" ~ "binding" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ => A0Condition14(e0)
    }
  }
  def a0condition15: Parser[A0Condition15] = {
    binding ~ "is" ~ "an" ~ "indirect" ~ "binding" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ => A0Condition15(e0)
    }
  }
  def a0condition16: Parser[A0Condition16] = {
    binding ~ "is" ~ "an" ~ "uninitialized" ~ "binding" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ => A0Condition16(e0)
    }
  }
  def a0condition17: Parser[A0Condition17] = {
    "This" ~ "is" ~ "an" ~ "attempt" ~ "to" ~ "change" ~ "the" ~ "value" ~ "of" ~ "an" ~ "immutable" ~ "binding" ^^ {
      case _ => A0Condition17()
    }
  }
  def a0condition18: Parser[A0Condition18] = {
    id ~ "has" ~ "a" ~ "binding" ~ "for" ~ id ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ ~ e1 => A0Condition18(Var(e0), Var(e1))
    }
  }
  def a0condition19: Parser[A0Condition19] = {
    ("The" | "the") ~ "execution" ~ "context" ~ "stack" ~ "is" ~ opt("now") ~ "empty" ^^ {
      case _ => A0Condition19()
    }
  }
  def a0condition20: Parser[A0Condition20] = {
    "no" ~ "such" ~ "execution" ~ "context" ~ "exists" ^^ {
      case _ => A0Condition20()
    }
  }
  def a0condition21: Parser[A0Condition21] = {
    id ~ "is" ~ "an" ~ "abrupt" ~ "completion" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ => A0Condition21(Var(e0))
    }
  }
  def a0condition22: Parser[A0Condition22] = {
    id ~ "is" ~ "the" ~ "source" ~ "code" ~ "of" ~ "a" ~ "script" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ => A0Condition22(Var(e0))
    }
  }
  def a0condition23: Parser[A0Condition23] = {
    id ~ "has" ~ "a" ~ "[" ~ "[" ~ field ~ "]" ~ "]" ~ "field" ^^ {
      case e0 ~ _ ~ _ ~ _ ~ _ ~ e1 ~ _ ~ _ ~ _ => A0Condition23(Var(e0), e1)
    }
  }
  lazy val a0condition: Parser[A0Condition] = a0condition0 |
    a0condition1 | a0condition2 | a0condition3 | a0condition4 |
    a0condition5 | a0condition6 | a0condition7 | a0condition8 |
    a0condition9 | a0condition10 | a0condition11 | a0condition12 |
    a0condition13 | a0condition14 | a0condition15 | a0condition16 |
    a0condition17 | a0condition18 | a0condition19 | a0condition20 |
    a0condition21 | a0condition22 | a0condition23

}
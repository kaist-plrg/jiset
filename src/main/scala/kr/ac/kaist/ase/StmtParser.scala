package kr.ac.kaist.ase

class StmtParser extends ConditionParser {
  def start0: Parser[Stmt] = {
    stmt ~ "." ^^ {
      case e0 ~ _ => e0
    }
  }
  def start1: Parser[Stmt] = {
    stmt
  }
  lazy val start = start0 | start1

  def stmt0: Parser[Stmt0] = {
    "Let" ~ id ~ "be" ~ expr ^^ {
      case _ ~ e0 ~ _ ~ e1 => Stmt0(Var(e0), e1)
    }
  }
  def stmt1: Parser[Stmt1] = {
    "If" ~ condition ~ "," ~ stmt ~ ("." | ";") ~ ("Otherwise" | "otherwise") ~ opt(",") ~ stmt ^^ {
      case _ ~ e0 ~ _ ~ e1 ~ _ ~ _ ~ _ ~ e2 => Stmt1(e0, e1, e2)
    }
  }
  def stmt2: Parser[Stmt2] = {
    "If" ~ condition ~ "," ~ opt("then") ~ stmt ^^ {
      case _ ~ e0 ~ _ ~ _ ~ e1 => Stmt2(e0, e1)
    }
  }
  def stmt3: Parser[Stmt3] = {
    ("Otherwise" | "Else") ~ "," ~ stmt ^^ {
      case _ ~ _ ~ e0 => Stmt3(e0)
    }
  }
  def stmt4: Parser[Stmt4] = {
    "Else" ~ opt("if") ~ condition ~ "," ~ stmt ^^ {
      case _ ~ _ ~ e0 ~ _ ~ e1 => Stmt4(e0, e1)
    }
  }
  def stmt5: Parser[Stmt5] = {
    ("Return" | "return") ~ expr ^^ {
      case _ ~ e0 => Stmt5(e0)
    }
  }
  def stmt6: Parser[Stmt6] = {
    "Assert" ~ ":" ~ condition ^^ {
      case _ ~ _ ~ e0 => Stmt6(e0)
    }
  }
  def stmt7: Parser[Stmt7] = {
    "throw" ~ "a" ~ value ~ "exception" ^^ {
      case _ ~ _ ~ e0 ~ _ => Stmt7(Value(e0))
    }
  }
  def stmt8: Parser[Stmt8] = {
    ("Perform" | "perform") ~ expr ^^ {
      case _ ~ e0 => Stmt8(e0)
    }
  }
  def stmt9: Parser[Stmt9] = {
    ("Set" | "set") ~ settable ~ "to" ~ expr ^^ {
      case _ ~ e0 ~ _ ~ e1 => Stmt9(e0, e1)
    }
  }
  def stmt10: Parser[Stmt10] = {
    "Call" ~ expr ^^ {
      case _ ~ e0 => Stmt10(e0)
    }
  }
  def stmt11: Parser[Stmt11] = {
    "Repeat" ~ "," ~ stmt ^^ {
      case _ ~ _ ~ e0 => Stmt11(e0)
    }
  }
  def stmt12: Parser[Stmt12] = {
    "Append" ~ expr ~ "to" ~ id ^^ {
      case _ ~ e0 ~ _ ~ e1 => Stmt12(e0, Var(e1))
    }
  }
  def stmt13: Parser[Stmt13] = {
    "change" ~ "its" ~ "bound" ~ "value" ~ "to" ~ expr ^^ {
      case _ ~ _ ~ _ ~ _ ~ _ ~ e0 => Stmt13(e0)
    }
  }
  def stmt14: Parser[Stmt14] = {
    "line-list" ^^ { case _ => Stmt14() }
  }
  lazy val stmt: Parser[Stmt] = stmt0 | stmt1 | stmt2 | stmt3 |
    stmt4 | stmt5 | stmt6 | stmt7 |
    stmt8 | stmt9 | stmt10 | stmt11 |
    stmt12 | stmt13 | stmt14

}

object StartParser extends StmtParser {
  def apply(s: String): ParseResult[Stmt] = {
    parse(start, s)
  }
}
package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.parser.BasicParsers

// parser for type checker components
object Parser extends Parsers

// parser for type checker components
trait Parser[T] extends Parsers {
  def fromFile(str: String)(implicit parser: Parser[T]): T =
    fromFileWithParser(str, parser)
  def apply(str: String)(implicit parser: Parser[T]): T =
    parse[T](str)
}

// parsers for type checker components
trait Parsers extends BasicParsers {
  ////////////////////////////////////////////////////////////////////////////////
  // Type
  ////////////////////////////////////////////////////////////////////////////////
  // abstract types
  implicit lazy val aty: Parser[AbsType] = (
    "⊥" ^^^ AbsType() |
    "(" ~> rep1sep(ty, "|") <~ ")" ^^ { case s => AbsType(s.toSet) } |
    ty ^^ { case t => AbsType(t) }
  )

  def pairParser[L, R](lparser: Parser[L], rparser: Parser[R]): Parser[(L, R)] =
    lparser ~ ("->" ~> rparser) ^^ { case l ~ r => (l, r) }

  // types
  implicit lazy val ty: Parser[Type] = cty | pty
  lazy val cty: Parser[CompType] = (
    "Normal(" ~> pty <~ ")" ^^ { NormalT(_) } |
    "Abrupt" ^^^ AbruptT
  )
  lazy val pty: Parser[PureType] = (
    "{" ~> repsep(pairParser(word, aty), ",") <~ "}" ^^ {
      case ps => RecordT(ps.toMap)
    } |
    "☊(" ~> word <~ ")" ^^ { AstT(_) } |
    "~" ~> "[^~]+".r <~ "~" ^^ { ConstT(_) } |
    "λ[" ~> int <~ "]" ^^ { FuncT(_) } |
    "ESValue" ^^^ ESValueT |
    "prim" ^^^ PrimT |
    "arith" ^^^ ArithT |
    "numeric" ^^^ NumericT |
    "num" ^^^ NumT |
    "bigint" ^^^ BigIntT |
    "str" ^^^ StrT |
    "bool" ^^^ BoolT |
    "[]" ^^^ NilT |
    "[" ~> pty <~ "]" ^^ { ListT(_) } |
    "{ _ |-> " ~> pty <~ "}" ^^ { MapT(_) } |
    "symbol" ^^^ SymbolT |
    s"${integer}n".r ^^ { case s => ABigInt(BigInt(s.dropRight(1))) } |
    floatingPointNumber ^^ { case s => ANum(s.toDouble) } |
    "Infinity" ^^ { case s => ANum(Double.PositiveInfinity) } |
    "+Infinity" ^^ { case s => ANum(Double.PositiveInfinity) } |
    "-Infinity" ^^ { case s => ANum(Double.NegativeInfinity) } |
    "NaN" ^^ { case s => ANum(Double.NaN) } |
    string ^^ { AStr(_) } |
    "true" ^^^ ABool(true) |
    "false" ^^^ ABool(false) |
    "undefined" ^^^ AUndef |
    "null" ^^^ ANull |
    "?" ^^^ AAbsent |
    word ^^ { NameT(_) }
  )
}

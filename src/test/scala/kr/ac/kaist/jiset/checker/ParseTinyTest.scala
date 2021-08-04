package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset._

class ParseTinyTest extends CheckerTest with checker.Parsers {
  val name: String = "checkerParseTest"

  // test helper
  def test[T <: CheckerElem](desc: String)(cases: (String, T)*)(
    implicit
    parser: Parser[T]
  ): Unit = check(desc, cases.foreach {
    case (given, expected) =>
      val result = parse[T](given)
      if (result != expected) {
        println(s"$desc FAILED")
        println(s"result: $result")
        println(s"answer: $expected")
        assert(result == expected)
      }
  })

  // registration
  def init: Unit = {
    test("AbsType")(
      "⊥" -> AbsType(),
      "num" -> AbsType(NumT),
      "(bool | num)" -> AbsType(NumT, BoolT),
      "(arith | bool)" -> AbsType(ArithT, BoolT),
    )
    test[CompType]("CompType")(
      "Normal(num)" -> NormalT(NumT),
      "Abrupt" -> AbruptT,
    )(cty)
    test[PureType]("PureType")(
      "A" -> NameT("A"),
      "{}" -> RecordT(),
      "{ p -> (bool | num) }" -> RecordT(Map("p" -> AbsType(NumT, BoolT))),
      "☊(X)" -> AstT("X"),
      "~let~" -> ConstT("let"),
      "λ[42]" -> FuncT(42),
      "ESValue" -> ESValueT,
      "prim" -> PrimT,
      "arith" -> ArithT,
      "numeric" -> NumericT,
      "num" -> NumT,
      "bigint" -> BigIntT,
      "str" -> StrT,
      "bool" -> BoolT,
      "[]" -> NilT,
      "[num]" -> ListT(NumT),
      "{ _ |-> num }" -> MapT(NumT),
      "symbol" -> SymbolT,
      "42.8" -> ANum(42.8),
      "Infinity" -> ANum(Double.PositiveInfinity),
      "-Infinity" -> ANum(Double.NegativeInfinity),
      "NaN" -> ANum(Double.NaN),
      "29834029830482093849293n" -> ABigInt(BigInt("29834029830482093849293")),
      "\"str\"" -> AStr("str"),
      "\"\\\"\\\"\"" -> AStr("\"\""),
      "true" -> ABool(true),
      "false" -> ABool(false),
      "undefined" -> AUndef,
      "null" -> ANull,
      "?" -> AAbsent,
    )(pty)
  }
  init
}

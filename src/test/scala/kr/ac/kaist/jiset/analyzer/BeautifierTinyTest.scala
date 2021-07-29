package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.Beautifier._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.Appender._

class BeautifierTinyTest extends AnalyzerTest {
  val name: String = "analyzerBeautifierTest"

  // test helper
  def test[T <: AnalyzerComponent](desc: String)(cases: (T, String)*)(
    implicit
    tApp: App[T]
  ): Unit = check(desc, cases.foreach {
    case (given, expected) =>
      val result = given.toString
      if (result != expected) {
        println(s"$desc FAILED")
        println(s"result: $result")
        println(s"answer: $expected")
        assert(result == expected)
      }
  })

  // registration
  def init: Unit = {
    val nidGen = new UIdGen[Node]
    val fidGen = new UIdGen[Function]
    val entry = Entry(nidGen)
    val exit = Exit(nidGen)
    val func = Function(fidGen, null, entry, exit, Set(), Map(), Map(), false)
    val base = AbsType(NameT("A"))

    test("AbsRef")(
      AbsId("x") -> "x",
      AbsStrProp(base, "p") -> "A.p",
      AbsGeneralProp(base, AbsType("p", "q")) -> """A[("p" | "q")]""",
    )
    test("ControlPoint")(
      NodePoint(entry, View(NumT, BoolT)) -> "Entry[0]:[num, bool]",
      ReturnPoint(func, View(NumT, BoolT)) -> "RETURN:[num, bool]",
    )
    test("View")(
      View() -> "[]",
      View(NumT, BoolT) -> "[num, bool]",
    )
    test("AbsState")(
      AbsState.Bot -> "⊥",
      AbsState.Empty -> "{}",
      AbsState(reachable = true, Map(
        "x" -> AbsType(NumT, StrT),
        "y" -> AbsType(BoolT),
      )) -> """{
      |  x -> (num | str)
      |  y -> bool
      |}""".stripMargin,
    )
    test("AbsType")(
      AbsType() -> "⊥",
      AbsType(NumT) -> "num",
      AbsType(NumT, BoolT) -> "(bool | num)",
      AbsType(NumT, BigIntT, StrT, BoolT) -> "(arith | bool)",
    )
    test("CompType")(
      NormalT(NumT) -> "Normal(num)",
      AbruptT -> "Abrupt",
    )
    test("PureType")(
      NameT("A") -> "A",
      RecordT() -> "{}",
      RecordT(Map("p" -> AbsType(NumT, BoolT))) -> "{ p -> (bool | num) }",
      AstT("X") -> "☊(X)",
      ConstT("let") -> "~let~",
      FuncT(42) -> "λ[42]",
      ESValueT -> "ESValue",
      PrimT -> "prim",
      ArithT -> "arith",
      NumericT -> "numeric",
      NumT -> "num",
      BigIntT -> "bigint",
      StrT -> "str",
      BoolT -> "bool",
      NilT -> "[]",
      ListT(NumT) -> "[num]",
      MapT(NumT) -> "{ _ |-> num }",
      SymbolT -> "symbol",
      ANum(42.8) -> "42.8",
      ANum(Double.PositiveInfinity) -> "Infinity",
      ANum(Double.NegativeInfinity) -> "-Infinity",
      ANum(Double.NaN) -> "NaN",
      ABigInt(BigInt("29834029830482093849293")) -> "29834029830482093849293n",
      AStr("str") -> "\"str\"",
      AStr("\"\"") -> "\"\\\"\\\"\"",
      ABool(true) -> "true",
      ABool(false) -> "false",
      AUndef -> "undefined",
      ANull -> "null",
      AAbsent -> "?",
    )
  }
  init
}

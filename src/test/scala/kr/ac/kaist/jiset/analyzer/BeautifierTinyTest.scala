package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.util.Appender._

class BeautifierTinyTest extends AnalyzerTest {
  def test[T](desc: String)(cases: (T, String)*)(
    implicit
    tApp: App[T]
  ): Unit = check(desc, cases.foreach {
    case (given, expected) =>
      val result = beautify(given)
      if (result != expected) {
        println(s"FAILED: $result != $expected")
        assert(result == expected)
      }
  })

  // registration
  def init: Unit = {
    test("Primitive Values")(
      Num(42.34) -> "42.34",
      INum(23) -> "23i",
      BigINum(BigInt(2).pow(100)) -> "1267650600228229401496703205376n",
      Str("hello") -> "\"hello\"",
      Bool(true) -> s"true",
      Bool(false) -> s"false",
      Undef -> "undefined",
      Null -> "null",
      Absent -> "absent",
    )

    test("AST values")(ASTVal("Literal") -> "☊(Literal)")

    test("Addresses")(
      NamedAddr("Global") -> "#Global",
      DynamicAddr(432) -> "#432",
    )

    test("Abstract Values")(
      AbsValue(42.34, BigInt(24), true) -> "42.34 | 24n | true",
      AbsValue(123, "abc", Undef, Null, Absent) -> "123i | \"abc\" | undef | null | ?",
      AbsValue(1.2, 2.3, 3, 4, BigInt(2), BigInt(3)) -> "num | int | bigint",
      AbsValue("a", "b", true, false) -> "str | bool",
      AbsValue(42, NamedAddr("Global"), DynamicAddr(432)) -> "(#Global | #432) | 42i",
      (AbsValue(true, Cont()) ⊔ AbsClo.Top) -> "λ | κ | true",
      AbsValue(ASTVal("Literal"), ASTVal("Identifier")) -> "(☊(Literal) | ☊(Identifier))",
    )

    val id = RefValueId("x")
    val prop = RefValueProp(DynamicAddr(42), "p")
    val string = RefValueString("abc", "length")
    test("Abstract Reference Values")(
      AbsRefValue(id) -> "x",
      AbsRefValue(prop) -> "#42.p",
      AbsRefValue(string) -> """"abc".length""",
      AbsRefValue(id, prop, string) -> """x | #42.p | "abc".length""",
    )

    test("Abstract Objects")(
      AbsObj(SymbolObj("has"), SymbolObj("get")) -> "@(has | get)",
      AbsObj(MapObj("x" -> true, "y" -> 2), MapObj("x" -> "a", "z" -> Null)) -> """{
      |  x -> ! "a" | true
      |  y -> ? 2i
      |  z -> ? null
      |}""".stripMargin,
      AbsObj(ListObj(Undef, true, 42)) -> "[undef, true, 42i]",
      AbsObj(SymbolObj("has"), MapObj(), ListObj()) -> "@has | {} | []",
    )

    val heap = AbsHeap(Heap(
      NamedAddr("Global") -> SymbolObj("has"),
      DynamicAddr(42) -> MapObj(),
    ))
    test("Abstract Heaps")(
      heap -> """{
      |  #Global -> @has
      |  #42 -> {}
      |}""".stripMargin,
    )

    val env = AbsEnv(Env(
      "x" -> 42,
      "y" -> true,
    ), Env(
      "x" -> 42,
      "z" -> Null,
    ))
    test("Abstract Environments")(
      env -> """{
      |  x -> ! 42i
      |  y -> ? true
      |  z -> ? null
      |}""".stripMargin,
    )

    val st = AbsState.Elem(env, heap)
    test("Abstract State")(
      st -> """{
      |  env: {
      |    x -> ! 42i
      |    y -> ? true
      |    z -> ? null
      |  }
      |  heap: {
      |    #Global -> @has
      |    #42 -> {}
      |  }
      |}""".stripMargin,
      AbsState.Empty.copy(env = env) -> """{
      |  env: {
      |    x -> ! 42i
      |    y -> ? true
      |    z -> ? null
      |  }
      |}""".stripMargin,
    )
  }
  init
}

package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

class BeautifierTinyTest extends AnalyzerTest {
  val name: String = "analyzerBeautifierTest"

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
    import ir._

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
      AbsValue(42, NamedAddr("Global"), DynamicAddr(432)) -> "(#432 | #Global) | 42i",
      (AbsValue(42, Const("empty")) ⊔ AbsValue(Ty("Object"))) -> "Object | ~empty~ | 42i",
      (AbsValue(true, Cont()) ⊔ AbsClo(Clo(42))) -> "λ(42) | κ | true",
      AbsValue(Clo(42, Env("x" -> Bool(true), "y" -> Num(42)))) -> """λ(42)[{
      |  x -> ! true
      |  y -> ! 42.0
      |}]""".stripMargin,
      AbsValue(ASTVal("Literal"), ASTVal("Identifier")) -> "(☊(Identifier) | ☊(Literal))",
      AbsValue(Const("invalid"), Const("empty")) -> "(~empty~ | ~invalid~)",
      AbsValue(Completion(CompNormal, 42, Const("empty"))) -> "N(42i)",
      AbsValue(
        Completion(CompNormal, 42, Const("empty")),
        Completion(CompNormal, true, Const("empty")),
      ) -> "N(42i | true)",
      AbsValue(
        Completion(CompThrow, 42, Const("empty")),
        Completion(CompNormal, true, Const("empty")),
      ) -> "N(true) | T(42i)",
    )

    val id = RefValueId("x")
    val prop = RefValueProp(DynamicAddr(42), "p")
    val string = RefValueString("abc", "length")
    test("Abstract Reference Values")(
      AbsRefValue.Bot -> "⊥",
      AbsRefValue(id) -> "x",
      AbsRefValue.Prop(AbsTy("Object"), AbsStr("p")) -> """(Object)["p"]""",
      AbsRefValue.Prop(
        AbsValue(DynamicAddr(42)) ⊔ AbsTy("Object"),
        AbsStr("p")
      ) -> """(#42 | Object)["p"]""",
      AbsRefValue(prop) -> """(#42)["p"]""",
      AbsRefValue(string) -> """("abc")["length"]""",
      AbsRefValue(id, prop, string) -> "⊤",
    )

    test("Abstract Objects")(
      AbsObj(SymbolObj("has")) -> """@"has"""",
      AbsObj(
        MapObj(Ty("Object"), "x" -> true, "y" -> 2),
        MapObj(Ty("Object"), "x" -> "a", "z" -> Null),
      ) -> """Object {
      |  x -> ! "a" | true
      |  y -> ? 2i
      |  z -> ? null
      |}""".stripMargin,
      AbsObj(ListObj()) -> "[]",
      AbsObj(ListObj(Undef, true, 42)) -> "[42i | true | undef]",
      AbsObj(ListObj(0, 1, 2, 3)) -> "[int]",
      AbsObj.Top -> "⊤",
    )

    val heap = AbsHeap(Heap(
      NamedAddr("Global") -> SymbolObj("has"),
      DynamicAddr(42) -> MapObj(Ty("Record")),
    ))
    test("Abstract Heaps")(
      heap -> """{
      |  #42 -> Record {}
      |  #Global -> @"has"
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
      |    #42 -> Record {}
      |    #Global -> @"has"
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

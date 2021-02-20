package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.analyzer.domain.JsonProtocol._
import spray.json._

class JsonSmallTest extends AnalyzerTest {
  def test[T](desc: String)(cases: T*)(
    implicit
    tApp: App[T],
    tFormat: JsonFormat[T]
  ): Unit = check(desc, cases.foreach(given => {
    val json = given.toJson
    val result = json.convertTo[T]
    if (given != result) {
      println(s"FAILED: ${beautify(given)} != ${beautify(result)}")
      assert(given == result)
    }
  }))

  // registration
  def init: Unit = {
    test("Abstract Values")(
      AbsValue(42.34, BigInt(24), true),
      AbsValue(123, "abc", Undef, Null, Absent),
      AbsValue(1.2, 2.3, 3, 4, BigInt(2), BigInt(3)),
      AbsValue("a", "b", true, false),
      AbsValue(42, NamedAddr("Global"), DynamicAddr(432)),
      (AbsValue(true, Cont()) ⊔ AbsClo.Top),
      AbsValue(ASTVal("Literal"), ASTVal("Identifier")),
    )

    test("Abstract Objects")(
      AbsObj(SymbolObj("has"), SymbolObj("get")),
      AbsObj(MapObj("x" -> true, "y" -> 2), MapObj("x" -> "a", "z" -> Null)),
      AbsObj(ListObj(Undef, true, 42)),
      AbsObj(SymbolObj("has"), MapObj(), ListObj()),
    )

    val heap = AbsHeap(Heap(
      NamedAddr("Global") -> SymbolObj("has"),
      DynamicAddr(42) -> MapObj(),
    ))
    test("Abstract Heaps")(heap)

    val env = AbsEnv(Env(
      "x" -> 42,
      "y" -> true,
    ), Env(
      "x" -> 42,
      "z" -> Null,
    ))
    test("Abstract Environments")(env)

    val ctxt = AbsCtxt.Elem(
      globals = env,
      locals = env,
      retVal = AbsValue(Null, true)
    )
    test("Abstract Contexts")(ctxt)

    val st = AbsState.Elem(ctxt, heap)
    test("Abstract State")(st)
  }
  init
}

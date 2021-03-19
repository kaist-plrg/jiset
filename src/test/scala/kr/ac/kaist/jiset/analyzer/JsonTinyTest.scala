package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.analyzer.domain.JsonProtocol._
import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._
import spray.json._

class JsonTinyTest extends AnalyzerTest {
  val name: String = "analyzerJsonTest"

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
    import ir._

    test("Abstract Values")(
      AbsValue(42.34, BigInt(24), true),
      AbsValue(123, "abc", Undef, Null, Absent),
      AbsValue(1.2, 2.3, 3, 4, BigInt(2), BigInt(3)),
      AbsValue("a", "b", true, false),
      AbsValue(42, NamedAddr("Global"), AllocSite(432, 42)),
      (AbsValue(42, Const("empty")) ⊔ AbsValue(Ty("Object"))),
      (AbsValue(true, Cont()) ⊔ AbsClo.Top),
      AbsValue(ASTVal("Literal"), ASTVal("Identifier")),
      AbsValue(Const("invalid"), Const("empty")),
      AbsValue(Completion(CompNormal, 42, Const("empty"))),
      AbsValue(
        Completion(CompNormal, 42, Const("empty")),
        Completion(CompNormal, true, Const("empty")),
      ),
      AbsValue(
        Completion(CompThrow, 42, Const("empty")),
        Completion(CompNormal, true, Const("empty")),
      ),
    )

    val id = RefValueId("x")
    val prop = RefValueProp(NamedAddr("Global"), "p")
    val string = RefValueString("abc", "length")
    test("Abstract Reference Values")(
      AbsRefValue.Bot,
      AbsRefValue(id),
      AbsRefValue.Prop(AbsTy("Object"), AbsStr("p")),
      AbsRefValue.Prop(
        AbsValue(AllocSite(1, 2)) ⊔ AbsTy("Object"),
        AbsStr("p")
      ),
      AbsRefValue(prop),
      AbsRefValue(string),
      AbsRefValue(id, prop, string),
    )

    test("Abstract Objects")(
      AbsObj(SymbolObj("has")),
      AbsObj(
        MapObj(Ty("Object"), "x" -> true, "y" -> 2),
        MapObj(Ty("Object"), "x" -> "a", "z" -> Null)
      ),
      AbsObj(ListObj()),
      AbsObj(ListObj(Undef, true, 42)),
      AbsObj(ListObj(0, 1, 2, 3)),
      AbsObj.Top,
    )

    val heap = AbsHeap(Heap(
      NamedAddr("Global") -> SymbolObj("has"),
      NamedAddr("A") -> MapObj(Ty("Record")),
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

    val st = AbsState.Elem(env, heap)
    test("Abstract State")(st)
  }
  init
}

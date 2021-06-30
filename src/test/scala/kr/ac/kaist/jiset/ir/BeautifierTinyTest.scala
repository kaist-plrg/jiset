package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.ir.Beautifier._
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.{ Span, Pos }
import scala.collection.mutable.{ Map => MMap }

class BeautifierTinyTest extends IRTest {
  val name: String = "irBeautifierTest"

  import BeautifierTinyTest.beautifier._
  def test[T <: IRNode](desc: String)(cases: (T, String)*)(
    implicit
    tApp: App[T]
  ): Unit = check(desc, cases.foreach {
    case (given, expected) =>
      val result = given.beautified
      if (result != expected) {
        println(s"$desc FAILED")
        println(s"result: $result")
        println(s"answer: $expected")
        assert(result == expected)
      }
  })
  def init: Unit = {
    val IRMapElems = List(
      EBool(true) -> EStr("true"),
      ENull -> EStr("null")
    )
    val SMapElems = "(true -> \"true\", null -> \"null\")"
    val IRList_ = List(ENull, EAbsent)
    val SList = "(new [null, absent])"
    val IRReturn = IReturn(EINum(4))
    val SReturn = "return 4i"
    val IdList = List(Id("x"), Id("y"))
    val SIdList = "(x, y)"

    // Syntax
    test("Inst")(
      IExpr(EINum(4)) -> "4i",
      ILet(Id("x"), EINum(4)) -> "let x = 4i",
      IAssign(RefId(Id("x")), ENum(3.0)) -> "x = 3.0",
      IDelete(RefId(Id("ref"))) -> "delete ref",
      IAppend(EUndef, EList(IRList_)) -> s"append undefined -> $SList",
      IPrepend(EUndef, EList(IRList_)) -> s"prepend undefined -> $SList",
      IRReturn -> SReturn,
      IThrow("SyntaxError") -> "throw SyntaxError",
      IIf(EBool(true), IRReturn, IExpr(ENum(3.0))) ->
        s"if true $SReturn else 3.0",
      IWhile(EBool(false), IRReturn) -> s"while false $SReturn",
      ISeq(List()) -> "{}",
      ISeq(List(IRReturn, IExpr(ENull))) -> s"{\n  $SReturn\n  null\n}",
      IAssert(EBool(false)) -> "assert false",
      IPrint(EBool(false)) -> "print false",
      IApp(Id("x"), EStr("f"), IRList_) ->
        "app x = (\"f\" null absent)",
      IAccess(Id("x"), EStr("b"), ENum(3.0), Nil) ->
        "access x = (\"b\" 3.0)",
      IAccess(Id("x"), EStr("b"), ENum(3.0), List(EStr("x"), ENull)) ->
        "access x = (\"b\" 3.0 \"x\" null)",
      IWithCont(Id("x"), IdList, IRReturn) ->
        s"withcont x $SIdList = $SReturn",
    )
    test("Expr")(
      ENum(3.0) -> "3.0",
      EINum(4) -> "4i",
      EBigINum(1024) -> "1024n",
      EStr("hi") -> "\"hi\"",
      EBool(true) -> "true",
      EUndef -> "undefined",
      ENull -> "null",
      EAbsent -> "absent",
      EMap(Ty("T"), IRMapElems) -> s"(new T$SMapElems)",
      EList(IRList_) -> SList,
      EPop(EList(IRList_), EINum(0)) -> s"(pop $SList 0i)",
      ERef(RefId(Id("x"))) -> "x",
      ECont(IdList, IExpr(EINum(4))) ->
        s"$SIdList [=>] 4i",
      EUOp(ONeg, EINum(4)) -> "(- 4i)",
      EBOp(ODiv, ENum(3.0), ENum(7.0)) -> "(/ 3.0 7.0)",
      ETypeOf(EBool(false)) -> "(typeof false)",
      EIsCompletion(EINum(5)) -> "(is-completion 5i)",
      EIsInstanceOf(EBool(false), "instanceof") ->
        "(is-instance-of false instanceof)",
      EGetElems(EBool(false), "getelems") ->
        "(get-elems false getelems)",
      EGetSyntax(EAbsent) -> "(get-syntax absent)",
      EParseSyntax(EStr("code"), EStr("rule"), EStr("flag"))
        -> "(parse-syntax \"code\" \"rule\" \"flag\")",
      EConvert(EStr("4"), CStrToNum, IRList_) ->
        "(convert \"4\" str2num null absent)",
      EContains(EList(IRList_), ENull) -> s"(contains $SList null)",
      EReturnIfAbrupt(ENum(3.0), true) -> "[? 3.0]",
      EReturnIfAbrupt(ENum(3.0), false) -> "[! 3.0]",
      ECopy(EStr("obj")) -> "(copy-obj \"obj\")",
      EKeys(EStr("obj"), false) -> "(map-keys \"obj\")",
      EKeys(EStr("obj"), true) -> "(map-keys \"obj\" [int-sorted])",
      ENotSupported("hi") -> "??? \"hi\""
    )
    test("Ref")(
      RefId(Id("y")) -> "y",
      RefProp(RefId(Id("z")), EStr("w")) -> "z.w",
      RefProp(RefId(Id("x")), ENum(3.0)) -> "x[3.0]"
    )
    test("Ty")(Ty("T") -> "T")
    test("Id")(Id("x") -> "x")
    test("UOp")(
      ONeg -> "-",
      ONot -> "!",
      OBNot -> "~"
    )
    test("BOp")(
      OPlus -> "+",
      OSub -> "-",
      OMul -> "*",
      OPow -> "**",
      ODiv -> "/",
      OUMod -> "%%",
      OMod -> "%",
      OEq -> "=",
      OEqual -> "==",
      OAnd -> "&&",
      OOr -> "||",
      OXor -> "^^",
      OBAnd -> "&",
      OBOr -> "|",
      OBXOr -> "^",
      OLShift -> "<<",
      OLt -> "<",
      OURShift -> ">>>",
      OSRShift -> ">>"
    )
    test("COp")(
      CStrToNum -> "str2num",
      CStrToBigInt -> "str2bigint",
      CNumToStr -> "num2str",
      CNumToInt -> "num2int",
      CNumToBigInt -> "num2bigint",
      CBigIntToNum -> "bigint2num"
    )

    // State
    test("State")(
      State() -> """context: {
      |  name: TOP_LEVEL
      |  return: RETURN
      |  insts: {}
      |  local-vars: {}}
      |context-stack: []
      |globals: {}
      |heap: (SIZE = 0): {}""".stripMargin
    )
    test("Context")(
      Context() -> """{
        |  name: TOP_LEVEL
        |  return: RETURN
        |  insts: {}
        |  local-vars: {}}""".stripMargin
    )
    test("Heap")(
      Heap(MMap(NamedAddr("namedaddr") -> IRSymbol(Num(3.0))), 1) ->
        """(SIZE = 1): {
        |  #namedaddr -> (Symbol 3.0)
        |}""".stripMargin
    )
    test("Obj")(
      IRSymbol(Num(3.0)) -> "(Symbol 3.0)",
      IRMap(Ty("T"), MMap(Num(3.0) -> (Num(2.0), 4)), 1) ->
        """(TYPE = T) {
        |  3.0 -> 2.0
        |}""".stripMargin,
      IRList(Vector(Num(3.0))) -> "[3.0]",
      IRNotSupported("tyname", "desc") -> "(NotSupported \"tyname\" \"desc\")"
    )
    test("Value")(
      Num(3.0) -> "3.0",
      INum(2) -> "2i",
      BigINum(2) -> "2n",
      Str("hello") -> "\"hello\"",
      Bool(true) -> "true",
      Undef -> "undefined",
      Null -> "null",
      Absent -> "absent"
    )
    test("Addr")(
      NamedAddr("namedaddr") -> "#namedaddr",
      DynamicAddr(3) -> "#3"
    )
    test("ASTVal")(
      ASTVal(PrimaryExpression0(List(), Span())) -> "☊[PrimaryExpression](this)"
    )
    test("ASTMethod")(
      ASTMethod(Func(Algo(NormalHead("name", List()), "x", IExpr(EINum(4)), List())), MMap(Id("y") -> INum(3))) ->
        """ASTMethod(λ(name), {
        |  y -> 3i
        |}""".stripMargin
    )
    test("Func")(
      Func(Algo(NormalHead("normalname", List()), "x", IExpr(EINum(4)), List())) ->
        "λ(normalname)",
      Func(Algo(MethodHead("base", "methodname", Param("p"), List()), "x", IExpr(EINum(4)), List())) ->
        "λ(base.methodname)",
      Func(Algo(SyntaxDirectedHead("lhsname", 0, 1, Rhs(List(), None), "methodname", List()), "x", IExpr(EINum(4)), List())) ->
        "λ(lhsname[0,1].methodname)",
      Func(Algo(BuiltinHead(RefId(Id("id")), List()), "x", IExpr(EINum(4)), List())) ->
        "λ(GLOBAL.id)",
      Func(Algo(BuiltinHead(RefProp(RefId(Id("id")), ENum(3.0)), List()), "x", IExpr(EINum(4)), List())) ->
        "λ(GLOBAL.id[3.0])"
    )
    test("Clo")(
      Clo("clo", IdList, MMap[Id, Value](Id("z") -> Num(3.0)), IExpr(EINum(4))) ->
        "clo:closure(x, y)[z -> 3.0] => 4i"
    )
    test("Cont")(
      Cont(IdList, IExpr(EINum(4)), Context(), List()) -> "TOP_LEVEL(x, y) [=>] 4i",
    )
    test("RefValue")(
      RefValueId(Id("x")) -> "x",
      RefValueProp(NamedAddr("namedaddr"), Num(3.0)) -> "#namedaddr[3.0]",
      RefValueString("hello", Num(3.0)) -> "\"hello\"[3.0]"
    )
  }
  init
}

object BeautifierTinyTest {
  val beautifier = new Beautifier()
}

package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.ir.Beautifier._

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
    val IRList = List(ENull, EAbsent)
    val SList = "(new [null, absent])"
    val IRReturn = IReturn(EINum(4))
    val SReturn = "return 4i"
    val IdList = List(Id("x"), Id("y"))
    val SIdList = "(x, y)"
    test("Inst")(
      IExpr(EINum(4)) -> "4i",
      ILet(Id("x"), EINum(4)) -> "let x = 4i",
      IAssign(RefId(Id("x")), ENum(3.0)) -> "x = 3.0",
      IDelete(RefId(Id("ref"))) -> "delete ref",
      IAppend(EUndef, EList(IRList)) -> s"append undefined -> $SList",
      IPrepend(EUndef, EList(IRList)) -> s"prepend undefined -> $SList",
      IRReturn -> SReturn,
      IThrow("SyntaxError") -> "throw SyntaxError",
      IIf(EBool(true), IRReturn, IExpr(ENum(3.0))) ->
        s"if true $SReturn else 3.0",
      IWhile(EBool(false), IRReturn) -> s"while false $SReturn",
      ISeq(List()) -> "{}",
      ISeq(List(IRReturn, IExpr(ENull))) -> s"{\n  $SReturn\n  null\n}",
      IAssert(EBool(false)) -> "assert false",
      IPrint(EBool(false)) -> "print false",
      IApp(Id("x"), EStr("f"), IRList) ->
        "app x = (\"f\" null absent)",
      IAccess(Id("x"), EStr("b"), ENum(3.0), Nil) ->
        "access x = (\"b\" 3.0)",
      IAccess(Id("x"), EStr("b"), ENum(3.0), List(EStr("x"), ENull)) ->
        "access x = (\"b\" 3.0 \"x\" null)",
      IWithCont(Id("x"), IdList, IRReturn) ->
        s"withcont x $SIdList = $SReturn",
      ISetType(EINum(4), Ty("T")) -> "set-type 4i T"
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
      EList(IRList) -> SList,
      EPop(EList(IRList), EINum(0)) -> s"(pop $SList 0i)",
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
      EConvert(EStr("4"), CStrToNum, IRList) ->
        "(convert \"4\" str2num null absent)",
      EContains(EList(IRList), ENull) -> s"(contains $SList null)",
      EReturnIfAbrupt(ENum(3.0), true) -> "[? 3.0]",
      EReturnIfAbrupt(ENum(3.0), false) -> "[! 3.0]",
      ECopy(EStr("obj")) -> "(copy-obj \"obj\")",
      EKeys(EStr("obj")) -> "(map-keys \"obj\")",
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
  }
  init
}

object BeautifierTinyTest {
  val beautifier = new Beautifier()
}

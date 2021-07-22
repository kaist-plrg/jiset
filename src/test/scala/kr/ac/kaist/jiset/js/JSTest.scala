package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.phase._
import kr.ac.kaist.jiset.js.{ Parser => JSParser }
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.util.JvmUseful._
import io.circe._, io.circe.syntax._, io.circe.parser.{ parse => parseJson }

trait JSTest extends IRTest {
  override def category: String = "js"

  // parse JS codes
  def parse(str: String): Script =
    JSParser.parse(JSParser.Script(Nil), str).get
  def parseFile(filename: String): Script =
    JSParser.parse(JSParser.Script(Nil), fileReader(filename)).get
  def esparseFile(filename: String): Json =
    parseJson(executeCmd(s"bin/esparse $filename")) match {
      case Left(jsonFail) => fail(jsonFail)
      case Right(json) => json
    }

  // eval JS codes
  def eval(script: Script): State = Interp(Load(script))
  def eval(str: String): State = eval(parse(str))
  def evalFile(filename: String): State =
    Interp(Load(parseFile(filename), filename))

  // tests for JS parser
  def parseTest(ast: AST): Unit = {
    val newAST = parse(ast.toString)
    assert(ast == newAST)
  }

  // tests for esparse
  def esparseTest(filename: String): Unit = {
    val answer = parseFile(filename)
    val json = esparseFile(filename)

    // check compressed form equality
    val comp0 = AST(answer.toJson).get
    val comp1 = AST(json).get
    assert(comp0.equals(comp1))

    // check AST equality
    val esAST = Script(comp1)
    assert(answer.toString == esAST.toString)
  }

  // tests for JS interpreter
  def evalTest(st: State): State = st(Id(RESULT)) match {
    case addr: Addr => st(addr, Str("Type")) match {
      case (addr: Addr) =>
        assert(addr == st(Id("CONST_normal")))
        st
      case v => fail(s"invalid completion type: ${v.beautified}")
    }
    case Absent => fail("no return value")
    case v => fail(s"return not an address: ${v.beautified}")
  }
  def evalTest(script: Script): Unit = evalTest(eval(script))
  def evalTest(str: String): Unit = evalTest(eval(str))
  def evalTestFile(filename: String): Unit = evalTest(evalFile(filename))

  // conversion extension from .js to .ir
  val js2ir = changeExt("js", "ir")
}

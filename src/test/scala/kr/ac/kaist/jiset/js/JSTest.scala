package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.phase._
import kr.ac.kaist.jiset.js.{ Parser => JSParser }
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.spec.NativeHelper._
import kr.ac.kaist.jiset.checker.NativeHelper._
import kr.ac.kaist.jiset.analyzer.{ Initialize => AInintialize, _ }
import kr.ac.kaist.jiset.analyzer.domain._
import scala.sys.process._
import io.circe._, io.circe.syntax._, io.circe.parser.{ parse => parseJson }

trait JSTest extends IRTest {
  override def category: String = "js"

  // cursor generator
  val cursorGen: CursorGen[_ <: Cursor] = NodeCursor

  // parse JS codes
  def parse(str: String): Script =
    JSParser.parse(JSParser.Script(Nil), str).get
  def parseFile(filename: String): Script =
    JSParser.parse(JSParser.Script(Nil), fileReader(filename)).get
  def esparse(str: String): Script = {
    var json = parseJson(executeCmd(s"""bin/esparse -s "$str"""")) match {
      case Left(jsonFail) => fail(jsonFail)
      case Right(json) => json
    }
    Script(AST(json).get)
  }
  def esparseFile(filename: String): Json =
    parseJson(executeCmd(s"bin/esparse $filename")) match {
      case Left(jsonFail) => fail(jsonFail)
      case Right(json) => json
    }

  // load initial codes
  def load(script: Script, fnameOpt: Option[String] = None): State = {
    setSpec(loadSpec(s"$VERSION_DIR/generated"))
    if (PARTIAL) setPartialModel(loadPartialModel(s"$VERSION_DIR/partial.json"))
    Initialize(script, fnameOpt, cursorGen)
  }

  // eval JS codes
  def eval(str: String): State = eval(parse(str), None)
  def evalFile(filename: String): State =
    eval(parseFile(filename), Some(filename))
  def eval(script: Script, fnameOpt: Option[String]): State =
    Interp(load(script, fnameOpt))

  // analyze JS codes
  def analyze(str: String): AbsSemantics = analyze(parse(str))
  def analyzeFile(filename: String): AbsSemantics = analyze(parseFile(filename))
  def analyze(script: Script): AbsSemantics = {
    // intitialize spec
    JISETTest.spec
    // fixpoint calculation
    val absSem = AbsSemantics(script)
    absSem.fixpoint
    absSem
  }

  // tests for JS parser
  def parseTest(ast: AST): Unit = {
    val newAST = parse(ast.toString)
    assert(ast == newAST)
  }

  // tests for esparse
  def esparseTest(jsName: String, filename: String): Unit = {
    val answer = parseFile(jsName)
    val toJsonName = changeExt("js", "json")
    try {
      val json = esparseFile(jsName)

      // check compressed form equality
      val comp0 = AST(answer.toJson).get
      val comp1 = AST(json).get
      assert(comp0.equals(comp1))

      // check AST equality
      val ast = Script(comp1)
      ASTDiff.diff(answer, ast)
    } catch {
      // save answer to tests/ast for debugging
      case e: Throwable =>
        val jsonName = toJsonName(filename)
        dumpFile(answer.toString, s"$ESPARSE_DIR/$filename")
        dumpFile(answer.toJson.noSpaces, s"$ESPARSE_DIR/$jsonName")
        fail(s"esparse failed: $jsName")
    }
  }

  // tests for JS interpreter
  def evalTest(st: State): State = {
    val resId = Id(RESULT)
    if (st.exists(resId)) st(resId) match {
      case comp: CompValue => assert(comp.ty == CONST_NORMAL)
      case v => fail(s"return not a completion: $v")
    }
    st
  }
  def evalTest(str: String): State = evalTest(eval(str))
  def evalTestFile(filename: String): State = evalTest(evalFile(filename))
  def evalTest(script: Script, filename: String): State =
    evalTest(eval(script, Some(filename)))

  // tests for JS analyzer
  def analyzeTestFile(filename: String): Unit = {
    // get analyze result
    val absSem = analyzeFile(filename)
    // get final state of concrete interp
    val st = evalFile(filename)
    // get abs state of ReturnPoint of RunJobs
    val runJobs = absSem.cfg.funcMap("RunJobs")
    val runJobsRp = ReturnPoint(runJobs, View())
    val retId = Id(RESULT)
    val absSt =
      if (!absSem.rpMap.contains(runJobsRp)) AbsState.Empty
      else {
        val absRet = absSem(runJobsRp)
        if (!st.exists(retId)) absRet.state
        else absRet.state.defineLocal((retId, absRet.value))
      }
    // check soundness
    assert(CheckWithInterp.check(runJobsRp, absSt, st))
  }

  // conversion extension from .js to .ir
  val js2ir = changeExt("js", "ir")
}

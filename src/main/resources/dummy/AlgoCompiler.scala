package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.parser.TokenParsers
import kr.ac.kaist.ase.algorithm.Algorithm

object AlgoCompiler extends TokenParsers {
  def apply(algo: Algorithm): Func = Func(
    params = algo.params.map(Id(_)),
    body = ISeq(parseAll(rep(stmt), (algo.toTokenList)).get)
  )

  def etodo(msg: String): ENotYetImpl = ENotYetImpl(msg)
  def itodo(msg: String): IExpr = IExpr(etodo(msg))

  def appendStmt = ("append" ~> expr <~ "to") ~ (id <~ ("."?) <~ next) ^^ { case e1 ~ id => itodo("appendExpr") }
  def assertStmt = "assert :" <~ step ^^ { case _ => itodo("assertStmt") }
  def callStmt = "call" ~> expr <~ ("."?) <~ next ^^ { case _ => itodo("callStmt") }
  def ifthenElseStmt = ("if" ~> cond <~ (","?)) ~ (stmt <~ (("." | ";" | ",")?) <~ ("otherwise" | "else") <~ (","?)) ~ stmt ^^ { case c ~ s1 ~ s2 => itodo("Ite") }
  def ifStmt = ("if" ~> cond <~ (","?) <~ ("then"?)) ~ stmt ^^ { case c ~ s => itodo("ifStmt") }
  def letStmt = ("let" ~> id <~ "be") ~ (expr <~ ("."?) <~ next) ^^ { case e1 ~ e2 => itodo("letStmt") }
  def performStmt = "perform" ~> expr <~ ("."?) <~ next ^^ { case e1 => itodo("performStmt") }
  def repeatStmt = "repeat ," ~> in ~> rep(stmt) <~ out <~ next ^^ { case s => ISeq(s) }
  def returnStmt = "return" ~> expr <~ ("."?) <~ next ^^ { case e1 => itodo("returnStmt") }
  def setObjStmt = "set" ~> id <~ "' s essential internal methods to the default ordinary object definitions specified in 9 . 1 ." <~ next ^^ { case i1 => itodo("setObj") }
  def setStmt = ("set" ~> lhs) ~ ("to" <~ expr <~ ("."?) <~ next) ^^ { case e1 ~ e2 => itodo("setStmt") }
  def innerStmt = in ~> rep(stmt) <~ out <~ next ^^ { case slist => itodo("innerStmt") }
  def throwStmt = "throw a" ~> value <~ "exception" <~ ("."?) <~ next ^^ { case _ => itodo("throwStmt") }
  def pushStmt = ("push" ~> expr <~ "onto") ~ (expr <~ (";"?) <~ step) ^^ { case _ => itodo("pushStmt") }
  def addStmt = ("add" ~> expr <~ "at the back of") ~ (expr <~ ("."?) <~ next) ^^ { case e1 ~ e2 => itodo("addexpr") }
  def suspendAndRemoveStmt = ("suspend" ~> expr) ~ ("and remove it from" ~> expr <~ ("."?) <~ next) ^^ { case e1 ~ e2 => itodo("suspendRemoveStmt") }
  def sourceCodeStmt = ("in an implementation - dependent manner , obtain the eCMAScript source texts ( see clause 10 ) and any associated host - defined values for zero or more eCMAScript scripts and / or eCMAScript modules . for each such" ~> id) ~ ("and" ~> id) ~ (", do" ~> innerStmt) ^^ {
    case i1 ~ i2 ~ stl => itodo("sourceCodeStmt")
  }
  def jobInitializeStmt = "perform any implementation or host environment defined job initialization using" ~> id <~ "." <~ next ^^ { case _ => itodo("jobInitialStmt") }
  lazy val stmt: Parser[Inst] = appendStmt |
    assertStmt |
    callStmt |
    ifthenElseStmt |
    ifStmt |
    letStmt |
    performStmt |
    repeatStmt |
    returnStmt |
    setObjStmt |
    setStmt |
    innerStmt |
    throwStmt |
    pushStmt |
    addStmt |
    suspendAndRemoveStmt |
    sourceCodeStmt |
    jobInitializeStmt |
    step ^^ { case tokens => itodo(tokens.mkString(" ").replace("\"", "\\\"")) }

  def numberExpr = number ^^ { case i => etodo(i) }
  def evaluationExpr = ("the result of evaluating" ~> word) ^^ { case _ => etodo("eval") }
  def stringValueExpr = ("StringValue of" ~ word) ^^ { case _ => etodo("eval") }
  def nameCallExpr = ((word <~ "(") ~ (repsep(expr, ",") ~ ")")) ^^ { case s ~ s1 => etodo("eval") }
  def fieldCallExpr = (id <~ ".") ~ (field <~ "(") ~ (repsep(expr, ",") ~ ")") ^^ { case _ => etodo("eval") }
  def questionExpr = ("?" ~> expr) ^^ { case _ => etodo("eval") }
  def lhsExpr = lhs ^^ { case s => etodo("eval") }
  def recordExpr = "a new" ~> word <~ "record" ^^ { case _ => etodo("eval") }
  def emptyRecordExpr = "a new record" ^^ { case _ => etodo("eval") }
  def executionContextExpr = "a new execution context" ^^ { case _ => etodo("eval") }
  def executionContextStackExpr = "the execution context stack" ^^ { case _ => etodo("eval") }
  def runningExecutionContextExpr = "the running execution context" ^^ { case _ => etodo("eval") }
  def emptyListExpr = "a new empty list" ^^ { case _ => etodo("eval") }
  def emptyList1Expr = "«" <~ "»" ^^ { case _ => etodo("eval") }
  def fieldListExpr = "«" ~> repsep(expr, ",") <~ "»" ^^ { case _ => etodo("eval") }
  def filledRecordExpr = (word <~ "{") ~ (repsep(fieldInit, ",") <~ "}") ^^ { case r1 ~ fs => etodo("eval") }
  def nonEmptyJobExpr = ("a non - empty job queue chosen in an implementation - defined manner . if all job queues are empty , the result is implementation - defined") ^^ { _ => etodo("jobqueue") }
  def recordPopExpr = ("the pendingJob record at the front of" ~> id) ~ (". remove that record from " ~> id) ^^ { case i1 ~ i2 => etodo("rpop") }
  def emptySequenceExpr = "an empty sequence of algorithm steps" ^^ { _ => etodo("ese") }
  def jobQueueExpr = "the job queue named by" ~> id ^^ { case s => etodo("eval") }
  def valueExpr = value ^^ { case s => etodo("eval") }
  def newObjectExpr = "a newly created object with an internal slot for each name in" ~> id ^^ { case s => etodo("eval") }
  def throwAlgorithmExpr = "the algorithm steps specified in 9 . 2 . 9 . 1 for the % throwTypeError % function" ^^ { _ => etodo("tae") }
  def abstractOperationExpr = ("the result of performing the abstract operation named by" ~> expr) ~ ("using the elements of" ~> expr <~ "as its arguments") ^^ { case e1 ~ e2 => etodo("aoe") }
  def pendingProcessingExpr = ("any implementation or host environment defined processing of" ~> id <~ ". this may include modifying the") ~ (field <~ "field or any other field of") ~ id ^^ { case e1 ~ e2 ~ e3 => etodo("ppe") }
  def wordExpr = word ^^ { case s => etodo(s) }
  lazy val expr: Parser[Expr] = numberExpr |
    evaluationExpr |
    stringValueExpr |
    nameCallExpr |
    fieldCallExpr |
    questionExpr |
    lhsExpr |
    recordExpr |
    emptyRecordExpr |
    executionContextExpr |
    executionContextStackExpr |
    runningExecutionContextExpr |
    emptyListExpr |
    emptyList1Expr |
    fieldListExpr |
    filledRecordExpr |
    nonEmptyJobExpr |
    recordPopExpr |
    emptySequenceExpr |
    jobQueueExpr |
    valueExpr |
    newObjectExpr |
    throwAlgorithmExpr |
    abstractOperationExpr |
    pendingProcessingExpr |
    wordExpr
  def specialField = "[" ~> "[" ~> word <~ "]" <~ "]" ^^ { case s => s"""[[$s]]""" }
  def symbolField = "[" ~> "[" ~> symbol <~ "]" <~ "]" ^^ { case s => s"""[[$s]]""" }
  def normalField = word ^^ { case s => s }
  lazy val field = specialField |
    symbolField |
    normalField

  lazy val fieldInit = (field <~ ":") ~ expr ^^ { case s ~ s2 => s"""$s : $s2""" }

  lazy val symbol = "%" ~ word ~ "%" ^^ { case s => s"""%$s%""" }

  def propLhs = (id <~ ("." | ("'" <~ "s"))) ~ field ^^ { case s ~ i => s"""$s.$i""" }
  def idLhs = id
  lazy val lhs = propLhs | idLhs

  def cond0 = a0cond ~ ("or" <~ a0cond) ^^ { case c0 ~ c1 => etodo("cond0") }
  def cond1 = a0cond ^^ { case c0 => etodo("cond1") }
  lazy val cond = cond0 | cond1

  def idNotPresent = id <~ "is not present" ^^ { case i1 => etodo("idNotPresent") }
  def a0Cond1 = (id <~ "is") ~ value ^^ { case i1 ~ v1 => etodo("a0cond1") }
  def a0Cond2 = "the code matching the syntactic production that is being evaluated is contained in strict mode code" ^^ { case _ => etodo("a0cond2") }
  def a0Cond3 = id <~ "is an abrupt completion" ^^ { case i1 => etodo("a0cond3") }
  def a0Cond4 = (expr <~ "is") ~ value ^^ { case e1 ~ v1 => etodo("a0cond4") }
  lazy val a0cond = idNotPresent | a0Cond1 | a0Cond2 | a0Cond3 | a0Cond4
}

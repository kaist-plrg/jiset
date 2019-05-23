package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.{ Parser => CoreParser, _ }
import kr.ac.kaist.ase.parser.TokenParsers
import kr.ac.kaist.ase.algorithm.Algorithm

object AlgoCompiler extends TokenParsers {
  def apply(algo: Algorithm): Func = Func(
    params = algo.params.map(Id(_)),
    body = ISeq(parseAll(rep(stmt), (algo.toTokenList)).get)
  )

  def appendStmt = ("append" ~> expr <~ "to") ~ (id <~ ("."?) <~ next) ^^ { case e1 ~ id => INotYetImpl("appendExpr") }
  def assertStmt = "assert :" <~ step ^^ { case _ => INotYetImpl("assertStmt") }
  def callStmt = "call" ~> expr <~ ("."?) <~ next ^^ { case _ => INotYetImpl("callStmt") }
  def ifthenElseStmt = ("if" ~> cond <~ (","?)) ~ (stmt <~ (("." | ";" | ",")?) <~ ("otherwise" | "else") <~ (","?)) ~ stmt ^^ { case c ~ s1 ~ s2 => INotYetImpl("Ite") }
  def ifStmt = ("if" ~> cond <~ (","?) <~ ("then"?)) ~ stmt ^^ { case c ~ s => INotYetImpl("ifStmt") }
  def letStmt = ("let" ~> id <~ "be") ~ (expr <~ ("."?) <~ next) ^^ { case e1 ~ e2 => INotYetImpl("letStmt") }
  def performStmt = "perform" ~> expr <~ ("."?) <~ next ^^ { case e1 => INotYetImpl("performStmt") }
  def repeatStmt = "repeat ," ~> in ~> rep(stmt) <~ out <~ next ^^ { case s => ISeq(s) }
  def returnStmt = "return" ~> expr <~ ("."?) <~ next ^^ { case e1 => INotYetImpl("returnStmt") }
  def setObjStmt = "set" ~> id <~ "' s essential internal methods to the default ordinary object definitions specified in 9 . 1 ." <~ next ^^ { case i1 => INotYetImpl("setObj") }
  def setStmt = ("set" ~> lhs) ~ ("to" <~ expr <~ ("."?) <~ next) ^^ { case e1 ~ e2 => INotYetImpl("setStmt") }
  def innerStmt = in ~> rep(stmt) <~ out <~ next ^^ { case slist => INotYetImpl("innerStmt") }
  def throwStmt = "throw a" ~> value <~ "exception" <~ ("."?) <~ next ^^ { case _ => INotYetImpl("throwStmt") }
  def pushStmt = ("push" ~> expr <~ "onto") ~ (expr <~ (";"?) <~ step) ^^ { case _ => INotYetImpl("pushStmt") }
  def addStmt = ("add" ~> expr <~ "at the back of") ~ (expr <~ ("."?) <~ next) ^^ { case e1 ~ e2 => INotYetImpl("addexpr") }
  def suspendAndRemoveStmt = ("suspend" ~> expr) ~ ("and remove it from" ~> expr <~ ("."?) <~ next) ^^ { case e1 ~ e2 => INotYetImpl("suspendRemoveStmt") }
  def sourceCodeStmt = ("in an implementation - dependent manner , obtain the eCMAScript source texts ( see clause 10 ) and any associated host - defined values for zero or more eCMAScript scripts and / or eCMAScript modules . for each such" ~> id) ~ ("and" ~> id) ~ (", do" ~> innerStmt) ^^ {
    case i1 ~ i2 ~ stl => INotYetImpl("sourceCodeStmt")
  }
  def jobInitializeStmt = "perform any implementation or host environment defined job initialization using" ~> id <~ "." <~ next ^^ { case _ => INotYetImpl("jobInitialStmt") }
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
    step ^^ { case tokens => INotYetImpl(tokens.mkString(" ").replace("\"","\\\"")) }

  def numberExpr = number ^^ { case i => CoreParser.parseExpr(i) }
  def evaluationExpr = ("the result of evaluating" ~> word) ^^ { case _ => CoreParser.parseExpr("eval") }
  def stringValueExpr = ("StringValue of" ~ word) ^^ { case _ => CoreParser.parseExpr("eval") }
  def nameCallExpr = ((word <~ "(") ~ (repsep(expr, ",") ~ ")")) ^^ { case s ~ s1 => CoreParser.parseExpr("eval") }
  def fieldCallExpr = (id <~ ".") ~ (field <~ "(") ~ (repsep(expr, ",") ~ ")") ^^ { case _ => CoreParser.parseExpr("eval") }
  def questionExpr = ("?" ~> expr) ^^ { case _ => CoreParser.parseExpr("eval") }
  def lhsExpr = lhs ^^ { case s => CoreParser.parseExpr("eval") }
  def recordExpr = "a new" ~> word <~ "record" ^^ { case _ => CoreParser.parseExpr("eval") }
  def emptyRecordExpr = "a new record" ^^ { case _ => CoreParser.parseExpr("eval") }
  def executionContextExpr = "a new execution context" ^^ { case _ => CoreParser.parseExpr("eval") }
  def executionContextStackExpr = "the execution context stack" ^^ { case _ => CoreParser.parseExpr("eval") }
  def runningExecutionContextExpr = "the running execution context" ^^ { case _ => CoreParser.parseExpr("eval") }
  def emptyListExpr = "a new empty list" ^^ { case _ => CoreParser.parseExpr("eval") }
  def emptyList1Expr = "«" <~ "»" ^^ { case _ => CoreParser.parseExpr("eval") }
  def fieldListExpr = "«" ~> repsep(expr, ",") <~ "»" ^^ { case _ => CoreParser.parseExpr("eval") }
  def filledRecordExpr = (word <~ "{") ~ (repsep(fieldInit, ",") <~ "}") ^^ { case r1 ~ fs => CoreParser.parseExpr("eval") }
  def nonEmptyJobExpr = ("a non - empty job queue chosen in an implementation - defined manner . if all job queues are empty , the result is implementation - defined") ^^ { _ => CoreParser.parseExpr("jobqueue") }
  def recordPopExpr = ("the pendingJob record at the front of" ~> id) ~ (". remove that record from " ~> id) ^^ { case i1 ~ i2 => CoreParser.parseExpr("rpop") }
  def emptySequenceExpr = "an empty sequence of algorithm steps" ^^ { _ => CoreParser.parseExpr("ese") }
  def jobQueueExpr = "the job queue named by" ~> id ^^ { case s => CoreParser.parseExpr("eval") }
  def valueExpr = value ^^ { case s => CoreParser.parseExpr("eval") }
  def newObjectExpr = "a newly created object with an internal slot for each name in" ~> id ^^ { case s => CoreParser.parseExpr("eval") }
  def throwAlgorithmExpr = "the algorithm steps specified in 9 . 2 . 9 . 1 for the % throwTypeError % function" ^^ { _ => CoreParser.parseExpr("tae") }
  def abstractOperationExpr = ("the result of performing the abstract operation named by" ~> expr) ~ ("using the elements of" ~> expr <~ "as its arguments") ^^ { case e1 ~ e2 => CoreParser.parseExpr("aoe") }
  def pendingProcessingExpr = ("any implementation or host environment defined processing of" ~> id <~ ". this may include modifying the") ~ (field <~ "field or any other field of") ~ id ^^ { case e1 ~ e2 ~ e3 => CoreParser.parseExpr("ppe") }
  def wordExpr = word ^^ { case s => CoreParser.parseExpr(s) }
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

  def cond0 = a0cond ~ ("or" <~ a0cond) ^^ { case c0 ~ c1 => CoreParser.parseExpr("cond0") }
  def cond1 = a0cond ^^ { case c0 => CoreParser.parseExpr("cond1") }
  lazy val cond = cond0 | cond1

  def idNotPresent = id <~ "is not present" ^^ { case i1 => CoreParser.parseExpr("idNotPresent") }
  def a0Cond1 = (id <~ "is") ~ value ^^ { case i1 ~ v1 => CoreParser.parseExpr("a0cond1") }
  def a0Cond2 = "the code matching the syntactic production that is being evaluated is contained in strict mode code" ^^ { case _ => CoreParser.parseExpr("a0cond2") }
  def a0Cond3 = id <~ "is an abrupt completion" ^^ { case i1 => CoreParser.parseExpr("a0cond3") }
  def a0Cond4 = (expr <~ "is") ~ value ^^ { case e1 ~ v1 => CoreParser.parseExpr("a0cond4") }
  lazy val a0cond = idNotPresent | a0Cond1 | a0Cond2 | a0Cond3 | a0Cond4
}

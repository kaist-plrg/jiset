package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.parser.TokenParsers
import kr.ac.kaist.ase.algorithm.Algorithm

object AlgoCompiler extends TokenParsers {
  def apply(algo: Algorithm): Func = Func(
    params = algo.params.map(Id(_)),
    body = ISeq(parseAll(stmts, algo.toTokenList) match {
      case Success(res, _) => res
      case NoSuccess(_, reader) => error(s"[AlgoCompiler]:${algo.filename}: $reader")
    })
  )

  // short-cut for TODO
  def itodo(msg: String): IExpr = IExpr(etodo(msg))
  def etodo(msg: String): ENotYetImpl = ENotYetImpl(msg)

  lazy val stmts: Parser[List[Inst]] = rep(
    stmt <~ "." <~ next |
      step ^^ { case tokens => itodo(tokens.mkString(" ").replace("\"", "\\\"")) }
  )

  ////////////////////////////////////////////////////////////////////////////////
  // Instructions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val stmt: Parser[Inst] =
    returnStmt |
      letStmt |
      returnIfAbruptStmt
  // appendStmt |
  // assertStmt |
  // callStmt |
  // ifthenElseStmt |
  // ifStmt |
  // performStmt |
  // repeatStmt |
  // setObjStmt |
  // setStmt |
  // innerStmt |
  // throwStmt |
  // pushStmt |
  // addStmt |
  // suspendAndRemoveStmt |
  // sourceCodeStmt |
  // jobInitializeStmt

  // return statements
  def returnStmt =
    "return" ~> expr ^^ { IReturn(_) }

  // let statements
  def letStmt =
    ("let" ~> id <~ "be") ~ expr ^^ { case e1 ~ e2 => ILet(Id(e1), e2) }

  // ReturnIfAbrupt statements
  def returnIfAbruptStmt =
    "ReturnIfAbrupt(" ~> id <~ ")" ^^ {
      case x => IIf(
        cond = parseExpr(s"""(= $x["[[Type]]"] normal)"""),
        thenInst = parseInst(s"""$x = $x["[[Value]]"]"""),
        elseInst = parseInst(s"""return $x""")
      )
    }

  // def appendStmt = ("append" ~> expr <~ "to") ~ (id <~ ("."?) <~ next) ^^ { case e1 ~ id => itodo("appendExpr") }
  // def assertStmt = "assert :" <~ step ^^ { case _ => itodo("assertStmt") }
  // def callStmt = "call" ~> expr <~ ("."?) <~ next ^^ { case _ => itodo("callStmt") }
  // def ifthenElseStmt = ("if" ~> cond <~ ",") ~ (stmt <~ (("." | ";" | ",")?) <~ ("otherwise" | "else") <~ (","?)) ~ stmt ^^ {
  //   case c ~ s1 ~ s2 => IIf(c, s1, s2)
  // }
  // def ifStmt = ("if" ~> cond <~ "," <~ ("then"?)) ~ stmt ^^ {
  //   case c ~ s => IIf(c, s, ISeq(Nil))
  // }
  // def performStmt = "perform" ~> expr <~ ("."?) <~ next ^^ {
  //   case e1 => IExpr(e1)
  // }
  // def repeatStmt = "repeat ," ~> stmt ^^ {
  //   case s => IWhile(EBool(true), s)
  // }
  // def setObjStmt = "set" ~> id <~ "' s essential internal methods to the default ordinary object definitions specified in 9 . 1 ." <~ next ^^ { case i1 => itodo("setObj") }
  // def setStmt = ("set" ~> lhs) ~ ("to" ~> expr <~ ("."?) <~ next) ^^ {
  //   case e1 ~ e2 => IAssign(e1, e2)
  // }
  // def innerStmt = in ~> rep(stmt) <~ out <~ next ^^ {
  //   case slist => ISeq(slist)
  // }
  // def throwStmt = "throw a" ~> value <~ "exception" <~ ("."?) <~ next ^^ { case _ => itodo("throwStmt") }
  // def pushStmt = ("push" ~> expr <~ "onto") ~ (expr <~ (";"?) <~ step) ^^ { case _ => itodo("pushStmt") }
  // def addStmt = ("add" ~> expr <~ "at the back of") ~ (expr <~ ("."?) <~ next) ^^ { case e1 ~ e2 => itodo("addexpr") }
  // def suspendAndRemoveStmt = ("suspend" ~> expr) ~ ("and remove it from" ~> expr <~ ("."?) <~ next) ^^ { case e1 ~ e2 => itodo("suspendRemoveStmt") }
  // def sourceCodeStmt = ("in an implementation - dependent manner , obtain the eCMAScript source texts ( see clause 10 ) and any associated host - defined values for zero or more eCMAScript scripts and / or eCMAScript modules . for each such" ~> id) ~ ("and" ~> id) ~ (", do" ~> innerStmt) ^^ {
  //   case i1 ~ i2 ~ stl => itodo("sourceCodeStmt")
  // }
  // def jobInitializeStmt = "perform any implementation or host environment defined job initialization using" ~> id <~ "." <~ next ^^ { case _ => itodo("jobInitialStmt") }

  ////////////////////////////////////////////////////////////////////////////////
  // Expressions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val expr: Parser[Expr] =
    valueExpr |
      evaluationExpr |
      completionExpr
  // numberExpr |
  // evaluationExpr |
  // stringValueExpr |
  // nameCallExpr |
  // fieldCallExpr |
  // questionExpr |
  // lhsExpr |
  // recordExpr |
  // emptyRecordExpr |
  // executionContextExpr |
  // executionContextStackExpr |
  // runningExecutionContextExpr |
  // emptyListExpr |
  // emptyList1Expr |
  // fieldListExpr |
  // filledRecordExpr |
  // nonEmptyJobExpr |
  // recordPopExpr |
  // emptySequenceExpr |
  // jobQueueExpr |
  // newObjectExpr |
  // throwAlgorithmExpr |
  // abstractOperationExpr |
  // pendingProcessingExpr |
  // wordExpr

  // value expressions
  def valueExpr = value ^^ {
    case "null" => ENull
    case "true" => EBool(true)
    case "false" => EBool(false)
    case "undefined" => EUndef
    case x0 if (x0.length > 1 && x0.charAt(0) == '"' && x0.charAt(x0.length - 1) == '"') => EStr(x0.slice(1, x0.length - 1))
    case const @ ("empty" | "throw" | "normal") => ERef(RefId(Id(const.replaceAll("-", ""))))
    case s => etodo(s)
  }

  // AST evaluation expressions
  def evaluationExpr =
    "the result of evaluating" ~> word ^^ { case s => ERun(ERef(RefId(Id(s))), "Evaluation", Nil) }

  // completion expressions
  def completionExpr =
    "normalcompletion(" ~> expr <~ ")" ^^ {
      case e => EMap(Ty("Completion"), List(
        EStr("[[Type]]") -> parseExpr("normal"),
        EStr("[[Value]]") -> e,
        EStr("[[Target]]") -> parseExpr("empty")
      ))
    }

  // def numberExpr = number ^^ { case i => etodo(i) }
  // def stringValueExpr = ("StringValue of" ~> word) ^^ {
  //   case s => ERun(ERef(RefId(Id(s))), "StringValue", Nil)
  // }
  // def nameCallExpr = ((word <~ "(") ~ (repsep(expr, ",") <~ ")")) ^^ {
  //   case s ~ s1 => EApp(ERef(RefId(Id(s))), s1)
  // }
  // def fieldCallExpr = (id <~ ".") ~ (field <~ "(") ~ (repsep(expr, ",") <~ ")") ^^ {
  //   case name ~ fname ~ elist => EApp(ERef(RefProp(RefId(Id(name)), EStr(fname))), elist)
  // }
  // def questionExpr = ("?" ~> expr) ^^ { case _ => etodo("question") }
  // def lhsExpr = lhs ^^ {
  //   case ref => ERef(ref)
  // }
  // def recordExpr = "a new" ~> word <~ "record" ^^ {
  //   case s => EMap(Ty(s), Nil)
  // }
  // def emptyRecordExpr = "a new record" ^^ {
  //   case _ => EMap(Ty("Record"), Nil)
  // }
  // def executionContextExpr = "a new execution context" ^^ { case _ => etodo("executionContext") }
  // def executionContextStackExpr = "the execution context stack" ^^ { case _ => etodo("executionContextStack") }
  // def runningExecutionContextExpr = "the running execution context" ^^ { case _ => etodo("runningExecutionContext") }
  // def emptyListExpr = "a new empty list" ^^ {
  //   case _ => EList(Nil)
  // }
  // def emptyList1Expr = "«" <~ "»" ^^ {
  //   case _ => EList(Nil)
  // }
  // def fieldListExpr = "«" ~> repsep(expr, ",") <~ "»" ^^ {
  //   case elist => EList(elist)
  // }
  // def filledRecordExpr = (word <~ "{") ~ (repsep(fieldInit, ",") <~ "}") ^^ {
  //   case r1 ~ fs => EMap(Ty(r1), fs)
  // }
  // def nonEmptyJobExpr = ("a non - empty job queue chosen in an implementation - defined manner . if all job queues are empty , the result is implementation - defined") ^^ { _ => etodo("jobqueue") }
  // def recordPopExpr = ("the pendingJob record at the front of" ~> id) ~ (". remove that record from " ~> id) ^^ { case i1 ~ i2 => etodo("recordPop") }
  // def emptySequenceExpr = "an empty sequence of algorithm steps" ^^ { _ => etodo("emptySequence") }
  // def jobQueueExpr = "the job queue named by" ~> id ^^ {
  //   case s => ERef(RefId(Id(s)))
  // }
  // def newObjectExpr = "a newly created object with an internal slot for each name in" ~> id ^^ {
  //   case s => EMap(Ty("Object"), Nil)
  // }
  // def throwAlgorithmExpr = "the algorithm steps specified in 9 . 2 . 9 . 1 for the % throwTypeError % function" ^^ { _ => etodo("throwAlgorithm") }
  // def abstractOperationExpr = ("the result of performing the abstract operation named by" ~> expr) ~ ("using the elements of" ~> expr <~ "as its arguments") ^^ { case e1 ~ e2 => etodo("abstractOperation") }
  // def pendingProcessingExpr = ("any implementation or host environment defined processing of" ~> id <~ ". this may include modifying the") ~ (field <~ "field or any other field of") ~ id ^^ { case e1 ~ e2 ~ e3 => etodo("pendingProcessing") }
  // def wordExpr = word ^^ { case s => etodo(s) }
  // def specialField = "[" ~> "[" ~> word <~ "]" <~ "]" ^^ { case s => s"""[[$s]]""" }
  // def symbolField = "[" ~> "[" ~> symbol <~ "]" <~ "]" ^^ { case s => s"""[[$s]]""" }
  // def normalField = word ^^ { case s => s }
  // lazy val field = specialField |
  //   symbolField |
  //   normalField

  // lazy val fieldInit: Parser[(Expr, Expr)] = (field <~ ":") ~ expr ^^ {
  //   case s ~ s2 => (EStr(s), s2)
  // }

  // lazy val symbol = "%" ~ word ~ "%" ^^ { case s => s"""%$s%""" }

  // def propLhs = (id <~ ("." | ("'" <~ "s"))) ~ field ^^ { case s ~ i => RefProp(RefId(Id(s)), EStr(i)) }
  // def idLhs = id ^^ { case s => RefId(Id(s)) }
  // lazy val lhs: Parser[Ref] = propLhs | idLhs

  // def cond0 = a0cond ~ ("or" ~> a0cond) ^^ { case c0 ~ c1 => EBOp(OOr, c0, c1) }
  // def cond1 = a0cond ^^ { case c0 => c0 }
  // lazy val cond: Parser[Expr] = cond0 | cond1
  // def isAbruptCompletion = id <~ "is an abrupt completion" ^^ { case i1 => etodo("isAbruptCompletion") }

  // def idNotPresent = id <~ "is not present" ^^ {
  //   case i1 => EBOp(OEq, ERef(RefId(Id(i1))), EUndef)
  // }
  // def a0Cond0 = (expr <~ "is") ~ expr ^^ {
  //   case e1 ~ e2 => EBOp(OEq, e1, e2)
  // }
  // def a0Cond1 = "the code matching the syntactic production that is being evaluated is contained in strict mode code" ^^ { case _ => etodo("a0cond2") }
  // lazy val a0cond: Parser[Expr] = idNotPresent | isAbruptCompletion | a0Cond0 | a0Cond1
}

package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.parser.TokenParsers
import kr.ac.kaist.ase.algorithm.{ Algorithm, Token }

object AlgoCompiler extends TokenParsers {
  def apply(algo: Algorithm): Func = Func(
    params = algo.params.map(Id(_)),
    body = ISeq(parseAll(stmts, algo.toTokenList) match {
      case Success(res, _) => res
      case NoSuccess(_, reader) => error(s"[AlgoCompiler]:${algo.filename}: $reader")
    })
  )

  def parseStmt(tokens: List[Token]): Inst = parseAll(stmt, tokens).get

  // short-cut for TODO
  def itodo(msg: String): IExpr = IExpr(etodo(msg))
  def etodo(msg: String): ENotYetImpl = ENotYetImpl(msg)

  lazy val stmts: Parser[List[Inst]] = rep(
    stmt <~ opt(".") <~ next |
      step ^^ { case tokens => itodo(tokens.mkString(" ").replace("\"", "\\\"")) }
  )

  ////////////////////////////////////////////////////////////////////////////////
  // Instructions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val stmt: Parser[Inst] =
    returnStmt |
      letStmt |
      returnIfAbruptStmt |
      innerStmt |
      ifStmt

  // return statements
  lazy val returnStmt =
    "return" ~> expr ^^ {
      e => IReturn(e)
    }

  // let statements
  lazy val letStmt =
    ("let" ~> id <~ "be") ~ expr ^^ {
      case x ~ e => ILet(Id(x), e)
    } |
      ("let" ~> id <~ "be") ~ ("?" ~> expr) ^^ {
        case x ~ e => ISeq(List(
          ILet(Id(x), e),
          IIf(
            cond = parseExpr(s"""(= $x["[[Type]]"] normal)"""),
            thenInst = parseInst(s"""$x = $x["[[Value]]"]"""),
            elseInst = parseInst(s"""return $x""")
          )
        ))
      }

  // ReturnIfAbrupt statements
  lazy val returnIfAbruptStmt =
    "ReturnIfAbrupt(" ~> id <~ ")" ^^ {
      case x => IIf(
        cond = parseExpr(s"""(= $x["[[Type]]"] normal)"""),
        thenInst = parseInst(s"""$x = $x["[[Value]]"]"""),
        elseInst = parseInst(s"""return $x""")
      )
    }

  // inner statements
  lazy val innerStmt =
    in ~> stmts <~ out ^^ {
      case list => ISeq(list)
    }

  // if-then-else statements
  lazy val ifStmt =
    ("if" ~> cond <~ ", then") ~ stmt ^^ {
      case c ~ t => IIf(c, t, ISeq(Nil))
    }

  ////////////////////////////////////////////////////////////////////////////////
  // Expressions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val expr: Parser[Expr] =
    valueExpr |
      astExpr |
      completionExpr |
      callExpr |
      idExpr

  // value expressions
  lazy val valueExpr = value ^^ {
    case "null" => ENull
    case "true" => EBool(true)
    case "false" => EBool(false)
    case "undefined" => EUndef
    case s if s.startsWith("\"") && s.endsWith("\"") => EStr(s.slice(1, s.length - 1))
    case const @ ("empty" | "throw" | "normal") => ERef(RefId(Id(const.replaceAll("-", ""))))
    case s => etodo(s)
  }

  // AST semantics
  lazy val astExpr =
    "the stringvalue of identifiername" ^^^ {
      parseExpr(s"IdentifierName")
    } |
      "the result of evaluating" ~> word ^^ {
        case x => parseExpr(s"(run Evaluation of $x)")
      } |
      (opt("the") ~> word <~ "of") ~ word ^^ {
        case f ~ x => parseExpr(s"(run $f of $x)")
      }

  // completion expressions
  lazy val completionExpr =
    "normalcompletion(" ~> expr <~ ")" ^^ {
      case e => EMap(Ty("Completion"), List(
        EStr("[[Type]]") -> parseExpr("normal"),
        EStr("[[Value]]") -> e,
        EStr("[[Target]]") -> parseExpr("empty")
      ))
    }

  // call expressions
  lazy val callExpr =
    word ~ ("(" ~> repsep(expr, ",") <~ ")") ^^ {
      case s ~ list => EApp(ERef(RefId(Id(s))), list)
    }

  // identifiers expressions
  lazy val idExpr =
    (id | word) ^^ {
      case x => ERef(RefId(Id(x)))
    }

  ////////////////////////////////////////////////////////////////////////////////
  // Conditions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val cond: Parser[Expr] =
    (id <~ "is not present") ~ subCond ^^ {
      case x ~ f => f(EUOp(ONot, EExist(RefId(Id(x)))))
    } |
      (expr <~ "is") ~ expr ~ subCond ^^ {
        case l ~ r ~ f => f(EBOp(OEq, l, r))
      }

  lazy val subCond: Parser[Expr => Expr] =
    "or if" ~> cond ^^ {
      case r => (l: Expr) => EBOp(OOr, l, r)
    } | success(x => x)
}

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

  // temporal identifiers
  lazy val temp: String = "temp"
  lazy val tempId: Id = Id(temp)

  // empty instruction
  lazy val emptyInst: Inst = ISeq(Nil)

  // list of statements
  lazy val stmts: Parser[List[Inst]] = rep(
    stmt <~ opt(".") <~ next |
      step ^^ { case tokens => itodo(tokens.mkString(" ").replace("\"", "\\\"")) }
  ) // TODO flatten

  ////////////////////////////////////////////////////////////////////////////////
  // Instructions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val stmt: Parser[Inst] =
    returnStmt |
      letStmt |
      returnIfAbruptStmt |
      innerStmt |
      ifStmt |
      callStmt |
      setStmt |
      assertStmt

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
    ("if" ~> cond <~ "," <~ opt("then")) ~ stmt ^^ {
      case c ~ t => IIf(c, t, emptyInst)
    }

  // call statements
  lazy val callStmt =
    ("perform" | "call") ~> "?" ~> callExpr ^^ {
      case e => ISeq(List(
        ILet(tempId, e),
        IIf(
          cond = parseExpr(s"""(= $temp["[[Type]]"] normal)"""),
          thenInst = parseInst(s"""{}"""),
          elseInst = parseInst(s"""return $temp""")
        )
      ))
    } |
      ("perform" | "call") ~> callExpr ^^ {
        case e => IExpr(e)
      }

  // set statements
  lazy val setStmt =
    ("set" ~> ref) ~ ("to" ~> expr) ^^ {
      case r ~ e => IAssign(r, e)
    } |
      "set" ~ expr ~ "'s essential internal methods" ~ rest ^^ {
        case _ => emptyInst
      }

  // assert statements
  lazy val assertStmt =
    "Assert:" ~ rest ^^ {
      case _ => emptyInst
    }

  ////////////////////////////////////////////////////////////////////////////////
  // Expressions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val expr: Parser[Expr] =
    valueExpr |
      astExpr |
      completionExpr |
      callExpr |
      newExpr |
      listExpr |
      curExpr |
      algoExpr |
      refExpr

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
    refExpr ~ ("(" ~> repsep(expr, ",") <~ ")") ^^ {
      case f ~ list => EApp(f, list)
    }

  // new expressions
  lazy val newExpr =
    "a new empty list" ^^^ {
      EList(Nil)
    } |
      ("a new" | "a newly created") ~> ty <~ opt(("with" | "that") ~ rest) ^^ {
        case t => EMap(t, List())
      } // TODO handle after "with" or "that"

  // list expressions
  lazy val listExpr =
    "«" ~> repsep(expr, ",") <~ "»" ^^ {
      case list => EList(list)
    }

  // current expressions
  lazy val curExpr =
    "the current Realm Record" ^^^ {
      parseExpr("context.Realm")
    }

  // algorithm expressions
  lazy val algoExpr =
    "an empty sequence of algorithm steps" ^^^ {
      EFunc(Nil, emptyInst)
    }

  // reference expressions
  lazy val refExpr =
    "the algorithm steps specified in" ~ secno ~ "for the" ~> name <~ "function" ^^ {
      case x => ERef(RefId(Id(x)))
    } |
      ref ^^ {
        case r => ERef(r)
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

  ////////////////////////////////////////////////////////////////////////////////
  // Types
  ////////////////////////////////////////////////////////////////////////////////
  lazy val ty: Parser[Ty] =
    "realm record" ^^^ Ty("RealmRecord") |
      "record" ^^^ Ty("Record") |
      "object" ^^^ Ty("Object") |
      "built-in function object" ^^^ Ty("BuiltinFunctionObject")

  ////////////////////////////////////////////////////////////////////////////////
  // References
  ////////////////////////////////////////////////////////////////////////////////
  lazy val ref: Parser[Ref] =
    name ~ rep(field) ^^ {
      case x ~ es => es.foldLeft[Ref](RefId(Id(x))) {
        case (r, e) => RefProp(r, e)
      }
    }

  ////////////////////////////////////////////////////////////////////////////////
  // Fields
  ////////////////////////////////////////////////////////////////////////////////
  lazy val field: Parser[Expr] =
    "." ~> name ^^ {
      case x => EStr(x)
    } |
      "[" ~> expr <~ "]" ^^ {
        case e => e
      }

  ////////////////////////////////////////////////////////////////////////////////
  // Section Numbers
  ////////////////////////////////////////////////////////////////////////////////
  lazy val secno: Parser[List[Int]] =
    number ~ rep("." ~> number) ^^ {
      case n ~ list => n.toInt :: list.map(_.toInt)
    }

  ////////////////////////////////////////////////////////////////////////////////
  // Names
  ////////////////////////////////////////////////////////////////////////////////
  lazy val name: Parser[String] =
    word | "%" ~> word <~ "%" | "[[" ~> word <~ "]]" | "[[%" ~> word <~ "%]]" | id
}

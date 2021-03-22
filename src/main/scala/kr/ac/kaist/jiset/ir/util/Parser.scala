package kr.ac.kaist.jiset.ir

import java.io._
import java.nio.charset.Charset
import kr.ac.kaist.jiset.util.Useful._
import scala.util.parsing.combinator.{ JavaTokenParsers, RegexParsers }

// parsers
trait Parser extends JavaTokenParsers with RegexParsers {
  // parse a file into a IRNode
  def fileToInsts(f: String): List[Inst] = fromFile(f, insts)
  def fileToInst(f: String): Inst = fromFile(f, inst)
  def fileToExpr(f: String): Expr = fromFile(f, expr)
  def fileToRef(f: String): Ref = fromFile(f, ref)
  def fileToTy(f: String): Ty = fromFile(f, ty)
  def fileToUOp(f: String): UOp = fromFile(f, uop)
  def fileToBOp(f: String): BOp = fromFile(f, bop)

  // parse a String into a IRNode
  def parseInsts(str: String): List[Inst] = errHandle(parseAll(insts, str))
  def parseInst(str: String): Inst = errHandle(parseAll(inst, str))
  def parseExpr(str: String): Expr = errHandle(parseAll(expr, str))
  def parseRef(str: String): Ref = errHandle(parseAll(ref, str))
  def parseTy(str: String): Ty = errHandle(parseAll(ty, str))
  def parseUOp(str: String): UOp = errHandle(parseAll(uop, str))
  def parseBOp(str: String): BOp = errHandle(parseAll(bop, str))

  // treat comments as white spaces
  override protected val whiteSpace = """(\s|//.*)+""".r

  // parse from file
  def fromFile[T](f: String, parser: Parser[T]): T = {
    var fileName = new File(f).getCanonicalPath
    val fs = new FileInputStream(new File(f))
    val sr = new InputStreamReader(fs, Charset.forName("UTF-8"))
    val in = new BufferedReader(sr)
    val result = errHandle(parseAll(parser, in))
    in.close; sr.close; fs.close
    result
  }

  // parse with error message
  def errHandle[T](result: ParseResult[T]): T = result match {
    case Success(result, _) => result
    case err => error(s"[IRParser] $err")
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Syntax
  ////////////////////////////////////////////////////////////////////////////////
  // instructions
  lazy val insts: Parser[List[Inst]] = rep(inst)
  lazy val inst: Parser[Inst] = opt(integer <~ ":") ~ (
    "delete " ~> ref ^^ { IDelete(_) } |
    ("append " ~> expr <~ "->") ~ expr ^^ { case e ~ l => IAppend(e, l) } |
    ("prepend " ~> expr <~ "->") ~ expr ^^ { case e ~ l => IPrepend(e, l) } |
    "return " ~> expr ^^ { case e => IReturn(e) } |
    throwInst |
    ("if " ~> expr) ~ inst ~ ("else" ~> inst) ^^ { case c ~ t ~ e => IIf(c, t, e) } |
    ("while " ~> expr) ~ inst ^^ { case c ~ b => IWhile(c, b) } |
    "{" ~> rep(inst) <~ "}" ^^ { case seq => ISeq(seq) } |
    "assert " ~> expr ^^ { case e => IAssert(e) } |
    "print " ~> expr ^^ { case e => IPrint(e) } |
    ("let " ~> id <~ "=") ~ expr ^^ { case x ~ e => ILet(x, e) } |
    callInst |
    ("withcont " ~> id) ~ ("(" ~> repsep(id, ",") <~ ")" <~ "=") ~ inst ^^ { case x ~ ps ~ b => IWithCont(x, ps, b) } |
    ("set-type " ~> expr ~ ty) ^^ { case e ~ t => ISetType(e, t) } |
    (ref <~ "=") ~ expr ^^ { case r ~ e => IAssign(r, e) } |
    expr ^^ { case e => IExpr(e) }
  ) ^^ { case k ~ i => i.line = k.fold(-1)(_.toInt); i }

  lazy val throwInst: Parser[IThrow] = opt("(" ~> integer <~ ")") ~ (
    "throw " ~> ident ^^ { case x => IThrow(x) }
  ) ^^ { case k ~ i => i.asite = k.fold(-1)(_.toInt); i }

  lazy val callInst: Parser[CallInst] = opt("(" ~> integer <~ ")") ~ (
    ("app " ~> id <~ "=") ~ ("(" ~> expr) ~ (rep(expr) <~ ")") ^^ { case x ~ f ~ as => IApp(x, f, as) } |
    ("access " ~> id <~ "=") ~ ("(" ~> expr) ~ expr ~ (rep(expr) <~ ")") ^^ { case x ~ e1 ~ e2 ~ e3 => IAccess(x, e1, e2, e3) }
  ) ^^ { case k ~ i => i.csite = k.fold(-1)(_.toInt); i }

  // expressions
  lazy private val expr: Parser[Expr] = opt("(" ~> integer <~ ")") ~ (
    ref ^^ { ERef(_) } |
    s"${integer}i".r ^^ { case s => EINum(s.dropRight(1).toLong) } |
    s"${integer}n".r ^^ { case s => EBigINum(BigInt(s.dropRight(1).toLong)) } |
    floatingPointNumber ^^ { case s => ENum(s.toDouble) } |
    "Infinity" ^^ { case s => ENum(Double.PositiveInfinity) } |
    "+Infinity" ^^ { case s => ENum(Double.PositiveInfinity) } |
    "-Infinity" ^^ { case s => ENum(Double.NegativeInfinity) } |
    "NaN" ^^ { case s => ENum(Double.NaN) } |
    string ^^ { EStr(_) } |
    "true" ^^^ EBool(true) |
    "false" ^^^ EBool(false) |
    "undefined" ^^^ EUndef |
    "null" ^^^ ENull |
    "absent" ^^^ EAbsent |
    "???" ~> string ^^ { ENotSupported(_) } |
    "(" ~> (uop ~ expr) <~ ")" ^^ { case u ~ e => EUOp(u, e) } |
    "(" ~> (bop ~ expr ~ expr) <~ ")" ^^ { case b ~ l ~ r => EBOp(b, l, r) } |
    "(" ~> ("typeof" ~> expr) <~ ")" ^^ { case e => ETypeOf(e) } |
    "(" ~> ("is-completion" ~> expr) <~ ")" ^^ { case e => EIsCompletion(e) } |
    ("(" ~> repsep(id, ",") <~ ")") ~ ("[=>]" ~> inst) ^^ { case ps ~ b => ECont(ps, b) } |
    ("(" ~> "new" ~> ty) ~ ("(" ~> repsep(prop, ",") <~ ")" <~ ")") ^^ {
      case t ~ props => EMap(t, props)
    } |
    ("(" ~> "new" ~> ty <~ ")") ^^ { case t => EMap(t, Nil) } |
    ("(" ~> "new" ~> "[" ~> repsep(expr, ",") <~ "]" <~ ")") ^^ { EList(_) } |
    ("(" ~> "new" ~> "'" ~> expr <~ ")") ^^ { ESymbol(_) } |
    ("(" ~> "pop" ~> expr ~ expr <~ ")") ^^ { case l ~ x => EPop(l, x) } |
    ("(" ~> "is-instance-of" ~> expr) ~ (ident <~ ")") ^^ {
      case e ~ x => EIsInstanceOf(e, x)
    } |
    ("(" ~> "get-elems" ~> expr) ~ (ident <~ ")") ^^ {
      case e ~ x => EGetElems(e, x)
    } |
    "(" ~> "get-syntax" ~> expr <~ ")" ^^ { case e => EGetSyntax(e) } |
    "(" ~> "parse-syntax" ~> expr ~ expr <~ ")" ^^ { case e ~ r => EParseSyntax(e, r, EAbsent) } |
    "(" ~> "parse-syntax" ~> expr ~ expr ~ expr <~ ")" ^^ { case e ~ r ~ le => EParseSyntax(e, r, le) } |
    "(" ~> "convert" ~> expr ~ cop ~ rep(expr) <~ ")" ^^ { case e ~ r ~ l => EConvert(e, r, l) } |
    "(" ~> "contains" ~> expr ~ expr <~ ")" ^^ { case l ~ e => EContains(l, e) } |
    "[" ~> "?" ~> expr <~ "]" ^^ { case e => EReturnIfAbrupt(e, true) } |
    "[" ~> "!" ~> expr <~ "]" ^^ { case e => EReturnIfAbrupt(e, false) } |
    "(" ~> "copy-obj" ~> expr <~ ")" ^^ { case e => ECopy(e) } |
    "(" ~> "map-keys" ~> expr <~ ")" ^^ { case e => EKeys(e) }
  ) ^^ {
      case k ~ (e: AllocExpr) =>
        e.asite = k.fold(-1)(_.toInt); e
      case k ~ e =>
        if (k != None) println(e.beautified, k)
        assert(k == None)
        e
    }

  // properties
  lazy private val prop: Parser[(Expr, Expr)] =
    (expr <~ "->") ~ expr ^^ { case k ~ v => (k, v) }

  // references
  lazy private val ref: Parser[Ref] = {
    id ~ rep(propExpr) ^^ {
      case x ~ es => es.foldLeft[Ref](RefId(x)) {
        case (ref, expr) => RefProp(ref, expr)
      }
    }
  }
  lazy private val propExpr: Parser[Expr] =
    "." ~> ident ^^ { case x => EStr(x) } | "[" ~> expr <~ "]"

  // types
  lazy private val ty: Parser[Ty] = ident ^^ { Ty(_) }

  // identifiers
  lazy private val id: Parser[Id] = ident.withFilter(s => !(keywords contains s)) ^^ { Id(_) }
  private val keywords: Set[String] = Set(
    "Infinity", "NaN", "true", "false", "undefined", "null", "absent",
    "typeof", /*"new",*/ "pop", "is-instance-of", "get-syntax", "contains"
  )

  // unary operators
  lazy private val uop: Parser[UOp] = {
    "-" ^^^ ONeg | "!" ^^^ ONot | "~" ^^^ OBNot
  }

  // binary operators
  lazy private val bop: Parser[BOp] = (
    "+" ^^^ OPlus |
    "-" ^^^ OSub |
    "**" ^^^ OPow |
    "*" ^^^ OMul |
    "/" ^^^ ODiv |
    "%%" ^^^ OUMod |
    "%" ^^^ OMod |
    "==" ^^^ OEqual |
    "=" ^^^ OEq |
    "&&" ^^^ OAnd |
    "||" ^^^ OOr |
    "^^" ^^^ OXor |
    "&" ^^^ OBAnd |
    "|" ^^^ OBOr |
    "^" ^^^ OBXOr |
    "<<" ^^^ OLShift |
    "<" ^^^ OLt |
    ">>>" ^^^ OURShift |
    ">>" ^^^ OSRShift
  )

  // convert operators
  lazy private val cop: Parser[COp] = (
    "str2num" ^^^ CStrToNum |
    "str2bigint" ^^^ CStrToBigInt |
    "num2str" ^^^ CNumToStr |
    "num2int" ^^^ CNumToInt |
    "num2bigint" ^^^ CNumToBigInt |
    "bigint2num" ^^^ CBigIntToNum
  )

  // integers
  val integer = "(0|-?[1-9]\\d*)".r

  ////////////////////////////////////////////////////////////////////////////////
  // Helper functions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val string = ("\"[\u0000-\u000F]\"".r | stringLiteral) ^^ {
    case s => StringContext processEscapes s.substring(1, s.length - 1)
  }
}
object Parser extends Parser

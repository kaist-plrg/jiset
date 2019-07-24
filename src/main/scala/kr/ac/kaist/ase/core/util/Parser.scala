package kr.ac.kaist.ase.core

import java.io._
import java.nio.charset.Charset
import scala.util.Either
import scala.util.parsing.combinator.{ JavaTokenParsers, RegexParsers }

// parsers
object Parser extends JavaTokenParsers with RegexParsers {
  // parse a file into a CoreNode
  def fileToProgram(f: String): Program = fromFile(f, program)
  def fileToInst(f: String): Inst = fromFile(f, inst)
  def fileToExpr(f: String): Expr = fromFile(f, expr)
  def fileToRef(f: String): Ref = fromFile(f, ref)
  def fileToTy(f: String): Ty = fromFile(f, ty)
  def fileToUOp(f: String): UOp = fromFile(f, uop)
  def fileToBOp(f: String): BOp = fromFile(f, bop)
  def fileToValue(f: String): Value = fromFile(f, value)
  def fileToFunc(f: String): Func = fromFile(f, func)

  // parse a String into a CoreNode
  def parseProgram(str: String): Program = errHandle(parseAll(program, str))
  def parseInst(str: String): Inst = errHandle(parseAll(inst, str))
  def parseExpr(str: String): Expr = errHandle(parseAll(expr, str))
  def parseRef(str: String): Ref = errHandle(parseAll(ref, str))
  def parseTy(str: String): Ty = errHandle(parseAll(ty, str))
  def parseUOp(str: String): UOp = errHandle(parseAll(uop, str))
  def parseBOp(str: String): BOp = errHandle(parseAll(bop, str))
  def parseValue(str: String): Value = errHandle(parseAll(value, str))
  def parseFunc(str: String): Func = errHandle(parseAll(func, str))

  // treat comments as white spaces
  override protected val whiteSpace = """(\s|//.*)+""".r

  // parse from file
  private def fromFile[T](f: String, parser: Parser[T]): T = {
    var fileName = new File(f).getCanonicalPath
    if (File.separatorChar == '\\') {
      // convert path string to linux style for windows
      fileName = fileName.charAt(0).toLower + fileName.replace('\\', '/').substring(1)
    }
    val fs = new FileInputStream(new File(f))
    val sr = new InputStreamReader(fs, Charset.forName("UTF-8"))
    val in = new BufferedReader(sr)
    val result = errHandle(parseAll(parser, in))
    in.close; sr.close; fs.close
    result
  }

  // parse with error message
  private def errHandle[T](result: ParseResult[T]): T = result match {
    case Success(result, _) => result
    case err => error(s"[CoreParser] $err")
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Syntax
  ////////////////////////////////////////////////////////////////////////////////

  // programs
  lazy private val program: Parser[Program] = rep(inst) ^^ { Program(_) }

  // instructions
  lazy private val inst: Parser[Inst] = {
    "delete " ~> ref ^^ { IDelete(_) } |
      ("append " ~> expr <~ "->") ~ expr ^^ { case e ~ l => IAppend(e, l) } |
      ("prepend " ~> expr <~ "->") ~ expr ^^ { case e ~ l => IPrepend(e, l) } |
      "return " ~> expr ^^ { case e => IReturn(e) } |
      ("if " ~> expr) ~ inst ~ ("else" ~> inst) ^^ { case c ~ t ~ e => IIf(c, t, e) } |
      ("while " ~> expr) ~ inst ^^ { case c ~ b => IWhile(c, b) } |
      "{" ~> rep(inst) <~ "}" ^^ { case seq => ISeq(seq) } |
      "assert " ~> expr ^^ { case e => IAssert(e) } |
      "print " ~> expr ^^ { case e => IPrint(e) } |
      ("let " ~> id <~ "=") ~ expr ^^ { case x ~ e => ILet(x, e) } |
      ("app " ~> id <~ "=") ~ ("(" ~> expr) ~ (rep(expr) <~ ")") ^^ { case x ~ f ~ as => IApp(x, f, as) } |
      (ref <~ "=") ~ expr ^^ { case r ~ e => IAssign(r, e) } |
      expr ^^ { case e => IExpr(e) }
  }

  // expressions
  lazy private val expr: Parser[Expr] = {
    ref ^^ { ERef(_) } |
      "(0|-?[1-9]\\d*)i".r ^^ { case s => EINum(s.dropRight(1).toLong) } |
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
      "???" ~> string ^^ { ENotYetImpl(_) } |
      "!!!" ~> string ^^ { ENotSupported(_) } |
      "(" ~> (uop ~ expr) <~ ")" ^^ { case u ~ e => EUOp(u, e) } |
      "(" ~> (bop ~ expr ~ expr) <~ ")" ^^ { case b ~ l ~ r => EBOp(b, l, r) } |
      "(" ~> ("typeof" ~> expr) <~ ")" ^^ { case e => ETypeOf(e) } |
      ("(" ~> (rep1sep(id, ",") ~ opt("," ~> "..." ~> id) | success(Nil) ~ opt("..." ~> id)) <~ ")") ~ ("=>" ~> inst) ^^ { case ps ~ ox ~ b => EFunc(ps, ox, b) } |
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
      "(" ~> "parse-syntax" ~> expr ~ expr ~ rep(expr) <~ ")" ^^ { case e ~ r ~ le => EParseSyntax(e, r, le) } |
      "(" ~> "parse-string" ~> expr ~ pop <~ ")" ^^ { case e ~ r => EParseString(e, r) } |
      "(" ~> "convert" ~> expr ~ cop ~ rep(expr) <~ ")" ^^ { case e ~ r ~ l => EConvert(e, r, l) } |
      "(" ~> "contains" ~> expr ~ expr <~ ")" ^^ { case l ~ e => EContains(l, e) } |
      "(" ~> "copy-obj" ~> expr <~ ")" ^^ { case e => ECopy(e) } |
      "(" ~> "map-keys" ~> expr <~ ")" ^^ { case e => EKeys(e) } |
      "(" ~> (expr ~ rep(expr)) <~ ")" ^^ { case f ~ as => EApp(f, as) }
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
    "typeof", "new", "pop", "is-instance-of", "get-syntax", "contains"
  )

  // unary operators
  lazy private val uop: Parser[UOp] = {
    "-" ^^^ ONeg | "!" ^^^ ONot | "~" ^^^ OBNot
  }

  // binary operators
  lazy private val bop: Parser[BOp] = {
    "+" ^^^ OPlus |
      "-" ^^^ OSub |
      "**" ^^^ OPow |
      "*" ^^^ OMul |
      "/" ^^^ ODiv |
      "%" ^^^ OMod |
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
  }

  // parse-string operators
  lazy private val pop: Parser[POp] = (
    "string" ^^^ PStr |
    "number" ^^^ PNum |
    "tv-no-subs" ^^^ PTVNoSubs |
    "trv-no-subs" ^^^ PTRVNoSubs |
    "tv-head" ^^^ PTVHead |
    "trv-head" ^^^ PTRVHead |
    "tv-middle" ^^^ PTVMiddle |
    "trv-middle" ^^^ PTRVMiddle |
    "tv-tail" ^^^ PTVTail |
    "trv-tail" ^^^ PTRVTail
  )

  // convert operators
  lazy private val cop: Parser[COp] = (
    "str2num" ^^^ CStrToNum |
    "num2str" ^^^ CNumToStr |
    "num2int" ^^^ CNumToInt
  )

  ////////////////////////////////////////////////////////////////////////////////
  // Values
  ////////////////////////////////////////////////////////////////////////////////
  // values
  lazy private val value: Parser[Value] = {
    func |
      addr |
      "(0|-?[1-9]\\d*)i".r ^^ { case s => INum(s.dropRight(1).toLong) } |
      floatingPointNumber ^^ { case n => Num(n.toDouble) } |
      string ^^ { Str(_) } |
      "true" ^^^ { Bool(true) } |
      "false" ^^^ { Bool(false) } |
      "undefined" ^^^ Undef |
      "null" ^^^ Null |
      "absent" ^^^ Absent
  }

  // functions
  lazy private val func: Parser[Func] =
    string ~ ("(" ~> (
      rep1sep(id, ",") ~ opt("," ~> "..." ~> id) | success(Nil) ~ opt("..." ~> id)
    ) <~ ")") ~ ("=>" ~> inst) ^^ { case n ~ (ps ~ ox) ~ b => Func(n, ps, ox, b) }

  // addresses
  lazy private val addr: Parser[Addr] = {
    "#addr(" ~> wholeNumber <~ ")" ^^ { case s => DynamicAddr(s.toLong) } |
      "#" ~> ident ^^ { NamedAddr(_) }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Helper functions
  ////////////////////////////////////////////////////////////////////////////////
  lazy val string = stringLiteral ^^ { case s => s.substring(1, s.length - 1) }
}

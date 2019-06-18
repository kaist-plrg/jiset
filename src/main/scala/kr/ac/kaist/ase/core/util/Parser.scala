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
      ("push " ~> expr <~ "->") ~ expr ^^ { case e ~ l => IPush(e, l) } |
      "return " ~> expr ^^ { case e => IReturn(e) } |
      ("if " ~> expr) ~ inst ~ opt("else" ~> inst) ^^ { case c ~ t ~ e => IIf(c, t, e.getOrElse(ISeq(Nil))) } |
      ("while " ~> expr) ~ inst ^^ { case c ~ b => IWhile(c, b) } |
      "{" ~> rep(inst) <~ "}" ^^ { case seq => ISeq(seq) } |
      "assert " ~> expr ^^ { case e => IAssert(e) } |
      "print " ~> expr ^^ { case e => IPrint(e) } |
      ("let " ~> id <~ "=") ~ expr ^^ { case x ~ e => ILet(x, e) } |
      (ref <~ "=") ~ expr ^^ { case r ~ e => IAssign(r, e) } |
      expr ^^ { case e => IExpr(e) }
  }

  // expressions
  lazy private val expr: Parser[Expr] = {
    floatingPointNumber ^^ { case s => ENum(s.toDouble) } |
      "Infinity" ^^ { case s => ENum(Double.PositiveInfinity) } |
      "+Infinity" ^^ { case s => ENum(Double.PositiveInfinity) } |
      "-Infinity" ^^ { case s => ENum(Double.NegativeInfinity) } |
      "NaN" ^^ { case s => ENum(Double.NaN) } |
      "i(0|-?[1-9]\\d*)".r ^^ { case s => EINum(s.substring(1, s.length).toLong) } |
      stringLiteral ^^ { case s => EStr(s.substring(1, s.length - 1)) } |
      "true" ^^^ EBool(true) |
      "false" ^^^ EBool(false) |
      "undefined" ^^^ EUndef |
      "null" ^^^ ENull |
      "absent" ^^^ EAbsent |
      "???" ~> stringLiteral ^^ { case s => ENotYetImpl(s.substring(1, s.length - 1)) } |
      "(" ~> (uop ~ expr) <~ ")" ^^ { case u ~ e => EUOp(u, e) } |
      "(" ~> (bop ~ expr ~ expr) <~ ")" ^^ { case b ~ l ~ r => EBOp(b, l, r) } |
      "(" ~> ("?" ~> ref) <~ ")" ^^ { case ref => EExist(ref) } |
      "(" ~> ("typeof" ~> expr) <~ ")" ^^ { case e => ETypeOf(e) } |
      ("(" ~> repsep(id, ",") <~ ")") ~ ("=>" ~> inst) ^^
      { case ps ~ b => EFunc(ps, b) } |
      ("(" ~> "new" ~> ty) ~ ("(" ~> repsep(prop, ",") <~ ")" <~ ")") ^^ {
        case t ~ props => EMap(t, props)
      } |
      ("(" ~> "new" ~> ty <~ ")") ^^ { case t => EMap(t, Nil) } |
      ("(" ~> "new" ~> "[" ~> repsep(expr, ",") <~ "]" <~ ")") ^^ { EList(_) } |
      ("(" ~> "run" ~> ident <~ "of") ~ (expr <~ "with") ~ (repsep(expr, ",") <~ ")") ^^ {
        case name ~ id ~ l => ERun(id, name, l)
      } |
      ("(" ~> "run" ~> ident <~ "of") ~ (expr <~ ")") ^^ {
        case name ~ id => ERun(id, name, Nil)
      } |
      ("(" ~> "pop" ~> expr <~ ")") ^^ { case e => EPop(e) } |
      ("(" ~> "is-instance-of" ~> expr) ~ (ident <~ ")") ^^ {
        case e ~ x => EIsInstanceOf(e, x)
      } |
      "(" ~> "get-syntax" ~> expr <~ ")" ^^ { case e => EGetSyntax(e) } |
      "(" ~> (expr ~ rep(expr)) <~ ")" ^^ { case f ~ as => EApp(f, as) } |
      ref ^^ { ERef(_) }
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
    "." ~> id ^^ { case x => EStr(x.name) } | "[" ~> expr <~ "]"

  // types
  lazy private val ty: Parser[Ty] = ident ^^ { Ty(_) }

  // identifiers
  lazy private val id: Parser[Id] = ident ^^ { Id(_) }

  // unary operators
  lazy private val uop: Parser[UOp] = {
    "-" ^^^ ONeg | "!" ^^^ ONot | "~" ^^^ OBNot
  }

  // binary operators
  lazy private val bop: Parser[BOp] = {
    "+" ^^^ OPlus |
      "-" ^^^ OSub |
      "*" ^^^ OMul |
      "/" ^^^ ODiv |
      "%" ^^^ OMod |
      "=" ^^^ OEq |
      "&&" ^^^ OAnd |
      "||" ^^^ OOr |
      "&" ^^^ OBAnd |
      "|" ^^^ OBOr |
      "^" ^^^ OBXOr |
      "<<" ^^^ OLShift |
      "<" ^^^ OLt |
      ">>>" ^^^ OURShift |
      ">>" ^^^ OSRShift
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Values
  ////////////////////////////////////////////////////////////////////////////////
  // values
  lazy private val value: Parser[Value] = {
    func |
      addr |
      floatingPointNumber ^^ { case n => Num(n.toDouble) } |
      "i" ~> wholeNumber ^^ { case n => INum(n.toLong) } |
      stringLiteral ^^ { Str(_) } |
      "true" ^^^ { Bool(true) } |
      "false" ^^^ { Bool(false) } |
      "undefined" ^^^ Undef |
      "null" ^^^ Null |
      "absent" ^^^ Absent
  }

  // functions
  lazy private val func: Parser[Func] =
    ident ~ ("(" ~> repsep(id, ",") <~ ")") ~ ("=>" ~> inst) ^^ { case n ~ ps ~ b => Func(n, ps, b) }

  // addresses
  lazy private val addr: Parser[Addr] = {
    "#addr(" ~> wholeNumber <~ ")" ^^ { case s => DynamicAddr(s.toLong) } |
      "#" ~> ident ^^ { NamedAddr(_) }
  }
}

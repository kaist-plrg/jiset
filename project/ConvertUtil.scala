import scala.io.Source
import scala.util.parsing.combinator._
import java.io.{File, PrintWriter}
import scala.util.Failure

object ConvertUtil {
  def run: Unit = {
    for ( i <- fileList ) {
      val iname = i.getName
      val basename = iname.substring(0, iname.length - 5)
      val oname = s"./src/main/scala/kr/ac/kaist/ase/node/algorithm/${basename}.scala"
      parseAndGenerate(i.getAbsolutePath, s"./src/main/scala/kr/ac/kaist/ase/node/algorithm/${basename}.scala", basename)
    }
  }
  val fileList = {
    val d = new File("./src/main/resources/es2018/")
    d.listFiles.filter((x) => x.isFile && x.getName.endsWith(".rule")).toList
  }
  def parseAndGenerate(i: String, o: String, className: String): Unit = {
    val pw = new PrintWriter(new File(o))

    val rules = Source.fromFile(i).getLines.map(RuleParser(_)).toList
    pw.println("package kr.ac.kaist.ase.node.algorithm")
    pw.println(s"sealed trait $className")

    rules.foreach{
      case (subname, subrule) => {
        val stype = subrule.map{ case (a, b) => a }.filter{
          case EmptyType() => false
          case _ => true
        }.zipWithIndex.map{ case (t, i) => s"e${i}: $t"}.mkString(", ")
        pw.println(s"case class ${subname}($stype) extends $className")
      }
    }

    pw.println(s"trait ${className}Parsers { this: AlgorithmParsers =>")
    rules.foreach{
      case (subname, subrule) => {
        pw.println(s"def ${subname.head.toLower + subname.tail}: Parser[$subname] = {")
        val rulelist = subrule.foldLeft(List[String]()){
          case (Nil, (EmptyType(), st)) => List("\"\" " + st)
          case (Nil, (_, st)) => List("\"\"", st)
          case (rest :+ s, (EmptyType(), st)) => rest :+ (s + st)
          case (rest, (_, t)) => rest :+ t
        }
        val rulestr = rulelist.map{s => s"($s)"}.mkString(" ~ ")
        val paramlist = List.tabulate(rulelist.length - 1)(i => s"e${i}")

        val argstr = paramlist.mkString(" ~ ")
        val paramstr = paramlist.mkString(", ")
        pw.println(s"${rulestr} ^^ {")
        pw.println(s"case _ ~ ${argstr} => ${subname}(${paramstr})")
        pw.println("}")
        pw.println("}")
      }
    }
    pw.println(s"lazy val ${className.head.toLower + className.tail} = ")
    pw.println(rules.map{ case (subname, _) => s"${subname.head.toLower + subname.tail}"}.mkString(" |\n"))
    pw.println("}")
    pw.close()
  }
}

sealed trait RuleType
case class ListType(ty: RuleType) extends RuleType {
  override def toString = s"List[$ty]"
}
case class OptionType(ty: RuleType) extends RuleType {
  override def toString = s"Option[$ty]"
}
case class ClassType(f: String) extends RuleType {
  override def toString = f
}
case class EmptyType() extends RuleType

object RuleParser extends RegexParsers {
  override def skipWhitespace = false
  lazy val tokens: Parser[List[(RuleType, String)]] = repsep(token, whiteSpace)
  lazy val token : Parser[(RuleType, String)] =
    nt ^^ { case (ty, s) => (ty, s) } |
  rep1(t) ^^ { ts => (EmptyType(), " <~ " + ts.mkString(" ")) }
  lazy val nt: Parser[(RuleType, String)] =
  nt0 <~ "?" ^^ { case (ty, s) => (OptionType(ty), s + "?") } |
  (nt0 <~ "*(") ~ (t <~ ")") ^^ { case (ty, s) ~ y => (ListType(ty), s"repsep($s, $y)") } |
  nt0 <~ "*" ^^ { case (ty, s) => (ListType(ty), s"rep($s)") } |
  (nt0 <~ "+(") ~ (t <~ ")") ^^ { case (ty, s) ~ y => (ListType(ty), s"rep1sep($s, $y)") } |
  nt0 <~ "+" ^^ { case (ty, s) => (ListType(ty), s"rep1($s)") } |
  nt0 ^^ {case x => x }

  lazy val nt0: Parser[(ClassType, String)] =
    """[A-Z]\w*(?!\w)""".r ^^ { x => (ClassType(x), x.head.toLower + x.tail) }

  lazy val t: Parser[String] =
    """[a-z]\w*(?!\w)""".r ^^ { x => s""""$x"""" } |
  """\\.""".r ^^ { x => s""""${x.tail}"""" } |
  """[^\w\s]""".r ^^ { x => x } 


  lazy val rule: Parser[(String, List[(RuleType, String)])] =
    (nt <~ rep(whiteSpace) <~ "=" <~ rep(whiteSpace)) ~ tokens ^^ {
      case (ClassType(n), _) ~ y => (n, y)
    }
  def apply(s: String): (String, List[(RuleType, String)]) = parseAll(rule, s) match {
    case Success(result, _) => { println(result); result }
    case Failure(msg, next) => {println((msg, next.first, next.rest.first)); throw new Exception("Exception")}
    case _ => throw new Exception("Exception")
  }
}

package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir.Parser

// parser for ir with algorithm definitions
object IRParser extends Parsers {
  // parse a file
  def fileToIR(f: String): (List[Algo], Inst) = fromFileWithParser(f, ir)
  def fileToAlgo(f: String): Algo = fromFileWithParser(f, algo)

  // parse a string
  def parseIR(str: String): (List[Algo], Inst) = errHandle(parseAll(ir, str))
  def parseAlgo(str: String): Algo = errHandle(parseAll(algo, str))

  // IR
  lazy val ir: Parser[(List[Algo], Inst)] = rep(irElem) ^^ {
    case elems => elems.foldLeft((Vector[Algo](), Vector[Inst]())) {
      case ((a, i), Left(algo)) => (a :+ algo, i)
      case ((a, i), Right(inst)) => (a, i :+ inst)
    }
  } ^^ {
    case (algos, insts) => (algos.toList, ISeq(insts.toList))
  }
  lazy val irElem: Parser[Either[Algo, Inst]] =
    algo ^^ { Left(_) } | inst ^^ { Right(_) }

  // algorithm
  lazy val algo: Parser[Algo] = "def" ~> head ~ ("=" ~> inst) ^^ {
    case h ~ body => Algo(h, "", body, Nil)
  }

  // head
  lazy val algoName: Parser[String] = "\\S+".r
  lazy val head: Parser[Head] = (
    "[BUILTIN]" ~> ref ~ params ^^ {
      case r ~ ps => BuiltinHead(r, ps)
    } | "[METHOD]" ~> (ident <~ ".") ~ ident ~ ("(" ~> param <~ ")") ~ params ^^ {
      case b ~ n ~ r ~ ps => MethodHead(b, n, r, ps)
    } | opt("[NORMAL]") ~> algoName ~ params ^^ {
      case n ~ ps => NormalHead(n, ps)
    } // TODO SyntaxDirectedHead
  )

  import Param.Kind._
  lazy val params: Parser[List[Param]] = "(" ~> repsep(param, ",") <~ ")"
  lazy val param: Parser[Param] = (
    "..." ~> ident ^^ { case p => Param(p, Variadic) } |
    ident <~ "?" ^^ { case p => Param(p, Optional) } |
    ident ^^ { case p => Param(p, Normal) }
  )
}

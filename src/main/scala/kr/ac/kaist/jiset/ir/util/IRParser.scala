package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir.Parser

// parser for ir with algorithm definitions
object IRParser extends Parser {
  // parse a file
  def fileToIR(f: String): (List[Algo], Inst) = fromFile(f, ir)
  def fileToAlgo(f: String): Algo = fromFile(f, algo)

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
    case h ~ body => Algo(h, body, Nil)
  }

  // head
  lazy val head: Parser[Head] = "\\S+".r ~ { "(" ~> repsep(ident, ",") <~ ")" } ^^ {
    case name ~ params => NormalHead(name, params.map(Param(_)))
  }
}

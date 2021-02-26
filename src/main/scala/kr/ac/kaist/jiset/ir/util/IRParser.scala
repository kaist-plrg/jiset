package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir.Parser

// parser for ir with function definitions
object IRParser extends Parser {
  // parse a file
  def fileToIR(f: String): (List[Algo], Inst) = fromFile(f, ir)
  def fileToFunc(f: String): Function = fromFile(f, func)
  def fileToAlgo(f: String): Algo = fromFile(f, algo)

  // parse a string
  def parseIR(str: String): (List[Algo], Inst) = errHandle(parseAll(ir, str))
  def parseFunc(str: String): Function = errHandle(parseAll(func, str))
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

  // function
  lazy val func: Parser[Function] = algo ^^ { Translator(_) }

  // algorithm
  lazy val algo: Parser[Algo] = "def" ~> (head ~ inst) <~ "#" ^^ {
    case h ~ body => Algo(h, body, List(""))
  }

  // head
  lazy val head: Parser[Head] = ident ~ { "(" ~> repsep(ident, ",") <~ ")" } ^^ {
    case name ~ params => NormalHead(name, params.map(Param(_)))
  }
}

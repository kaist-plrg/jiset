package kr.ac.kaist.ase.algorithm

import kr.ac.kaist.ase.LINE_SEP
import kr.ac.kaist.ase.parser._
import kr.ac.kaist.ase.util.Appendable

// parameters
case class Param(name: String) extends Appendable {
  override def appendTo(sb: StringBuilder, pre: String): StringBuilder = {
    sb.append(pre).append("<param>").append(name).append("</param>").append(LINE_SEP)
  }
}

// parser for params
trait ParamParsers { this: AlgorithmParsers =>
  lazy val param: Parser[Param] = tagged("param", ident) ^^ { Param(_) }
}

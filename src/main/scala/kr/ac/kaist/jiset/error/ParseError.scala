package kr.ac.kaist.jiset.error

import kr.ac.kaist.jiset.util.Useful

sealed abstract class ParseError(msg: String) extends JISETError(msg)

case class NoFileError(cmd: String) extends ParseError({
  s"Need a file to $cmd."
})

case class NoParseRule(name: String) extends ParseError({
  s"No parsing rule for $name"
})

case class ParseFailed(msg: String) extends ParseError({
  s"Parse failed: $msg"
})

case class TooManySemicolonInsertion(max: Int) extends ParseError({
  s"More than $max semicolon insertions needed"
})

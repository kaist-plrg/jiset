package kr.ac.kaist.jiset

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import org.scalatest._

class GrammarTest extends JISETTest {
  // tag name
  override val tag = "grammar"

  // registration
  def init: Unit = {
    for (file <- walkTree(GRAMMAR_DIR)) {
      val filename = file.getName
      if (grammarFilter(filename)) {
        check(tag, filename, {
          // get version
          val version =
            if (filename.contains("recent")) RECENT_VERSION
            else removedExt(filename)
          // read answer
          val answer = readFile(file.toString)
          // get parse result of version
          val result = ECMAScript.grammar(version).toString
          // compare result with answer
          assert(answer == result)
        })
      }
    }
  }

  init
}

package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.ast.Script

// mutator
trait Mutator {
  // name
  val name: String

  // must be touched after mutation
  val nids: Set[Int]

  // program
  val program: JsProgram

  // mutate a given program
  def _mutate: Option[Script]

  // re-try counter
  private var tried = 0
  val retryMax: Int

  // check if mutation success
  def success(p0: JsProgram): Boolean =
    (p0.size < program.size) && (nids subsetOf p0.touchedNIds.toSet)

  // mutate wrapper
  def mutate: Option[JsProgram] = if (tried < retryMax) {
    tried += 1
    _mutate match {
      case Some(script) => try {
        val mutated = JsProgram.fromScript(script)
        if (success(mutated)) Some(mutated)
        else mutate
      } catch {
        case _: Throwable =>
          println("TODO: DEBUG", script.toString)
          mutate
      }
      case None => mutate
    }
  } else None
}

package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Useful._

// abstract states
case class AbsState(
  map: Map[String, AbsType] = Map()
) {
  // bottom check
  def isBottom: Boolean = map.isEmpty

  // lookup
  def apply(x: String): AbsType = map.getOrElse(x, AbsType.Absent)

  // normalization
  def normalized: AbsState =
    if (map.exists { case (_, v) => v == AbsType.Bot }) AbsState.Bot
    else this

  // partial order
  def ⊑(that: AbsState): Boolean = {
    (this.map.keySet ++ that.map.keySet)
      .forall(key => this(key) ⊑ that(key))
  }

  // not partial order
  def !⊑(that: AbsState): Boolean = !(this ⊑ that)

  // join operator
  def ⊔(that: AbsState): AbsState = {
    val keys = this.map.keySet ++ that.map.keySet
    val map = keys.map(key => key -> (this(key) ⊔ that(key))).toMap
    AbsState(map.filter { case (_, v) => v != AbsType.Absent })
  }

  // meet operator
  def ⊓(that: AbsState): AbsState = {
    val keys = this.map.keySet ++ that.map.keySet
    val map = keys.map(key => key -> (this(key) ⊓ that(key))).toMap
    AbsState(map.filter { case (_, v) => v != AbsType.Absent }).normalized
  }

  // variable update
  def +(pair: (String, AbsType)): AbsState = {
    val (x, t) = pair
    if (t.isBottom) AbsState.Bot
    else AbsState(map = map + (x -> t))
  }

  // conversion to string
  override def toString: String = {
    val app = new Appender
    app.wrap(for ((x, t) <- map) {
      app :> x >> " -> " >> t.toString >> LINE_SEP
    })
    app.toString
  }
}
object AbsState {
  // bottom value
  val Bot: AbsState = AbsState()
}

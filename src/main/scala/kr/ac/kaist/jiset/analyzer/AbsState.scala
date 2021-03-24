package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Useful._

// abstract states
case class AbsState(
  reachable: Boolean,
  map: Map[String, AbsType] = Map()
) {
  import AbsState._

  // bottom check
  def isBottom: Boolean = !reachable

  // normalization
  def normalized: AbsState = {
    if (!reachable) Bot
    else if (map.exists { case (_, v) => v.isBottom }) Bot
    else this
  }

  // partial order
  def ⊑(that: AbsState): Boolean = {
    (this.map.keySet ++ that.map.keySet)
      .forall(key => this(key) ⊑ that(key))
  }

  // not partial order
  def !⊑(that: AbsState): Boolean = !(this ⊑ that)

  // join operator
  def ⊔(that: AbsState): AbsState = AbsState(
    reachable = this.reachable || that.reachable,
    map = {
      val keys = this.map.keySet ++ that.map.keySet
      val map = keys.map(key => key -> (this(key) ⊔ that(key))).toMap
      map.filter { case (_, v) => !v.isMustAbsent }
    }
  )

  // meet operator
  def ⊓(that: AbsState): AbsState = AbsState(
    reachable = this.reachable && that.reachable,
    map = {
      val keys = this.map.keySet ++ that.map.keySet
      val map = keys.map(key => key -> (this(key) ⊓ that(key))).toMap
      map.filter { case (_, v) => !v.isMustAbsent }
    }
  ).normalized

  // define variable
  def define(x: String, t: AbsType): AbsState = norm({
    if (t.isBottom) Bot
    else copy(map = map + (x -> t))
  })

  // lookup references
  def lookup(x: String)(implicit model: Model): AbsType = norm {
    val AbsType(ts) = this(x)
    val local = ts - Absent
    val global = if (ts contains Absent) model(x).set else Set()
    AbsType(local ++ global)
  }
  def lookup(ref: AbsRef)(implicit model: Model): AbsType = norm(ref match {
    case AbsId(x) => lookup(x)
    case _ => ???
  })

  // update reference
  def update(ref: AbsRef, t: AbsType)(implicit model: Model): AbsState = norm(ref match {
    case _ => ???
  })

  // conversion to string
  override def toString: String = {
    val app = new Appender
    app.wrap(for ((x, t) <- map) {
      app :> x >> " -> " >> t.toString >> LINE_SEP
    })
    app.toString
  }

  // private helper functions
  private def norm(f: => AbsState): AbsState = if (isBottom) Bot else f
  private def norm(f: => AbsType): AbsType = if (isBottom) AbsType.Bot else f
  private def apply(x: String): AbsType =
    if (reachable) map.getOrElse(x, Absent.abs) else AbsType.Bot
}
object AbsState {
  // bottom value
  val Bot: AbsState = AbsState(reachable = false)

  // empty value
  val Empty: AbsState = AbsState(reachable = true)
}

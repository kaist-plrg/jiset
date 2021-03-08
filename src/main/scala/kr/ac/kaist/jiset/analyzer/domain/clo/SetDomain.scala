package kr.ac.kaist.jiset.analyzer.domain.clo

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object SetDomain extends clo.Domain {
  // abstract closure
  case class Pair(fid: Int, env: AbsEnv)

  val SetD = new generator.SetDomain[Pair]
  type SetD = SetD.Elem

  // abstraction function
  def alpha(clo: Clo): Elem = Elem(SetD(Pair(clo.fid, AbsEnv(clo.env))))

  // bottom value
  val Bot: Elem = Elem(SetD.Bot)

  // top value
  val Top: Elem = Elem(SetD.Top)

  // constructor
  def apply(set: SetD): Elem = Elem(set)

  // extractor
  def unapply(elem: Elem): Option[SetD] = Some(elem.set)

  case class Elem(set: SetD) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = this.set ⊑ that.set

    // join operator
    def ⊔(that: Elem): Elem = Elem(this.set ⊔ that.set)

    // meet operator
    def ⊓(that: Elem): Elem = Elem(this.set ⊓ that.set)

    // map function
    def map(f: Pair => Pair): Elem = Elem(set.map(f))

    // foreach function
    def foreach(f: Pair => Unit): Unit = set.foreach(f)

    // prune
    def prune(v: Clo): Elem = v match {
      case Clo(fid, env) => Elem(this.set.prune(Pair(fid, AbsEnv.alpha(env))))
    }

    // concretization function
    def gamma: concrete.Set[Clo] = for {
      Pair(f, ae) <- set.gamma
      e <- ae.gamma
    } yield Clo(f, e)

    // conversion to flat domain
    def getSingle: concrete.Flat[Clo] = for {
      Pair(f, ae) <- set.getSingle
      e <- ae.getSingle
    } yield Clo(f, e)
  }
}

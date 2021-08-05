package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// basic abstract completions
object BasicComp extends Domain {
  lazy val Bot = Elem(Map())

  // abstraction functions
  def apply(comp: AComp): Elem = {
    val AComp(ty, value, target) = comp
    Elem(Map(ty.name -> (AbsValue(value), AbsSimple(target))))
  }

  // appender
  implicit val app: App[Elem] = (app, elem) => {
    app >> elem.map
      .toList
      .sortBy(_._1)
      .map { case (k, (v, t)) => s"~$k~ -> ($v, $t)" }
      .mkString("{", ", ", "}")
  }

  // elements
  case class Elem(map: Map[String, (AbsValue, AbsSimple)]) extends ElemTrait {
    // partial order
    override def isBottom = map.isEmpty

    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case _ if this.isBottom => true
      case _ if that.isBottom => false
      case (Elem(lmap), Elem(rmap)) =>
        (lmap.keySet ++ rmap.keySet).forall(ty => {
          val (lv, lt) = this(ty)
          val (rv, rt) = that(ty)
          lv ⊑ rv && lt ⊑ rt
        })
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case _ if this.isBottom => that
      case _ if that.isBottom => this
      case (Elem(lmap), Elem(rmap)) => {
        val newMap = (lmap.keySet ++ rmap.keySet).toList.map(ty => {
          val (lv, lt) = this(ty)
          val (rv, rt) = that(ty)
          ty -> (lv ⊔ rv, lt ⊔ rt)
        }).toMap
        Elem(newMap)
      }
    }

    // lookup
    def apply(ty: String): (AbsValue, AbsSimple) =
      map.getOrElse(ty, (AbsValue.Bot, AbsSimple.Bot))

    // get single value
    def getSingle: Flat[AComp] = {
      if (isBottom) FlatBot
      else map.toList match {
        case List((ty, (value, target))) =>
          (value.getSingle, target.getSingle) match {
            case (FlatElem(v), FlatElem(t)) => FlatElem(AComp(AConst(ty), v, t))
            case _ => FlatTop
          }
        case _ => FlatTop
      }
    }
  }
}

package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// basic abstract completions
object BasicComp extends Domain {
  lazy val Bot = Elem(Map())

  // results
  case class Result(value: AbsValue, target: AbsSimple) {
    def ⊑(that: Result): Boolean =
      this.value ⊑ that.value && this.target ⊑ that.target
    def ⊔(that: Result): Result =
      Result(this.value ⊔ that.value, this.target ⊔ that.target)
  }

  // abstraction functions
  def apply(comp: AComp): Elem = {
    val AComp(ty, value, target) = comp
    Elem(Map(ty.name -> Result(AbsValue(value), AbsSimple(target))))
  }

  // appender
  implicit val app: App[Elem] = (app, elem) => {
    app >> elem.map
      .toList
      .sortBy(_._1)
      .map { case (k, Result(v, t)) => s"~$k~ -> ($v, $t)" }
      .mkString("{", ", ", "}")
  }

  // elements
  case class Elem(map: Map[String, Result]) extends ElemTrait {
    // partial order
    override def isBottom = map.isEmpty

    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case _ if this.isBottom => true
      case _ if that.isBottom => false
      case (Elem(lmap), Elem(rmap)) =>
        (lmap.keySet ++ rmap.keySet).forall(ty => {
          this(ty) ⊑ that(ty)
        })
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case _ if this.isBottom => that
      case _ if that.isBottom => this
      case (Elem(lmap), Elem(rmap)) => {
        val newMap = (lmap.keySet ++ rmap.keySet).toList.map(ty => {
          ty -> this(ty) ⊔ that(ty)
        }).toMap
        Elem(newMap)
      }
    }

    // lookup
    def apply(ty: String): Result =
      map.getOrElse(ty, Result(AbsValue.Bot, AbsSimple.Bot))

    // get single value
    def getSingle: Flat[AComp] = {
      if (isBottom) FlatBot
      else map.toList match {
        case List((ty, Result(value, target))) =>
          (value.getSingle, target.getSingle) match {
            case (FlatElem(v), FlatElem(t)) => FlatElem(AComp(AConst(ty), v, t))
            case _ => FlatTop
          }
        case _ => FlatTop
      }
    }
  }
}

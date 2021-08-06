package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// basic abstract completions
object BasicComp extends Domain {
  lazy val Bot = Elem(Map())

  // results
  case class Result(value: AbsValue, target: AbsValue) {
    def isBottom = value.isBottom && target.isBottom
    def ⊑(that: Result): Boolean =
      this.value ⊑ that.value && this.target ⊑ that.target
    def ⊔(that: Result): Result =
      Result(this.value ⊔ that.value, this.target ⊔ that.target)
  }
  object Result {
    val Bot = Result(AbsValue.Bot, AbsValue.Bot)
  }

  // abstraction functions
  def apply(comp: AComp): Elem = {
    val AComp(ty, value, target) = comp
    Elem(Map(ty.name -> Result(AbsValue(value), AbsValue(target))))
  }

  // appender
  implicit val app: App[Elem] = (app, elem) => {
    app >> elem.map
      .toList
      .sortBy(_._1)
      .map { case (k, Result(v, t)) => s"~$k~ -> ($v, $t)" }
      .mkString("{", ", ", "}")
  }

  // constructors
  def apply(pairs: (String, Result)*): Elem = this(pairs.toMap)
  def apply(map: Map[String, Result]): Elem = Elem(map)
  def unapply(elem: Elem) = Some(elem.map)

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
          resultOf(ty) ⊑ resultOf(ty)
        })
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case _ if this.isBottom => that
      case _ if that.isBottom => this
      case (Elem(lmap), Elem(rmap)) => {
        val newMap = (lmap.keySet ++ rmap.keySet).toList.map(ty => {
          ty -> resultOf(ty) ⊔ resultOf(ty)
        }).toMap
        Elem(newMap)
      }
    }

    // normal completions
    def normal: Result = resultOf("normal")

    // remove absent values
    def removeNormal: Elem = copy(map = map - "normal")

    // result of each completion type
    def resultOf(ty: String): Result =
      map.getOrElse(ty, Result(AbsValue.Bot, AbsValue.Bot))

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

    // merged result
    def mergedResult: Result =
      map.map { case (k, v) => v }.foldLeft(Result.Bot)(_ ⊔ _)

    // lookup
    def apply(value: AbsValue): AbsValue = {
      val str = value.str
      var newV = AbsValue.Bot
      val Result(v, t) = mergedResult
      if (str contains Str("Type"))
        newV ⊔= AbsValue.Bot.copy(const = AbsConst(map.keySet.map(AConst)))
      if (str contains Str("Value"))
        newV ⊔= v
      if (str contains Str("Target"))
        newV ⊔= t
      newV
    }
  }
}

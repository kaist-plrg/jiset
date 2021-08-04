package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.js.builtin.TyModel
import kr.ac.kaist.jiset.spec.algorithm.Algo
import scala.annotation.tailrec
import scala.collection.mutable.{ Map => MMap }

// IR Types
case class Ty(name: String) extends IRElem {
  import Ty._

  // check whether it has SubMap
  def hasSubMap: Boolean = {
    (name endsWith "Object") || (name endsWith "EnvironmentRecord")
  }

  // get root of type
  def root: Ty = parent.fold(this)(_.root)

  // get ancestor types
  def ancestors: Set[Ty] = strictAncestors + this
  def ancestorNames: Set[String] = ancestors.map(_.name)
  def strictAncestors: Set[Ty] = parent.map(_.ancestors).getOrElse(Set())
  def strictAncestorNames: Set[String] = strictAncestors.map(_.name)

  // check sub typing
  @tailrec
  final def <(that: Ty): Boolean = {
    if (this == that) true
    else parent match {
      case Some(parent) => parent < that
      case None => false
    }
  }

  // get name of base types
  def typeName: String = if (name endsWith "Object") "Object" else name

  // get parent types
  def parent: Option[Ty] = infoMap.get(name) match {
    case Some(Info(_, Some(parent), _)) => Some(Ty(parent))
    case _ => None
  }

  // get methods
  def methods: MMap[PureValue, (Value, Long)] = {
    val map = MMap[PureValue, (Value, Long)]()
    for ((name, algo) <- methodMap.getOrElse(name, Map())) {
      map += Str(name) -> (Func(algo), 0L)
    }
    map
  }
}
object Ty {
  // method map
  type MethodMap = Map[String, Algo]

  // type information
  case class Info(
    name: String,
    parent: Option[String],
    methods: MethodMap
  )

  // type information constructors
  def I(name: String, parent: String, methods: MethodMap): Info =
    Info(name, Some(parent), methods)
  def I(name: String, methods: MethodMap): Info =
    Info(name, None, methods)

  // get method map
  def getMethodMap(name: String): MethodMap = infoMap.get(name) match {
    case Some(info) =>
      val parentmethods = info.parent.map(getMethodMap).getOrElse(Map())
      val curmethods = info.methods
      parentmethods ++ curmethods
    case None => Map()
  }

  // get type information
  lazy val infos: List[Info] = TyModel.infos

  // type info map
  lazy val infoMap: Map[String, Info] =
    infos.map(info => info.name -> info).toMap

  // direct subtypes
  lazy val subTypes: Map[String, Set[String]] = {
    var children = Map[String, Set[String]]()
    for {
      info <- infos
      parent <- info.parent
      set = children.getOrElse(parent, Set())
    } children += parent -> (set + info.name)
    children
  }

  // recursive subtypes
  lazy val recSubTypes: Map[String, Set[String]] = {
    var descs = Map[String, Set[String]]()
    def aux(name: String): Set[String] = descs.get(name) match {
      case Some(set) => set
      case None =>
        val set = (for {
          sub <- subTypes.getOrElse(name, Set())
          elem <- aux(sub)
        } yield elem) + name
        descs += name -> set
        set
    }
    infos.collect { case Info(name, None, _) => aux(name) }
    descs
  }

  // method map
  lazy val methodMap: Map[String, MethodMap] =
    infos.map(info => info.name -> getMethodMap(info.name)).toMap
}

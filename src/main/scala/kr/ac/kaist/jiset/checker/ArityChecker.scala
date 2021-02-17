package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.DEBUG
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm.{ Algo, Param }
import kr.ac.kaist.jiset.util.Conversion._
import kr.ac.kaist.jiset.util.InfNum

object ArityChecker extends Checker {
  // for specifications
  def apply(
    spec: ECMAScript,
    targets: List[Algo]
  ): List[Result] = targets.flatMap(apply(spec.arities, _))

  // for algorithms
  def apply(arities: Map[String, (InfNum, InfNum)], algo: Algo): List[Result] = {
    var detected = List[Result]()
    var todo = List[Todo]()
    var accessed = Map[String, Target]()

    // temporal
    def isTemporal(v: String): Boolean = v.startsWith("__")

    // get base name of reference
    def getRefBaseName(r: Ref): String = r match {
      case RefId(Id(id)) => id
      case RefProp(r, e) => getRefBaseName(r)
    }

    // get target of function expression
    // type of evaluating f => Func/ASTMethod/Cont
    // Assume `r` is Func or ASTMethod
    def getTarget(r: Ref): Option[Target] = r match {
      case RefId(Id(id)) if !isTemporal(id) =>
        arities.get(id) match {
          case None => {
            if (DEBUG) todo ::= Todo(algo, id, 1)
            None
          }
          case Some(arity) => Some(Target(id, arity))
        }
      case RefId(Id(id)) => accessed.get(id) match {
        case None => {
          if (DEBUG) todo ::= Todo(algo, id, 2)
          None
        }
        case Some(target) => Some(target)
      }
      case RefProp(r, EStr(method)) => {
        val refBase = getRefBaseName(r)
        if (refBase == "PRIMITIVE") {
          // handle PRIMITIVE
          Some(Target(s"$refBase.$method", arities(s"Number::$method")))
        } else {
          if (DEBUG) todo ::= Todo(algo, refBase, 3)
          None
        }
      }
    }

    // algorithm body walker
    object Walker extends UnitWalker {
      override def walk(inst: Inst): Unit = inst match {
        case IApp(_, ERef(r), as) => {
          val argc = as.length
          // get target of function expr
          getTarget(r) match {
            case None =>
            case Some(target) => {
              // target arity
              val (l, u) = target.arity
              // check arity mismatch
              if (argc < l || argc > u)
                detected ::= Result(algo, target, argc)
            }
          }
        }
        case IAccess(Id(x), ERef(r), EStr(method)) => {
          // get base name
          val base = numberPattern.replaceAllIn(getRefBaseName(r), "")
          // get related info from `arities`
          val (ls, us) = arities.filter {
            // TODO handle implicit definition of chain production
            case (baseMethodPattern(b, m), _) => b == base && m == method
            case _ => false
          }.values.unzip
          if (!ls.isEmpty && !us.isEmpty) {
            // remember base and arity of variable x
            val target = Target(s"$base.$method", (ls.min, us.max))
            accessed += (x -> target)
          } else {
            if (DEBUG) todo ::= Todo(algo, x, 4)
          }
        }
        case _ => super.walk(inst)
      }
    }
    Walker.walk(algo.getBody)
    if (DEBUG) todo.foreach(println _)
    detected
  }

  // patterns
  val baseMethodPattern = """(\w+)(?:\[\d+,\d+\])\.(\w+)""".r
  val numberPattern = """\d+""".r

  // target
  case class Target(name: String, arity: (InfNum, InfNum))

  // result
  case class Result(
      algo: Algo,
      target: Target,
      argc: Int
  ) extends Bug {
    // bug name
    val name: String = "Arity Mismatch"

    // bug message
    val msg: String =
      s"${algo.name}: To call ${target.name}, ${target.arity}(current : ${argc}) argument(s) should be supplied"
  }

  // todo
  case class Todo(algo: Algo, unknown: String, n: Int) {
    override def toString: String = s"[TODO#$n] ${algo.name}: $unknown"
  }
}

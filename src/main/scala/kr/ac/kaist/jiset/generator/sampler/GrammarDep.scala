package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._

case class DepElem(name: String, params: Set[String])
case class GrammarDep(grammar: Grammar) {
  lazy val dep: Map[DepElem, Set[DepElem]] = _dep
  lazy val bases: Set[DepElem] = {
    val values = dep.values.reduce(_ ++ _)
    dep.keySet.filter {
      case elem if !(values contains elem) => true
      case DepElem(name, params) => prodMap(name).rhsList.exists {
        case rhs @ Rhs(tokens, cond) => rhs.satisfy(params) && tokens.forall {
          case _: Terminal => true
          case _ => false
        }
      }
    }
  }
  lazy val prodMap: Map[String, Production] =
    targetProds.map(prod => prod.lhs.name -> prod).toMap
  lazy val targetProds = prods.filter(!_.lhs.isModule)

  private var _dep: Map[DepElem, Set[DepElem]] = Map()
  private var _bases: Set[DepElem] = Set()
  private val Grammar(_, prods) = grammar

  targetProds.foreach(prod => {
    val Production(lhs, rhsList) = prod
    val Lhs(name, rawParams) = lhs
    val length = rawParams.length
    val size = 1 << length
    for (params <- rawParams.toSet.subsets) getDep(prod, DepElem(name, params))
  })

  private def add(from: DepElem, to: DepElem): Unit = _dep.get(from) match {
    case Some(set) => _dep += from -> (set + to)
    case None => _dep += from -> Set(to)
  }

  private def getDep(prod: Production, elem: DepElem): Unit =
    prod.rhsList.foreach(rhs => if (rhs.satisfy(elem.params)) getDep(rhs, elem))

  private def getDep(rhs: Rhs, elem: DepElem): Unit =
    if (!rhs.containsModuleNT) rhs.tokens.foreach(getDep(_, elem))

  private def getDep(token: Token, elem: DepElem): Unit = token match {
    case NonTerminal(name, args, optional) => {
      val params = elem.params
      val ntParams = args.flatMap(arg => {
        arg.headOption match {
          case Some(a) => a match {
            // ? => params, + => true, ~ => false
            case '?' if params contains (arg substring 1) => Some(arg substring 1)
            case '+' => Some(arg substring 1)
            case _ => None
          }
          case None => None
        }
      }).toSet
      val ntDepElem = DepElem(name, ntParams)
      add(ntDepElem, elem)
    }
    case ButNot(base, cases) => getDep(base, elem)
    case _ =>
  }
}

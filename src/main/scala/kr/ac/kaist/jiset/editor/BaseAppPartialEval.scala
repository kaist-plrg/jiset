package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.StateMonad
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec
import scala.collection.mutable.{ Map => MMap }
import kr.ac.kaist.jiset.js.ast.AbsAST
import kr.ac.kaist.jiset.js.ast.Lexical
import kr.ac.kaist.jiset.js.Initialize
import kr.ac.kaist.jiset.analyzer.domain.FlatElem

class BaseAppPartialEval extends PartialEval[EnvOnlyAbstraction[FlatVE]] {
  val vtbuilder = FlatVEBuilder
  val asbuilder = EnvOnlyAbstractionBuilder[FlatVE]()(vtbuilder)
  val instTransformer = new BaseAppInstTransformer(ExprTransformer(new BaseExprEvaluator))
}
class BaseAppInstTransformer(override val pet: ExprTransformer[EnvOnlyAbstraction[FlatVE]]) extends BaseInstTransformer(pet) {

  val simpleFuncs: Set[String] = Set(
    "GetArgument",
    "IsDuplicate",
    "IsArrayIndex",
    "min",
    "max",
    "abs",
    "floor",
    "fround",
    "ThrowCompletion",
    "NormalCompletion",
    "IsAbruptCompletion"
  )

  override def pe_iaccess: IAccess => Result[Inst] = {
    case IAccess(id, bexpr, expr, args) => ((s: EnvOnlyAbstraction[FlatVE]) => {
      val ((baseV, baseE), s1) = pet.pe(bexpr)(s)
      val ((propV, propE), s2) = pet.pe(expr)(s1)
      val ((argsV, argsE), s3) = args.foldLeft(((List[FlatVE](), List[Expr]()), s2)) {
        case (((lv, le), s), arg) => {
          val ((v, e), sp) = pet.pe(arg)(s)
          ((lv :+ v, le :+ e), sp)
        }
      }
      ((baseV, propV) match {
        case (FlatVE(FlatElem((b, _))), FlatVE(FlatElem((p, _)))) =>
          {
            val escapedb = b match {
              case NormalComp(v) => Some(v)
              case CompValue(_, _, _) => None
              case p: PureValue => Some(p)
            }
            val escapedp = p match {
              case NormalComp(v) => Some(v)
              case CompValue(_, _, _) => None
              case p: PureValue => Some(p)
            }
            (escapedb, escapedp) match {
              case (Some(eb), Some(ep)) => (eb, ep) match {
                case (ASTVal(Lexical(kind, str)), Str(name)) => {
                  val s4 = s3.setVar(id.name, FlatVEBuilder.simple(Interp.getLexicalValue(kind, name, str)))
                  (IAccess(id, baseE, propE, argsE), s4)
                }
                case (ASTVal(ast), Str("parent")) => (IAccess(id, baseE, propE, argsE), s3)
                case (ASTVal(ast), Str("children")) => (IAccess(id, baseE, propE, argsE), s3)
                case (ASTVal(ast), Str("kind")) => (IAccess(id, baseE, propE, argsE), s3)
                case (ASTVal(ast), Str(name)) => ast.semantics(name) match {
                  case Some((algo, asts)) if (argsV.forall { case FlatVE(FlatElem(_)) => true; case _ => false }) => {
                    val args = asts.map(Some(_)) ++ argsV.map {
                      case FlatVE(FlatElem((v, _))) => Some(v)
                      case _ => None
                    }
                    val (nalgo, ns) = pe(algo, args) // potential infinite loop
                    (ns.getRet match {
                      case FlatVE(FlatElem((v: SimpleValue, _))) => {
                        val s4 = s3.setVar(id.name, FlatVEBuilder.simple(v))
                        (IAccess(id, baseE, propE, argsE), s4)
                      }
                      case _ => (IAccess(id, baseE, propE, argsE), s3)
                    })
                  }
                  case _ => (IAccess(id, baseE, propE, argsE), s3)
                }
                case _ => (IAccess(id, baseE, propE, argsE), s3)
              }
              case _ => (IAccess(id, baseE, propE, argsE), s3)
            }
          }
        case _ => {
          (IAccess(id, baseE, propE, argsE), s3)
        }
      })
    })
  }

  override def pe_iapp: IApp => Result[Inst] = {
    case IApp(id, ERef(RefId(Id(name))), args) if simpleFuncs contains name => (s: EnvOnlyAbstraction[FlatVE]) => {
      val ((argsV, argsE), s2) = args.foldLeft(((List[FlatVE](), List[Expr]()), s)) {
        case (((lv, le), s), arg) => {
          val ((v, e), sp) = pet.pe(arg)(s)
          ((lv :+ v, le :+ e), sp)
        }
      }
      (IApp(id, ERef(RefId(Id(name))), argsE), s2)
    }
    case IApp(id, fexpr, args) => ((s: EnvOnlyAbstraction[FlatVE]) => {
      val ((fpV, fpE), s2) = pet.pe(fexpr)(s)
      val ((argsV, argsE), s3) = args.foldLeft(((List[FlatVE](), List[Expr]()), s2)) {
        case (((lv, le), s), arg) => {
          val ((v, e), sp) = pet.pe(arg)(s)
          ((lv :+ v, le :+ e), sp)
        }
      }
      (fpV match {
        case FlatVE(FlatElem((Func(algo), _))) if (argsV.forall { case FlatVE(FlatElem(_)) => true; case _ => false }) => {
          val (nalgo, ns) = pe(algo, argsV.map {
            case FlatVE(FlatElem((v, _))) => Some(v)
            case _ => None
          }) // potential infinite loop
          (ns.getRet match {
            case FlatVE(FlatElem((v: SimpleValue, _))) => {
              val s4 = s3.setVar(id.name, FlatVEBuilder.simple(v))
              (IApp(id, fpE, argsE), s4)
            }
            case _ => (IApp(id, fpE, argsE), s3)
          })
        }
        case _ => (IApp(id, fpE, argsE), s3)
      })
    })
  }
}

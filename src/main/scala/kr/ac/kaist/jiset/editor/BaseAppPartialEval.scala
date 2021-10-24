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

trait BaseAppPartialEval[LT <: LabelwiseContext[LT], GT <: GlobalContext[GT]] extends BasePartialEval[LT, GT] {
  import psm._

  override def pe_iaccess: IAccess => Result[Inst] = {
    case IAccess(id, bexpr, expr, args) => (for {
      baseE <- pe(bexpr)
      propE <- pe(expr)
      argsE <- join(args.map(pe))
      res <- ((baseE, propE) match {
        case (SymbolicValue(Some(b), _), SymbolicValue(Some(p), _)) =>
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
                case (ASTVal(Lexical(kind, str)), Str(name)) => for {
                  _ <- (context: S) => context.updateLabelwise((u) => u.setId(id.name, SymbolicValueFactory.mkSimple(Interp.getLexicalValue(kind, name, str))))
                } yield IAccess(id, baseE.expr, propE.expr, argsE.map(_.expr)) // IExpr(EStr("skip"))
                case (ASTVal(ast), Str("parent")) => IAccess(id, baseE.expr, propE.expr, argsE.map(_.expr))
                case (ASTVal(ast), Str("children")) => IAccess(id, baseE.expr, propE.expr, argsE.map(_.expr))
                case (ASTVal(ast), Str("kind")) => IAccess(id, baseE.expr, propE.expr, argsE.map(_.expr))
                case (ASTVal(ast), Str(name)) => ast.semantics(name) match {
                  case Some((algo, asts)) => {
                    val args = asts.map(Some(_)) ++ argsE.map(_.valueOption)
                    for {
                      peres <- pe(algo, args)
                      (_, rpv) = peres // TODO: test whether algorithm does not contains instruction causes side-effect
                      res <- (rpv match {
                        case Some(v) if v.isRepresentable => for {
                          _ <- (context: S) => context.updateLabelwise((u) => u.setId(id.name, v))
                        } yield IAccess(id, baseE.expr, propE.expr, argsE.map(_.expr)) // IExpr(EStr("skip"))
                        case _ => IAccess(id, baseE.expr, propE.expr, argsE.map(_.expr))
                      }): Result[Inst]
                    } yield res
                  }
                  case _ => IAccess(id, baseE.expr, propE.expr, argsE.map(_.expr))
                }
                case _ => IAccess(id, baseE.expr, propE.expr, argsE.map(_.expr))
              }
              case _ => IAccess(id, baseE.expr, propE.expr, argsE.map(_.expr))
            }
          }
        case _ => {
          IAccess(id, baseE.expr, propE.expr, argsE.map(_.expr))
        }
      }): Result[Inst]
    } yield res)
  }
  override def pe_iapp: IApp => Result[Inst] = {
    case IApp(id, ERef(RefId(Id(name))), args) if simpleFuncs contains name => for {
      argse <- join(args.map(pe))
    } yield IApp(id, ERef(RefId(Id(name))), argse.map(_.expr))
    case IApp(id, fexpr, args) => (for {
      fpe <- pe(fexpr)
      argse <- join(args.map(pe))
      res <- (fpe match {
        case SymbolicValue(Some(Func(algo)), _) => for {
          pres <- pe(algo, argse.map(_.valueOption))
          (_, rpv) = pres // TODO: test whether algorithm does not contains instruction causes side-effect
          res <- (rpv match {
            case Some(v) if v.isRepresentable => for {
              _ <- (context: S) => context.updateLabelwise((u) => u.setId(id.name, v))
            } yield IApp(id, fpe.expr, argse.map(_.expr)) // IExpr(EStr("skip"))
            case _ =>
              IApp(id, fpe.expr, argse.map(_.expr))
          }): Result[Inst]
        } yield res
        case _ => IApp(id, fpe.expr, argse.map(_.expr))
      }): Result[Inst]
    } yield res)
  }
}

object BaseAppPartialEvalImpl extends BaseAppPartialEval[SymbolicEnv, FunctionMap] {
  val lcbuilder = SymbolicEnvBuilder
  val gcbuilder = FunctionMapBuilder
}
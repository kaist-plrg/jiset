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

class BasePartialEval extends PassingPartialEval {

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

  val dynamicGlobal: Set[String] = {
    import kr.ac.kaist.jiset.js._
    Set(
      CONTEXT,
      EXECUTION_STACK,
      GLOBAL,
      JOB_QUEUE,
      PRIMITIVE,
      RET_CONT,
      SYMBOL_REGISTRY
    )
  }

  def isPermittedGlobal(id: Id) = if (dynamicGlobal contains id.name) false else true

  import PartialStateMonad._

  override def pe_ilet: ILet => Result[Inst] = {
    case ILet(id, expr) => for {
      v <- pe(expr)
      _ <- (context: S) => context.updateLabelwise((u) => u.setId(id.name, v))
    } yield ILet(id, v.expr) // if (v.isRepresentable) IExpr(EStr("skip")) else ILet(id, v.expr)
  }

  override def pe_iseq: ISeq => Result[Inst] = {
    case ISeq(insts) => { (dcontext: PartialContext) =>
      {
        val (z, y) = insts.foldLeft((List[Inst](), dcontext)) {
          case ((li, dc), i) => if (dc.labelwiseContext.getRet.map(_.isRepresentable).getOrElse(false)) (li, dc) else {
            val (i2, dc2) = pe(i)(dc)
            (li :+ i2, dc2)
          }
        }
        (ISeq(z), y)
      }
    }
  }

  override def pe_iif: IIf => Result[Inst] = {
    case IIf(cond, thenInst, elseInst) => for {
      c <- pe(cond)
      res <- (c match {
        case PartialValue(Some(Bool(true)), _) => pe(thenInst)
        case PartialValue(Some(Bool(false)), _) => pe(elseInst)
        case _ => (dcontext1: S) => {
          val (thenInstI, dcontext2) = pe(thenInst)(dcontext1)
          val (elseInstI, dcontext3) = pe(elseInst)(PartialContextImpl(dcontext1.labelwiseContext, dcontext2.globalContext))
          val ((eif, efalse), nstate) = dcontext2.labelwiseContext merge dcontext3.labelwiseContext
          (IIf(
            c.expr,
            thenInstI,
            elseInstI
          ), PartialContextImpl(nstate, dcontext3.globalContext))
        }
      }): Result[Inst]
    } yield res
  }

  override def pe_enum: ENum => Result[PartialValue] = {
    case ENum(n) => PartialExpr.mkSimple(Num(n))
  }

  override def pe_einum: EINum => Result[PartialValue] = {
    case EINum(n) => (PartialExpr.mkSimple(INum(n)))
  }

  override def pe_ebiginum: EBigINum => Result[PartialValue] = {
    case EBigINum(b) => (PartialExpr.mkSimple(BigINum(b)))
  }

  override def pe_estr: EStr => Result[PartialValue] = {
    case EStr(str) => (PartialExpr.mkSimple(Str(str)))
  }

  override def pe_ebool: EBool => Result[PartialValue] = {
    case EBool(b) => (PartialExpr.mkSimple(Bool(b)))
  }

  override def pe_eundef: EUndef.type => Result[PartialValue] = {
    case EUndef => (PartialExpr.mkSimple(Undef))
  }

  override def pe_enull: ENull.type => Result[PartialValue] = {
    case ENull => (PartialExpr.mkSimple(Null))
  }

  override def pe_eabsent: EAbsent.type => Result[PartialValue] = {
    case EAbsent => PartialExpr.mkSimple(Absent)
  }

  override def pe_eref: ERef => Result[PartialValue] = {

    case ERef(ref) => for {
      refr <- pe(ref)
      res <- (refr match {
        case RefId(id) => for {
          context <- PartialStateMonad.get
        } yield context.labelwiseContext.getId(id.name) match {
          case Some(PartialValue(Some(ASTVal(ast: AbsAST)), _)) => PartialExpr.mkDynamic(ERef(refr))
          case Some(v @ PartialValue(Some(_), _)) => v
          case Some(_) => PartialExpr.mkDynamic(ERef(refr))
          case None => if (isPermittedGlobal(id)) Initialize.initGlobal.get(id) match {
            case Some(v: Addr) => PartialExpr.mkDynamic(ERef(refr))
            case Some(v) => PartialExpr.mkSExpr(v, ERef(ref))
            case _ => PartialExpr.mkDynamic(ERef(refr))
          }
          else PartialExpr.mkDynamic(ERef(refr))
        }
        case _ => PartialExpr.mkDynamic(ERef(refr))
      }): PartialStateMonad.Result[PartialValue]
    } yield res
  }

  override def pe_euop: EUOp => Result[PartialValue] = {
    case EUOp(uop, expr) => (for {
      e <- pe(expr)
    } yield PartialExpr.mkPValue(e.valueOption.map((v) => Interp.interp(uop, v)), EUOp(uop, e.expr)))
  }

  override def pe_ebop: EBOp => Result[PartialValue] = {
    case EBOp(bop, left, right) => (for {
      le <- pe(left)
      re <- pe(right)
    } yield PartialExpr.mkPValue(le.valueOption.flatMap((lv) => re.valueOption.map((rv) => Interp.interp(bop, lv, rv))), EBOp(bop, le.expr, re.expr)))
  }

  override def pe_etypeof: ETypeOf => Result[PartialValue] = {
    case ETypeOf(expr) => for {
      e <- pe(expr)
      v = e.valueOption.flatMap {
        case NormalComp(value) => Some(value)
        case CompValue(_, _, _) => None
        case pure: PureValue => Some(pure)
      } match {
        case Some(value) => value match {
          case Const(_) => PartialExpr.mkSimple(Str("Constant"))
          case (addr: Addr) => PartialExpr.mkDynamic(ETypeOf(e.expr))
          case Func(_) => PartialExpr.mkSimple(Str("Function"))
          case Clo(_, _, _, _) => PartialExpr.mkSimple(Str("Closure"))
          case Cont(_, _, _) => PartialExpr.mkSimple(Str("Continuation"))
          case ASTVal(_) => PartialExpr.mkSimple(Str("AST"))
          case Num(_) | INum(_) => PartialExpr.mkSimple(Str("Number"))
          case BigINum(_) => PartialExpr.mkSimple(Str("BigInt"))
          case Str(_) => PartialExpr.mkSimple(Str("String"))
          case Bool(_) => PartialExpr.mkSimple(Str("Boolean"))
          case Undef => PartialExpr.mkSimple(Str("Undefined"))
          case Null => PartialExpr.mkSimple(Str("Null"))
          case Absent => PartialExpr.mkSimple(Str("Absent"))
        }
        case None => PartialExpr.mkDynamic(ETypeOf(e.expr))
      }
    } yield v // TODO

  }

  override def pe_eisinstanceof: EIsInstanceOf => Result[PartialValue] = {
    case EIsInstanceOf(base, name) => (for {
      be <- pe(base)
    } yield PartialExpr.mkPValue(be.valueOption.flatMap((bv) => {
      if (bv.isAbruptCompletion) Some(Bool(false))
      else bv.escaped match {
        case ASTVal(ast) => Some(Bool(ast.name == name || ast.getKinds.contains(name)))
        case Str(str) => Some(Bool(str == name))
        case addr: Addr => None
        case _ => Some(Bool(false))
      }
    }), EIsInstanceOf(be.expr, name)))
  }

  override def pe_ereturnifabrupt: EReturnIfAbrupt => Result[PartialValue] = {
    case EReturnIfAbrupt(expr, check) => (for {
      ne <- pe(expr)
    } yield PartialExpr.mkPValue(ne.valueOption.flatMap(({
      case NormalComp(value) => value
      case pure: PureValue => pure
    }: PartialFunction[Value, Value]).lift), EReturnIfAbrupt(ne.expr, check)))
  }

}

object BasePartialEvalImpl extends BasePartialEval
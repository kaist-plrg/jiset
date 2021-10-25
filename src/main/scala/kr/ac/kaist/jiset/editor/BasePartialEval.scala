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

case class MustBoundVariableWalker(m: collection.mutable.Set[String]) extends UnitWalker {
  override def walk(i: Inst) = i match {
    case IClo(id, params, captured, body) => m ++= captured.map(_.name)
    case _ => super.walk(i)
  }
  override def walk(r: Ref) = r match {
    case RefProp(RefId(id), expr) => { m += id.name; super.walk(expr) }
    case _ => super.walk(r)
  }
}

trait BasePartialEval[LT <: LabelwiseContext[LT], GT <: GlobalContext[GT]] extends PassingPartialEval[LT, GT] {

  val mustBoundVariable = collection.mutable.Set[String]()
  override def init(view: SyntacticView): Unit = {
    val (algo, _) = view.ast.semantics("Evaluation").get
    MustBoundVariableWalker(mustBoundVariable).walk(algo)
  }

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

  import psm._

  override def pe_ilet: ILet => Result[Option[Inst]] = {
    case ILet(id, expr) => for {
      v <- pe(expr)
      _ <- (context: S) => context.updateLabelwise((u) => u.setId(id.name, v))
    } yield if (v.isRepresentable && !mustBoundVariable.contains(id.name)) None else Some(ILet(id, v.expr))
  }

  override def pe_iassign: IAssign => Result[Option[Inst]] = {
    case IAssign(RefId(id), expr) => for {
      v <- pe(expr)
      _ <- (context: S) => context.updateLabelwise((u) => u.setId(id.name, v))
    } yield if (v.isRepresentable && !mustBoundVariable.contains(id.name)) None else {
      Some(IAssign(RefId(id), v.expr))
    }
    case IAssign(ref, expr) => for {
      refr <- pe(ref)
      expre <- pe(expr)
    } yield { Some(IAssign(refr, expre.expr)) }
  }

  override def pe_iseq: ISeq => Result[Option[Inst]] = {
    case ISeq(insts) => { (dcontext: SpecializeContext[LT, GT]) =>
      {
        val (z, y) = insts.foldLeft((List[Inst](), dcontext)) {
          case ((li, dc), i) => if (dc.labelwiseContext.getRet.map(_.isRepresentable).getOrElse(false)) (li, dc) else {
            val (i2, dc2) = pe(i)(dc)
            (li ++ i2.toList, dc2)
          }
        }
        (Some(ISeq(z)), y)
      }
    }
  }

  override def pe_iif: IIf => Result[Option[Inst]] = {
    case IIf(cond, thenInst, elseInst) => for {
      c <- pe(cond)
      res <- (c match {
        case SymbolicValue(Some(Bool(true)), _) => pe(thenInst)
        case SymbolicValue(Some(Bool(false)), _) => pe(elseInst)
        case _ => (dcontext1: S) => {
          val (thenInstI, dcontext2) = pe(thenInst)(dcontext1)
          val (elseInstI, dcontext3) = pe(elseInst)(SpecializeContextImpl(dcontext1.labelwiseContext, dcontext2.globalContext))
          val (itrue, ifalse, nstate) = dcontext2.labelwiseContext merge dcontext3.labelwiseContext
          (Some(IIf(
            c.expr,
            ISeq(thenInstI.toList ++ itrue),
            ISeq(elseInstI.toList ++ ifalse)
          )), SpecializeContextImpl(nstate, dcontext3.globalContext))
        }
      }): Result[Option[Inst]]
    } yield res
  }

  override def pe_iwhile: IWhile => Result[Option[Inst]] = {
    case IWhile(cond, body) => for {
      c <- pe(cond)
      res <- (c match {
        case SymbolicValue(Some(Bool(false)), _) => None
        case _ => (dcontext1: S) => {
          def aux(dcbase: S, dccurrent: S): S = {
            val (_, dc2) = pe(body)(dccurrent)
            val (_, _, lc) = dcbase.labelwiseContext merge dc2.labelwiseContext
            val dc3 = dc2.updateLabelwise((_) => lc)
            if (dccurrent.labelwiseContext == dc3.labelwiseContext) dc3
            else {
              // println(dccurrent.labelwiseContext)
              // println(dc3.labelwiseContext)
              aux(dcbase, dc3)
            }
          }
          val dcontext2 = aux(dcontext1, dcontext1)
          val (b, dcontext3) = pe(body)(dcontext2)
          val (inotloop, iloop, nlabcont) = dcontext1.labelwiseContext merge dcontext3.labelwiseContext
          assert(nlabcont == dcontext2.labelwiseContext)
          val dcontext4 = SpecializeContextImpl(nlabcont, dcontext3.globalContext)
          val (c2, _) = pe(cond)(dcontext4)
          (Some(ISeq(inotloop :+ IWhile(c2.expr, ISeq(b.toList ++ iloop)))), dcontext4)
        }
      }): Result[Option[Inst]]
    } yield res
  }

  override def pe_enum: ENum => Result[SymbolicValue] = {
    case ENum(n) => SymbolicValueFactory.mkSimple(Num(n))
  }

  override def pe_einum: EINum => Result[SymbolicValue] = {
    case EINum(n) => (SymbolicValueFactory.mkSimple(INum(n)))
  }

  override def pe_ebiginum: EBigINum => Result[SymbolicValue] = {
    case EBigINum(b) => (SymbolicValueFactory.mkSimple(BigINum(b)))
  }

  override def pe_estr: EStr => Result[SymbolicValue] = {
    case EStr(str) => (SymbolicValueFactory.mkSimple(Str(str)))
  }

  override def pe_ebool: EBool => Result[SymbolicValue] = {
    case EBool(b) => (SymbolicValueFactory.mkSimple(Bool(b)))
  }

  override def pe_eundef: EUndef.type => Result[SymbolicValue] = {
    case EUndef => (SymbolicValueFactory.mkSimple(Undef))
  }

  override def pe_enull: ENull.type => Result[SymbolicValue] = {
    case ENull => (SymbolicValueFactory.mkSimple(Null))
  }

  override def pe_eabsent: EAbsent.type => Result[SymbolicValue] = {
    case EAbsent => SymbolicValueFactory.mkSimple(Absent)
  }

  override def pe_eref: ERef => Result[SymbolicValue] = {

    case ERef(ref) => for {
      refr <- pe(ref)
      res <- (refr match {
        case RefId(id) => for {
          context <- get
        } yield context.labelwiseContext.getId(id.name) match {
          case Some(SymbolicValue(Some(ASTVal(ast: AbsAST)), _)) => SymbolicValueFactory.mkDynamic(ERef(refr))
          case Some(v @ SymbolicValue(Some(_), _)) => v
          case Some(_) => SymbolicValueFactory.mkDynamic(ERef(refr))
          case None => if (isPermittedGlobal(id)) Initialize.initGlobal.get(id) match {
            case Some(v: Addr) => SymbolicValueFactory.mkDynamic(ERef(refr))
            case Some(v) => SymbolicValueFactory.mkSExpr(v, ERef(ref))
            case _ => SymbolicValueFactory.mkDynamic(ERef(refr))
          }
          else SymbolicValueFactory.mkDynamic(ERef(refr))
        }
        case _ => SymbolicValueFactory.mkDynamic(ERef(refr))
      }): Result[SymbolicValue]
    } yield res
  }

  override def pe_euop: EUOp => Result[SymbolicValue] = {
    case EUOp(uop, expr) => (for {
      e <- pe(expr)
    } yield SymbolicValueFactory.mkSymbolic(e.valueOption.map((v) => Interp.interp(uop, v)), EUOp(uop, e.expr)))
  }

  override def pe_ebop: EBOp => Result[SymbolicValue] = {
    case EBOp(bop, left, right) => (for {
      le <- pe(left)
      re <- pe(right)
    } yield SymbolicValueFactory.mkSymbolic(le.valueOption.flatMap((lv) => re.valueOption.map((rv) => Interp.interp(bop, lv, rv))), EBOp(bop, le.expr, re.expr)))
  }

  override def pe_etypeof: ETypeOf => Result[SymbolicValue] = {
    case ETypeOf(expr) => for {
      e <- pe(expr)
      v = e.valueOption.flatMap {
        case NormalComp(value) => Some(value)
        case CompValue(_, _, _) => None
        case pure: PureValue => Some(pure)
      } match {
        case Some(value) => value match {
          case Const(_) => SymbolicValueFactory.mkSimple(Str("Constant"))
          case (addr: Addr) => SymbolicValueFactory.mkDynamic(ETypeOf(e.expr))
          case Func(_) => SymbolicValueFactory.mkSimple(Str("Function"))
          case Clo(_, _, _, _) => SymbolicValueFactory.mkSimple(Str("Closure"))
          case Cont(_, _, _) => SymbolicValueFactory.mkSimple(Str("Continuation"))
          case ASTVal(_) => SymbolicValueFactory.mkSimple(Str("AST"))
          case Num(_) | INum(_) => SymbolicValueFactory.mkSimple(Str("Number"))
          case BigINum(_) => SymbolicValueFactory.mkSimple(Str("BigInt"))
          case Str(_) => SymbolicValueFactory.mkSimple(Str("String"))
          case Bool(_) => SymbolicValueFactory.mkSimple(Str("Boolean"))
          case Undef => SymbolicValueFactory.mkSimple(Str("Undefined"))
          case Null => SymbolicValueFactory.mkSimple(Str("Null"))
          case Absent => SymbolicValueFactory.mkSimple(Str("Absent"))
        }
        case None => SymbolicValueFactory.mkDynamic(ETypeOf(e.expr))
      }
    } yield v // TODO

  }

  override def pe_eisinstanceof: EIsInstanceOf => Result[SymbolicValue] = {
    case EIsInstanceOf(base, name) => (for {
      be <- pe(base)
    } yield SymbolicValueFactory.mkSymbolic(be.valueOption.flatMap((bv) => {
      if (bv.isAbruptCompletion) Some(Bool(false))
      else bv.escaped match {
        case ASTVal(ast) => Some(Bool(ast.name == name || ast.getKinds.contains(name)))
        case Str(str) => Some(Bool(str == name))
        case addr: Addr => None
        case _ => Some(Bool(false))
      }
    }), EIsInstanceOf(be.expr, name)))
  }

  override def pe_ereturnifabrupt: EReturnIfAbrupt => Result[SymbolicValue] = {
    case EReturnIfAbrupt(expr, check) => (for {
      ne <- pe(expr)
    } yield SymbolicValueFactory.mkSymbolic(ne.valueOption.flatMap(({
      case NormalComp(value) => value
      case pure: PureValue => pure
    }: PartialFunction[Value, Value]).lift), EReturnIfAbrupt(ne.expr, check)))
  }

}

object BasePartialEvalImpl extends BasePartialEval[SymbolicEnv, EmptyGlobalContext] {
  val lcbuilder = SymbolicEnvBuilder
  val gcbuilder = EmptyGlobalContextBuilder
}
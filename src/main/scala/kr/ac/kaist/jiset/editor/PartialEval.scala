package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.StateMonad
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec
import scala.collection.mutable.{ Map => MMap }
import kr.ac.kaist.jiset.analyzer.domain.FlatBot
import kr.ac.kaist.jiset.analyzer.domain.FlatTop
import kr.ac.kaist.jiset.analyzer.domain.FlatElem

trait Lattice[T] {
  def join(other: T): T
}

trait LatticeBuilder[T <: Lattice[T]] {
  def top: T
  def bottom: T
}

trait AbstractState[VT <: AbstractValue[VT], T] extends Lattice[T] {
  def setVar(s: String, v: VT): T
  def getVar(s: String): VT
}

trait AbstractStateBuilder[T <: Lattice[T]] extends LatticeBuilder[T] {
  def init: T
}

trait AbstractValue[T] extends Lattice[T] {
}

trait AbstractValueBuilder[T <: Lattice[T]] extends LatticeBuilder[T] {
  def fromValue(v: Value): T
  def fromValueAndExpr(v: Value, e: Expr): T
}

case class EnvOnlyAbstraction[T <: AbstractValue[T]](locals: Map[String, T], defaultTop: Boolean, mayContinue: Boolean, ret: T)(implicit builder: LatticeBuilder[T]) extends AbstractState[T, EnvOnlyAbstraction[T]] {
  def join(other: EnvOnlyAbstraction[T]): EnvOnlyAbstraction[T] = {
    val identList = locals.keySet ++ other.locals.keySet
    val nlocals = identList.foldLeft(Map[String, T]()) {
      case (m, s) => m + (s -> ((locals.get(s), other.locals.get(s)) match {
        case (Some(a), Some(b)) => a join b
        case (Some(a), None) => if (other.defaultTop) builder.top else a
        case (None, Some(b)) => if (defaultTop) builder.top else b
        case (None, None) => if (defaultTop || other.defaultTop) builder.top else builder.bottom
      }))
    }
    this.copy(locals = nlocals, defaultTop = defaultTop || other.defaultTop, mayContinue = mayContinue || other.mayContinue, ret = ret join other.ret)
  }
  def toDynamic: EnvOnlyAbstraction[T] = this.copy(locals = locals.map { case (id, v) => (id, builder.top) })

  def setVar(s: String, v: T): EnvOnlyAbstraction[T] = this.copy(locals = locals + (s -> v))
  def getVar(s: String): T = this.locals.getOrElse(s, if (defaultTop) builder.top else builder.bottom)

  def setRet(v: T): EnvOnlyAbstraction[T] = this.copy(mayContinue = false, ret = v)
  def getRet: T = ret

}

case class EnvOnlyAbstractionBuilder[T <: AbstractValue[T]]()(implicit builder: LatticeBuilder[T]) extends AbstractStateBuilder[EnvOnlyAbstraction[T]] {
  def top: EnvOnlyAbstraction[T] = EnvOnlyAbstraction(Map(), true, true, builder.top)
  def init: EnvOnlyAbstraction[T] = EnvOnlyAbstraction(Map(), false, true, builder.bottom)
  def bottom: EnvOnlyAbstraction[T] = EnvOnlyAbstraction(Map(), false, false, builder.bottom)
}

object FlatVEBuilder extends AbstractValueBuilder[FlatVE] {
  def apply(v: Value, e: Expr): FlatVE = FlatVE(FlatElem(v, e))
  def fromValueAndExpr(v: Value, e: Expr): FlatVE = apply(v, e)
  def fromValue(v: Value): FlatVE = v match {
    case v: SimpleValue => simple(v)
    case _ => top
  }
  def simple(v: SimpleValue): FlatVE = v match {
    case Num(double) => FlatVE(FlatElem(v, ENum(double)))
    case INum(long) => FlatVE(FlatElem(v, EINum(long)))
    case BigINum(b) => FlatVE(FlatElem(v, EBigINum(b)))
    case Str(str) => FlatVE(FlatElem(v, EStr(str)))
    case Bool(bool) => FlatVE(FlatElem(v, EBool(bool)))
    case Undef => FlatVE(FlatElem(v, EUndef))
    case Null => FlatVE(FlatElem(v, ENull))
    case Absent => FlatVE(FlatElem(v, EAbsent))
  }
  def top: FlatVE = FlatVE(FlatTop)
  def bottom: FlatVE = FlatVE(FlatBot)
}

case class FlatVE(v: kr.ac.kaist.jiset.analyzer.domain.Flat[(Value, Expr)]) extends AbstractValue[FlatVE] {
  def join(other: FlatVE): FlatVE = FlatVE(v.⊔(other.v))
  def isRepresentable: Boolean = this.v match {
    case FlatBot => false
    case FlatTop => false
    case FlatElem((v, e)) => v.isInstanceOf[SimpleValue]
  }
}

class InsensitiveUseTracker extends UnitWalker {
  val m: collection.mutable.Set[String] = collection.mutable.Set[String]()
  override def walk(inst: Inst): Unit = inst match {
    case IAssign(RefId(id), expr) => walk(expr)
    case IClo(_, _, captured, body) => { m ++ captured.map(_.name); walk(body) }
    case _ => super.walk(inst)
  }

  override def walk(ref: Ref): Unit = ref match {
    case RefId(id) => m.add(id.name)
    case RefProp(_, _) => super.walk(ref)
  }
}

class RemoveUnusedDefWalker(m: Set[String]) extends Walker {
  override def walk(inst: Inst): Inst = inst match {
    case ILet(id, _) => if (m(id.name)) inst else IExpr(EStr("empty"))
    case IAssign(RefId(id), _) => if (m(id.name)) inst else IExpr(EStr("empty"))
    case IClo(id, _, _, _) => if (m(id.name)) inst else IExpr(EStr("empty"))
    case ICont(id, _, _) => if (m(id.name)) inst else IExpr(EStr("empty"))
    case ISeq(insts) => {
      val ninsts = walkList[Inst](insts, walk).flatMap { case ISeq(insts) => insts; case i => List(i) }
      ISeq(ninsts.filter { case IExpr(EStr("empty")) => false; case _ => true })
    }
    case _ => super.walk(inst)
  }
}

// partial evaluator for IR functions with a given syntactic view
trait PartialEval[AS <: AbstractState[FlatVE, AS]] {
  val asbuilder: AbstractStateBuilder[AS]

  val instTransformer: InstTransformer[FlatVE, AS]

  def apply(view: SyntacticView): Algo = {
    val (targetAlgo, asts) = view.ast.semantics("Evaluation").get
    val (nalgo, _) = instTransformer.pe(targetAlgo, asts.map(Some(_))) // TODO: AAST to Dynamic Value
    val iut = new InsensitiveUseTracker
    iut.walk(nalgo.rawBody)
    val rud = new RemoveUnusedDefWalker(iut.m.toSet)
    print(iut.m.toSet)
    new Algo(nalgo.head, nalgo.id, rud.walk(nalgo.rawBody), nalgo.code)
  }

}

trait InstTransformer[VT <: AbstractValue[VT], AS <: AbstractState[VT, AS]] {
  implicit val asbuilder: AbstractStateBuilder[AS]
  implicit val vtbuilder: AbstractValueBuilder[VT]
  type Result[T] = (AS) => (T, AS)

  def pe_iseq: ISeq => Result[Inst]
  def pe_iaccess: IAccess => Result[Inst]
  def pe_iapp: IApp => Result[Inst]
  def pe_iappend: IAppend => Result[Inst]
  def pe_iassert: IAssert => Result[Inst]
  def pe_iassign: IAssign => Result[Inst]
  def pe_iclo: IClo => Result[Inst]
  def pe_icont: ICont => Result[Inst]
  def pe_idelete: IDelete => Result[Inst]
  def pe_iexpr: IExpr => Result[Inst]
  def pe_iif: IIf => Result[Inst]
  def pe_ilet: ILet => Result[Inst]
  def pe_iprepend: IPrepend => Result[Inst]
  def pe_iprint: IPrint => Result[Inst]
  def pe_ireturn: IReturn => Result[Inst]
  def pe_ithrow: IThrow => Result[Inst]
  def pe_iwhile: IWhile => Result[Inst]
  def pe_iwithcont: IWithCont => Result[Inst]

  def getLocals(params: List[Param], args: List[Option[Value]]): MMap[String, VT] = {
    val map = MMap[String, VT]()
    @tailrec
    def aux(ps: List[Param], as: List[Option[Value]]): Unit = (ps, as) match {
      case (Nil, Nil) =>
      case (Param(name, kind) :: pl, Nil) => kind match {
        case Param.Kind.Normal => error(s"remaining parameter: $name")
        case _ => {
          map += name -> vtbuilder.fromValueAndExpr(Absent, ERef(RefId(Id(name))))
          aux(pl, Nil)
        }
      }
      case (Nil, args) => {
        val argsStr = args.mkString("[", ", ", "]")
        error(s"$params, $args: remaining arguments: $argsStr")
      }
      case (param :: pl, arg :: al) => {
        map += param.name -> arg.map((v) => vtbuilder.fromValueAndExpr(v, ERef(RefId(Id(param.name))))).getOrElse(vtbuilder.top)
        aux(pl, al)
      }
    }
    aux(params, args)
    map
  }

  def pe(algo: Algo, args: List[Option[Value]]): (Algo, AS) = {
    // println(algo)
    val locals = getLocals(algo.head.params, args)
    val (newInsts, ncontext) = pe(algo.rawBody)(locals.toList.foldLeft(asbuilder.init) { case (dc, (s, v)) => dc.setVar(s, v) })
    val nalgo = new Algo(algo.head, algo.id, newInsts, algo.code)
    // println(nalgo)
    (nalgo, ncontext) //, ncontext.labelwiseContext.getRet), SpecializeContextImpl(dcontext.labelwiseContext, ncontext.globalContext.setAlgo((algo, args), Some((nalgo, ncontext.labelwiseContext.getRet)))))
  }

  def pe(inst: Inst): Result[Inst] = inst match {
    case inst: ISeq => pe_iseq(inst)
    case inst: IAccess => pe_iaccess(inst)
    case inst: IApp => pe_iapp(inst)
    case inst: IAppend => pe_iappend(inst)
    case inst: IAssert => pe_iassert(inst)
    case inst: IAssign => pe_iassign(inst)
    case inst: IClo => pe_iclo(inst)
    case inst: ICont => pe_icont(inst)
    case inst: IDelete => pe_idelete(inst)
    case inst: IExpr => pe_iexpr(inst)
    case inst: IIf => pe_iif(inst)
    case inst: ILet => pe_ilet(inst)
    case inst: IPrepend => pe_iprepend(inst)
    case inst: IPrint => pe_iprint(inst)
    case inst: IReturn => pe_ireturn(inst)
    case inst: IThrow => pe_ithrow(inst)
    case inst: IWhile => pe_iwhile(inst)
    case inst: IWithCont => pe_iwithcont(inst)
  }
}

trait ExprEvaluator[VT <: AbstractValue[VT], AS <: AbstractState[VT, AS]] {
  implicit val asbuilder: LatticeBuilder[AS]
  implicit val vtbuilder: LatticeBuilder[VT]
  type Result[T] = (AS) => (T, AS)

  def pe_enum: ENum => Result[VT]
  def pe_einum: EINum => Result[VT]
  def pe_ebiginum: EBigINum => Result[VT]
  def pe_estr: EStr => Result[VT]
  def pe_ebool: EBool => Result[VT]
  def pe_eundef: EUndef.type => Result[VT]
  def pe_enull: ENull.type => Result[VT]
  def pe_eabsent: EAbsent.type => Result[VT]
  def pe_econst: EConst => Result[VT]
  def pe_ecomp: EComp => Result[VT]
  def pe_emap: EMap => Result[VT]
  def pe_elist: EList => Result[VT]
  def pe_esymbol: ESymbol => Result[VT]
  def pe_epop: EPop => Result[VT]
  def pe_eref: ERef => Result[VT]
  def pe_euop: EUOp => Result[VT]
  def pe_ebop: EBOp => Result[VT]
  def pe_etypeof: ETypeOf => Result[VT]
  def pe_eiscompletion: EIsCompletion => Result[VT]
  def pe_eisinstanceof: EIsInstanceOf => Result[VT]
  def pe_egetelems: EGetElems => Result[VT]
  def pe_egetsyntax: EGetSyntax => Result[VT]
  def pe_eparsesyntax: EParseSyntax => Result[VT]
  def pe_econvert: EConvert => Result[VT]
  def pe_econtains: EContains => Result[VT]
  def pe_ereturnifabrupt: EReturnIfAbrupt => Result[VT]
  def pe_ecopy: ECopy => Result[VT]
  def pe_ekeys: EKeys => Result[VT]
  def pe_enotsupported: ENotSupported => Result[VT]

  def pe(expr: Expr): Result[VT] = expr match {
    case expr: ENum => pe_enum(expr)
    case expr: EINum => pe_einum(expr)
    case expr: EBigINum => pe_ebiginum(expr)
    case expr: EStr => pe_estr(expr)
    case expr: EBool => pe_ebool(expr)
    case EUndef => pe_eundef(EUndef)
    case ENull => pe_enull(ENull)
    case EAbsent => pe_eabsent(EAbsent)
    case expr: EConst => pe_econst(expr)
    case expr: EComp => pe_ecomp(expr)
    case expr: EMap => pe_emap(expr)
    case expr: EList => pe_elist(expr)
    case expr: ESymbol => pe_esymbol(expr)
    case expr: EPop => pe_epop(expr)
    case expr: ERef => pe_eref(expr)
    case expr: EUOp => pe_euop(expr)
    case expr: EBOp => pe_ebop(expr)
    case expr: ETypeOf => pe_etypeof(expr)
    case expr: EIsCompletion => pe_eiscompletion(expr)
    case expr: EIsInstanceOf => pe_eisinstanceof(expr)
    case expr: EGetElems => pe_egetelems(expr)
    case expr: EGetSyntax => pe_egetsyntax(expr)
    case expr: EParseSyntax => pe_eparsesyntax(expr)
    case expr: EConvert => pe_econvert(expr)
    case expr: EContains => pe_econtains(expr)
    case expr: EReturnIfAbrupt => pe_ereturnifabrupt(expr)
    case expr: ECopy => pe_ecopy(expr)
    case expr: EKeys => pe_ekeys(expr)
    case expr: ENotSupported => pe_enotsupported(expr)
  }

  def pe_refid: RefId => Result[VT]
  def pe_refprop: RefProp => Result[VT]

  def pe(ref: Ref): Result[VT] = ref match {
    case ref: RefId => pe_refid(ref)
    case ref: RefProp => pe_refprop(ref)
  }
}

case class ExprTransformer[AS <: AbstractState[FlatVE, AS]](ev: ExprEvaluator[FlatVE, AS]) {
  val asbuilder: LatticeBuilder[AS] = ev.asbuilder
  val vtbuilder: LatticeBuilder[FlatVE] = ev.vtbuilder
  type Result[T] = (AS) => (T, AS)

  def simpleE(v: SimpleValue): Expr = v match {
    case Num(double) => ENum(double)
    case INum(long) => EINum(long)
    case BigINum(b) => EBigINum(b)
    case Str(str) => EStr(str)
    case Bool(bool) => EBool(bool)
    case Undef => EUndef
    case Null => ENull
    case Absent => EAbsent
  }

  def pe(expr: Expr): Result[(FlatVE, Expr)] = (s: AS) => {
    val (v, s2) = ev.pe(expr)(s)
    val res = v match {
      case FlatVE(FlatElem((v1: SimpleValue, _))) => (v, simpleE(v1))
      case FlatVE(FlatElem((_, e1))) => (v, e1)
      case _ => (v, expr)
    }
    (res, s2)
  }
}
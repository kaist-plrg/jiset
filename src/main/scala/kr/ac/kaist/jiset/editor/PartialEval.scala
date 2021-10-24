package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.StateMonad
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec
import scala.collection.mutable.{ Map => MMap }

trait LabelwiseContext[T] {
  def merge(other: T): ((List[(String, SymbolicValue)], List[(String, SymbolicValue)]), T)
  def setId: (String, SymbolicValue) => T
  def getId: String => Option[SymbolicValue]
  def setRet: Option[SymbolicValue] => T
  def getRet: Option[SymbolicValue]
  def toDynamic: T
}

trait GlobalContext[T] {
  def getAlgo: (Algo, List[Option[Value]]) => Option[Option[(Algo, Option[SymbolicValue])]]
  def visitAlgo: (Algo, List[Option[Value]]) => T
  def setAlgo: ((Algo, List[Option[Value]]), Option[(Algo, Option[SymbolicValue])]) => T
}

trait SpecializeContext[LT <: LabelwiseContext[LT], GT <: GlobalContext[GT]] {
  val labelwiseContext: LT
  val globalContext: GT
  def updateLabelwise(u: LT => LT): SpecializeContext[LT, GT]
  def updateGlobal(u: GT => GT): SpecializeContext[LT, GT]
  def toDynamic: SpecializeContext[LT, GT]
}

case class EmptyGlobalContext() extends GlobalContext[EmptyGlobalContext] {
  def getAlgo: (Algo, List[Option[Value]]) => Option[Option[(Algo, Option[SymbolicValue])]] = (_, _) => None
  def visitAlgo: (Algo, List[Option[Value]]) => EmptyGlobalContext = (_, _) => EmptyGlobalContext()
  def setAlgo: ((Algo, List[Option[Value]]), Option[(Algo, Option[SymbolicValue])]) => EmptyGlobalContext = (_, _) => EmptyGlobalContext()
}

case object EmptyGlobalContextBuilder extends GCBuilder[EmptyGlobalContext] {
  def empty = EmptyGlobalContext()
}


case class FunctionMap(m: Map[(Algo, List[Option[Value]]), Option[(Algo, Option[SymbolicValue])]]) extends GlobalContext[FunctionMap] {
  def getAlgo: (Algo, List[Option[Value]]) => Option[Option[(Algo, Option[SymbolicValue])]] = (k, v) => m.get((k, v))
  def visitAlgo: (Algo, List[Option[Value]]) => FunctionMap = (k, v) => this.copy(m = m + ((k, v) -> None))
  def setAlgo: ((Algo, List[Option[Value]]), Option[(Algo, Option[SymbolicValue])]) => FunctionMap = (k, v) => this.copy(m = m + (k -> v))
}

case object FunctionMapBuilder extends GCBuilder[FunctionMap] {
  def empty = FunctionMap(Map())
}

case class SpecializeContextImpl[LT <: LabelwiseContext[LT], GT <: GlobalContext[GT]](val labelwiseContext: LT, val globalContext: GT) extends SpecializeContext[LT, GT] {
  def updateLabelwise(u: LT => LT): SpecializeContextImpl[LT, GT] = copy(labelwiseContext = u(labelwiseContext))
  def updateGlobal(u: GT => GT): SpecializeContextImpl[LT, GT] = copy(globalContext = u(globalContext))
  def toDynamic: SpecializeContextImpl[LT, GT] = SpecializeContextImpl(labelwiseContext.toDynamic, globalContext)
}

case class SymbolicEnv(locals: Map[String, SymbolicValue], ret: Option[SymbolicValue]) extends LabelwiseContext[SymbolicEnv] {
  def merge(other: SymbolicEnv): ((List[(String, SymbolicValue)], List[(String, SymbolicValue)]), SymbolicEnv) = {
    if (this.ret == other.ret && (this.ret match { case Some(SymbolicValue(Some(_), _)) => true; case _ => false })) {
      ((List(), List()), this)
    } else {
      ((List(), List()), this.copy(ret = None))
    }
  }
  def toDynamic: SymbolicEnv = this.copy(locals = locals.map { case (id, v) => (id, SymbolicValueFactory.mkDynamic(ERef(RefId(Id(id))))) }, ret = None)

  def getRet = ret
  def setRet: Option[SymbolicValue] => SymbolicEnv = (ret) => this.copy(ret = ret)
  def setId: (String, SymbolicValue) => SymbolicEnv = (s, v) => this.copy(locals = locals + (s -> v))
  def getId: String => Option[SymbolicValue] = this.locals.get

}

case object SymbolicEnvBuilder extends LCBuilder[SymbolicEnv] {
  def empty = SymbolicEnv(Map(), None)
  def init(m: Map[String,SymbolicValue]): SymbolicEnv = SymbolicEnv(m, None)
}

object SymbolicValueFactory {
  def mkSymbolic(v: Option[Value], e: Expr) = v match {
    case Some(v: SimpleValue) => mkSimple(v)
    case _ => SymbolicValue(v, e)
  }
  def mkSExpr(v: Value, e: Expr): SymbolicValue = v match {
    case v: SimpleValue => mkSimple(v)
    case _ => SymbolicValue(Some(v), e)
  }
  def mkSimple(v: SimpleValue): SymbolicValue = {
    val e = v match {
      case Num(double) => ENum(double)
      case INum(long) => EINum(long)
      case BigINum(b) => EBigINum(b)
      case Str(str) => EStr(str)
      case Bool(bool) => EBool(bool)
      case Undef => EUndef
      case Null => ENull
      case Absent => EAbsent
    }
    SymbolicValue(Some(v), e)
  }
  def mkDynamic(e: Expr): SymbolicValue = SymbolicValue(None, e)
}

case class SymbolicValue(valueOption: Option[Value], expr: Expr) {
  def isRepresentable: Boolean = valueOption.map(_.isInstanceOf[SimpleValue]).getOrElse(false)
}

case class SymbolicEnvMonad[LT <: LabelwiseContext[LT], GT <: GlobalContext[GT], ST <: SpecializeContext[LT, GT]]() extends StateMonad[ST] {
  type S = ST
}

trait LCBuilder[LT <: LabelwiseContext[LT]] {
  def empty: LT
  def init(m: Map[String, SymbolicValue]): LT
}

trait GCBuilder[GT <: GlobalContext[GT]] {
  def empty: GT
}

// partial evaluator for IR functions with a given syntactic view
trait PartialEval[LT <: LabelwiseContext[LT], GT <: GlobalContext[GT]] {
  val lcbuilder: LCBuilder[LT]
  val gcbuilder: GCBuilder[GT]
  
  val psm = SymbolicEnvMonad[LT, GT, SpecializeContext[LT, GT]]()
  import psm._

  def getLocals(params: List[Param], args: List[Option[Value]]): MMap[String, SymbolicValue] = {
    val map = MMap[String, SymbolicValue]()
    @tailrec
    def aux(ps: List[Param], as: List[Option[Value]]): Unit = (ps, as) match {
      case (Nil, Nil) =>
      case (Param(name, kind) :: pl, Nil) => kind match {
        case Param.Kind.Normal => error(s"remaining parameter: $name")
        case _ => {
          map += name -> SymbolicValueFactory.mkSExpr(Absent, ERef(RefId(Id(name))))
          aux(pl, Nil)
        }
      }
      case (Nil, args) => {
        val argsStr = args.mkString("[", ", ", "]")
        error(s"$params, $args: remaining arguments: $argsStr")
      }
      case (param :: pl, arg :: al) => {
        map += param.name -> arg.map((v) => SymbolicValueFactory.mkSExpr(v, ERef(RefId(Id(param.name))))).getOrElse(SymbolicValueFactory.mkDynamic(ERef(RefId(Id(param.name)))))
        aux(pl, al)
      }
    }
    aux(params, args)
    map
  }

  def apply(view: SyntacticView): Algo = {
    val (targetAlgo, asts) = view.ast.semantics("Evaluation").get
    val ((nalgo, _), _) = pe(targetAlgo, asts.map(Some(_)))(SpecializeContextImpl(lcbuilder.empty, gcbuilder.empty)) // TODO: AAST to Dynamic Value
    nalgo
  }

  def pe(algo: Algo, args: List[Option[Value]]): Result[(Algo, Option[SymbolicValue])] = (dcontext: SpecializeContext[LT, GT]) => {
    dcontext.globalContext.getAlgo(algo, args) match {
      case None => {
        println(algo.head.name)
        val locals = getLocals(algo.head.params, args)
        val (newInsts, ncontext) = pe(algo.rawBody)(SpecializeContextImpl(lcbuilder.init(locals.toMap), dcontext.globalContext.setAlgo((algo, args), None)))
        val nalgo = new Algo(algo.head, algo.id, newInsts, algo.code)
        println(nalgo)
        ((nalgo, ncontext.labelwiseContext.getRet), SpecializeContextImpl(dcontext.labelwiseContext, ncontext.globalContext.setAlgo((algo, args), Some((nalgo, ncontext.labelwiseContext.getRet)))))
      }
      case Some(None) => ((algo, None), dcontext)
      case Some(Some((a, p))) => ((a, p), dcontext)
    }
  }

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

  def pe_enum: ENum => Result[SymbolicValue]
  def pe_einum: EINum => Result[SymbolicValue]
  def pe_ebiginum: EBigINum => Result[SymbolicValue]
  def pe_estr: EStr => Result[SymbolicValue]
  def pe_ebool: EBool => Result[SymbolicValue]
  def pe_eundef: EUndef.type => Result[SymbolicValue]
  def pe_enull: ENull.type => Result[SymbolicValue]
  def pe_eabsent: EAbsent.type => Result[SymbolicValue]
  def pe_econst: EConst => Result[SymbolicValue]
  def pe_ecomp: EComp => Result[SymbolicValue]
  def pe_emap: EMap => Result[SymbolicValue]
  def pe_elist: EList => Result[SymbolicValue]
  def pe_esymbol: ESymbol => Result[SymbolicValue]
  def pe_epop: EPop => Result[SymbolicValue]
  def pe_eref: ERef => Result[SymbolicValue]
  def pe_euop: EUOp => Result[SymbolicValue]
  def pe_ebop: EBOp => Result[SymbolicValue]
  def pe_etypeof: ETypeOf => Result[SymbolicValue]
  def pe_eiscompletion: EIsCompletion => Result[SymbolicValue]
  def pe_eisinstanceof: EIsInstanceOf => Result[SymbolicValue]
  def pe_egetelems: EGetElems => Result[SymbolicValue]
  def pe_egetsyntax: EGetSyntax => Result[SymbolicValue]
  def pe_eparsesyntax: EParseSyntax => Result[SymbolicValue]
  def pe_econvert: EConvert => Result[SymbolicValue]
  def pe_econtains: EContains => Result[SymbolicValue]
  def pe_ereturnifabrupt: EReturnIfAbrupt => Result[SymbolicValue]
  def pe_ecopy: ECopy => Result[SymbolicValue]
  def pe_ekeys: EKeys => Result[SymbolicValue]
  def pe_enotsupported: ENotSupported => Result[SymbolicValue]

  def pe(expr: Expr): Result[SymbolicValue] = expr match {
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

  def pe_refid: RefId => Result[Ref]
  def pe_refprop: RefProp => Result[Ref]

  def pe(ref: Ref): Result[Ref] = ref match {
    case ref: RefId => pe_refid(ref)
    case ref: RefProp => pe_refprop(ref)
  }

}

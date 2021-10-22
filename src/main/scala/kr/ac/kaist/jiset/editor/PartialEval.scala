package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.StateMonad
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec
import scala.collection.mutable.{ Map => MMap }

trait LabelwiseContext {
  def merge(other: LabelwiseContext): ((List[(String, PartialValue)], List[(String, PartialValue)]), LabelwiseContext)
  def setId: (String, PartialValue) => LabelwiseContext
  def getId: String => Option[PartialValue]
  def getRet: Option[PartialValue]
  def setRet: Option[PartialValue] => LabelwiseContext
  def toDynamic: LabelwiseContext
}

trait GlobalContext {
  def getAlgo: (Algo, List[Option[Value]]) => Option[Option[(Algo, Option[PartialValue])]]
  def visitAlgo: (Algo, List[Option[Value]]) => GlobalContext
  def setAlgo: ((Algo, List[Option[Value]]), Option[(Algo, Option[PartialValue])]) => GlobalContext
}

trait PartialContext {
  val labelwiseContext: LabelwiseContext
  val globalContext: GlobalContext
  def updateLabelwise(u: LabelwiseContext => LabelwiseContext): PartialContext
  def updateGlobal(u: GlobalContext => GlobalContext): PartialContext
  def toDynamic: PartialContext
}

case class FunctionMap(m: Map[(Algo, List[Option[Value]]), Option[(Algo, Option[PartialValue])]]) extends GlobalContext {
  def getAlgo: (Algo, List[Option[Value]]) => Option[Option[(Algo, Option[PartialValue])]] = (k, v) => m.get((k, v))
  def visitAlgo: (Algo, List[Option[Value]]) => GlobalContext = (k, v) => this.copy(m = m + ((k, v) -> None))
  def setAlgo: ((Algo, List[Option[Value]]), Option[(Algo, Option[PartialValue])]) => GlobalContext = (k, v) => this.copy(m = m + (k -> v))
}

case class PartialContextImpl(val labelwiseContext: LabelwiseContext, val globalContext: GlobalContext) extends PartialContext {
  def updateLabelwise(u: LabelwiseContext => LabelwiseContext): PartialContext = copy(labelwiseContext = u(labelwiseContext))
  def updateGlobal(u: GlobalContext => GlobalContext): PartialContext = copy(globalContext = u(globalContext))
  def toDynamic: PartialContextImpl = PartialContextImpl(labelwiseContext.toDynamic, globalContext)
}

case class PartialState(locals: Map[String, PartialValue], ret: Option[PartialValue]) extends LabelwiseContext {
  def merge(other: LabelwiseContext): ((List[(String, PartialValue)], List[(String, PartialValue)]), PartialState) = {
    if (!other.isInstanceOf[PartialState]) error("A")
    if (this.ret == other.asInstanceOf[PartialState].ret && (this.ret match { case Some(PartialValue(Some(_), _)) => true; case _ => false })) {
      ((List(), List()), this)
    } else {
      ((List(), List()), this.copy(ret = None))
    }
  }
  def toDynamic: LabelwiseContext = this.copy(locals = locals.map { case (id, v) => (id, PartialExpr.mkDynamic(ERef(RefId(Id(id))))) }, ret = None)

  def getRet = ret
  def setRet: Option[PartialValue] => LabelwiseContext = (ret) => this.copy(ret = ret)
  def setId: (String, PartialValue) => LabelwiseContext = (s, v) => this.copy(locals = locals + (s -> v))
  def getId: String => Option[PartialValue] = this.locals.get

}

object PartialExpr {
  def mkPValue(v: Option[Value], e: Expr) = v match {
    case Some(v: SimpleValue) => mkSimple(v)
    case _ => PartialValue(v, e)
  }
  def mkSExpr(v: Value, e: Expr): PartialValue = v match {
    case v: SimpleValue => mkSimple(v)
    case _ => PartialValue(Some(v), e)
  }
  def mkSimple(v: SimpleValue): PartialValue = {
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
    PartialValue(Some(v), e)
  }
  def mkDynamic(e: Expr): PartialValue = PartialValue(None, e)
}

case class PartialValue(valueOption: Option[Value], expr: Expr) {
  def isRepresentable: Boolean = valueOption.map(_.isInstanceOf[SimpleValue]).getOrElse(false)
}

object PartialStateMonad extends StateMonad[PartialContext] {
  type S = PartialContext
}

// partial evaluator for IR functions with a given syntactic view
trait PartialEval {

  import PartialStateMonad._

  def getLocals(params: List[Param], args: List[Option[Value]]): MMap[String, PartialValue] = {
    val map = MMap[String, PartialValue]()
    @tailrec
    def aux(ps: List[Param], as: List[Option[Value]]): Unit = (ps, as) match {
      case (Nil, Nil) =>
      case (Param(name, kind) :: pl, Nil) => kind match {
        case Param.Kind.Normal => error(s"remaining parameter: $name")
        case _ => {
          map += name -> PartialExpr.mkSExpr(Absent, ERef(RefId(Id(name))))
          aux(pl, Nil)
        }
      }
      case (Nil, args) => {
        val argsStr = args.mkString("[", ", ", "]")
        error(s"$params, $args: remaining arguments: $argsStr")
      }
      case (param :: pl, arg :: al) => {
        map += param.name -> arg.map((v) => PartialExpr.mkSExpr(v, ERef(RefId(Id(param.name))))).getOrElse(PartialExpr.mkDynamic(ERef(RefId(Id(param.name)))))
        aux(pl, al)
      }
    }
    aux(params, args)
    map
  }

  def apply(view: SyntacticView): Algo = {
    val (targetAlgo, asts) = view.ast.semantics("Evaluation").get
    val ((nalgo, _), _) = pe(targetAlgo, asts.map(Some(_)))(PartialContextImpl(PartialState(Map(), None), FunctionMap(Map()))) // TODO: AAST to Dynamic Value
    nalgo
  }

  def pe(algo: Algo, args: List[Option[Value]]): PartialStateMonad.Result[(Algo, Option[PartialValue])] = (dcontext: PartialContext) => {
    dcontext.globalContext.getAlgo(algo, args) match {
      case None => {
        println(algo.head.name)
        val locals = getLocals(algo.head.params, args)
        val (newInsts, ncontext) = pe(algo.rawBody)(PartialContextImpl(PartialState(locals.toMap, None), dcontext.globalContext.setAlgo((algo, args), None)))
        val nalgo = new Algo(algo.head, algo.id, newInsts, algo.code)
        println(nalgo)
        ((nalgo, ncontext.labelwiseContext.getRet), PartialContextImpl(dcontext.labelwiseContext, ncontext.globalContext.setAlgo((algo, args), Some((nalgo, ncontext.labelwiseContext.getRet)))))
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

  def pe(inst: Inst): PartialStateMonad.Result[Inst] = inst match {
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

  def pe_enum: ENum => Result[PartialValue]
  def pe_einum: EINum => Result[PartialValue]
  def pe_ebiginum: EBigINum => Result[PartialValue]
  def pe_estr: EStr => Result[PartialValue]
  def pe_ebool: EBool => Result[PartialValue]
  def pe_eundef: EUndef.type => Result[PartialValue]
  def pe_enull: ENull.type => Result[PartialValue]
  def pe_eabsent: EAbsent.type => Result[PartialValue]
  def pe_econst: EConst => Result[PartialValue]
  def pe_ecomp: EComp => Result[PartialValue]
  def pe_emap: EMap => Result[PartialValue]
  def pe_elist: EList => Result[PartialValue]
  def pe_esymbol: ESymbol => Result[PartialValue]
  def pe_epop: EPop => Result[PartialValue]
  def pe_eref: ERef => Result[PartialValue]
  def pe_euop: EUOp => Result[PartialValue]
  def pe_ebop: EBOp => Result[PartialValue]
  def pe_etypeof: ETypeOf => Result[PartialValue]
  def pe_eiscompletion: EIsCompletion => Result[PartialValue]
  def pe_eisinstanceof: EIsInstanceOf => Result[PartialValue]
  def pe_egetelems: EGetElems => Result[PartialValue]
  def pe_egetsyntax: EGetSyntax => Result[PartialValue]
  def pe_eparsesyntax: EParseSyntax => Result[PartialValue]
  def pe_econvert: EConvert => Result[PartialValue]
  def pe_econtains: EContains => Result[PartialValue]
  def pe_ereturnifabrupt: EReturnIfAbrupt => Result[PartialValue]
  def pe_ecopy: ECopy => Result[PartialValue]
  def pe_ekeys: EKeys => Result[PartialValue]
  def pe_enotsupported: ENotSupported => Result[PartialValue]

  def pe(expr: Expr): PartialStateMonad.Result[PartialValue] = expr match {
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

  def pe(ref: Ref): PartialStateMonad.Result[Ref] = ref match {
    case ref: RefId => pe_refid(ref)
    case ref: RefProp => pe_refprop(ref)
  }

}

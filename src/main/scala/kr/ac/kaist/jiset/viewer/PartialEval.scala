package kr.ac.kaist.jiset.viewer

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.StateMonad
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec
import scala.collection.mutable.{ Map => MMap }
import kr.ac.kaist.jiset.js.ast.AbsAST
import scala.util.DynamicVariable
import kr.ac.kaist.jiset.js.ast.Lexical
import kr.ac.kaist.jiset.js.Initialize

case class PartialContext(state: PartialState, fmap: Map[(Algo, List[Option[Value]]), Option[(Algo, Option[PartialValue])]]) {
  def toDynamic: PartialContext = this.copy(state = state.copy(locals = state.locals.map { case (id, v) => (id, PartialExpr.mkDynamic(ERef(RefId(Id(id))))) }, ret = None))
}

case class PartialState(locals: Map[String, PartialValue], ret: Option[PartialValue]) {
  def merge(other: PartialState): ((List[(String, PartialValue)], List[(String, PartialValue)]), PartialState) = {
    if (this.ret == other.ret && (this.ret match { case Some(PartialValue(Some(_), _)) => true; case _ => false })) {
      ((List(), List()), this)
    } else {
      ((List(), List()), this.copy(ret = None))
    }
  }
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
    val ((nalgo, _), _) = pe(targetAlgo, asts.map(Some(_)))(PartialContext(PartialState(Map(), None), Map())) // TODO: AAST to Dynamic Value
    nalgo
  }

  def pe(algo: Algo, args: List[Option[Value]]): PartialStateMonad.Result[(Algo, Option[PartialValue])] = (dcontext: PartialContext) => {
    dcontext.fmap.get((algo, args)) match {
      case None => {
        println(algo.head.name)
        val locals = getLocals(algo.head.params, args)
        val (newInsts, ncontext) = pe(algo.rawBody)(PartialContext(PartialState(locals.toMap, None), dcontext.fmap + ((algo, args) -> None)))
        val nalgo = new Algo(algo.head, algo.id, newInsts, algo.code)
        println(nalgo)
        ((nalgo, ncontext.state.ret), PartialContext(dcontext.state, ncontext.fmap + ((algo, args) -> Some((nalgo, ncontext.state.ret)))))
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

object BasePartialEval extends PartialEval {
  import PartialStateMonad._

  def pe_iseq: ISeq => Result[Inst] = {
    case ISeq(insts) => { (dcontext: PartialContext) =>
      {
        val (z, y) = insts.foldLeft((List[Inst](), dcontext)) {
          case ((li, dc), i) => if (dc.state.ret.map(_.isRepresentable).getOrElse(false)) (li, dc) else {
            val (i2, dc2) = pe(i)(dc)
            (li :+ i2, dc2)
          }
        }
        (ISeq(z), y)
      }
    }
  }
  def pe_iaccess: IAccess => Result[Inst] = {
    case IAccess(id, bexpr, expr, args) => (for {
      baseE <- pe(bexpr)
      propE <- pe(expr)
      argsE <- PartialStateMonad.join(args.map(pe))
      res <- ((baseE, propE) match {
        case (PartialValue(Some(b), _), PartialValue(Some(p), _)) =>
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
                  _ <- (context: S) => context.copy(state = context.state.copy(
                    locals = context.state.locals + (id.name -> PartialExpr.mkSimple(Interp.getLexicalValue(kind, name, str)))
                  ))
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
                          _ <- (context: S) => context.copy(state = context.state.copy(
                            locals = context.state.locals + (id.name -> v)
                          ))
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
  def pe_iapp: IApp => Result[Inst] = {
    case IApp(id, ERef(RefId(Id(name))), args) if simpleFuncs contains name => for {
      argse <- join(args.map(pe))
    } yield IApp(id, ERef(RefId(Id(name))), argse.map(_.expr))
    case IApp(id, fexpr, args) => (for {
      fpe <- pe(fexpr)
      argse <- join(args.map(pe))
      res <- (fpe match {
        case PartialValue(Some(Func(algo)), _) => for {
          pres <- pe(algo, argse.map(_.valueOption))
          (_, rpv) = pres // TODO: test whether algorithm does not contains instruction causes side-effect
          res <- (rpv match {
            case Some(v) if v.isRepresentable => for {
              _ <- (context: S) => context.copy(state = context.state.copy(
                locals = context.state.locals + (id.name -> v)
              ))
            } yield IApp(id, fpe.expr, argse.map(_.expr)) // IExpr(EStr("skip"))
            case _ =>
              IApp(id, fpe.expr, argse.map(_.expr))
          }): Result[Inst]
        } yield res
        case _ => IApp(id, fpe.expr, argse.map(_.expr))
      }): Result[Inst]
    } yield res)
  }
  def pe_iappend: IAppend => Result[Inst] = {
    case IAppend(expr, list) => for {
      expre <- pe(expr)
      liste <- pe(list)
    } yield IAppend(expre.expr, liste.expr)
  }
  def pe_iassert: IAssert => Result[Inst] = {
    case IAssert(expr) => for {
      expre <- pe(expr)
    } yield IAssert(expre.expr)
  }
  def pe_iassign: IAssign => Result[Inst] = {
    case IAssign(ref, expr) => for {
      refr <- pe(ref)
      expre <- pe(expr)
    } yield { IAssign(refr, expre.expr) } // TODO
  }
  def pe_iclo: IClo => Result[Inst] = {
    case IClo(id, params, captured, body) => for {
      _ <- (context: S) => context.copy(state = context.state.copy(locals = context.state.locals + (id.name -> PartialExpr.mkDynamic(ERef(RefId(id))))))
    } yield IClo(id, params, captured, body)
  }
  def pe_icont: ICont => Result[Inst] = {
    case ICont(id, params, body) => (context: S) => {
      val (nbody, _) = pe(body)(context.copy(state = context.state.copy(locals = context.state.locals ++ params.map((id) => (id.name -> PartialExpr.mkDynamic(ERef(RefId(id))))))))
      (ICont(id, params, nbody), context.copy(context.state.copy(locals = context.state.locals + (id.name -> PartialExpr.mkDynamic(ERef(RefId(id)))))))
    }
  }
  def pe_idelete: IDelete => Result[Inst] = {
    case IDelete(ref) => for {
      refr <- pe(ref)
    } yield IDelete(refr)
  }
  def pe_iexpr: IExpr => Result[Inst] = {
    case IExpr(expr) => for {
      e <- pe(expr)
    } yield IExpr(e.expr)
  }

  def pe_iif: IIf => Result[Inst] = {
    case IIf(cond, thenInst, elseInst) => for {
      c <- pe(cond)
      res <- (c match {
        case PartialValue(Some(Bool(true)), _) => pe(thenInst)
        case PartialValue(Some(Bool(false)), _) => pe(elseInst)
        case _ => (dcontext1: S) => {
          val (thenInstI, PartialContext(nstate1, fmap1)) = pe(thenInst)(dcontext1)
          val (elseInstI, PartialContext(nstate2, fmap2)) = pe(elseInst)(PartialContext(dcontext1.state, fmap1))
          val ((eif, efalse), nstate) = nstate1 merge nstate2
          (IIf(
            c.expr,
            ISeq(eif.map { case (x, v) => ILet(Id(x), v.expr) } :+ thenInstI),
            ISeq(efalse.map { case (x, v) => ILet(Id(x), v.expr) } :+ elseInstI)
          ), PartialContext(nstate, fmap2))
        }
      }): Result[Inst]
    } yield res
  }

  def pe_ilet: ILet => Result[Inst] = {
    case ILet(id, expr) => for {
      v <- pe(expr)
      _ <- (context: S) => context.copy(state = context.state.copy(locals = context.state.locals + (id.name -> v)))
    } yield ILet(id, v.expr) // if (v.isRepresentable) IExpr(EStr("skip")) else ILet(id, v.expr)
  }

  def pe_iprepend: IPrepend => Result[Inst] = {
    case IPrepend(expr, list) => for {
      expre <- pe(expr)
      liste <- pe(list)
    } yield IPrepend(expre.expr, liste.expr)
  }

  def pe_iprint: IPrint => Result[Inst] = {
    case IPrint(expr) => for {
      expre <- pe(expr)
    } yield IPrint(expre.expr)
  }

  def pe_ireturn: IReturn => Result[Inst] = {
    case IReturn(expr) => for {
      ne <- pe(expr)
      _ <- (context: S) => context.copy(state = context.state.copy(ret = Some(ne)))
    } yield IReturn(ne.expr)

  }

  def pe_ithrow: IThrow => Result[Inst] = {
    case IThrow(name) => IThrow(name)
  }

  def pe_iwhile: IWhile => Result[Inst] = {
    case IWhile(cond, body) => for {
      _ <- (context: S) => context.toDynamic
    } yield IWhile(cond, body) // TODO
  }

  def pe_iwithcont: IWithCont => Result[Inst] = {
    case IWithCont(id, params, body) => (context: S) => {
      val (nbody, _) = pe(body)(context.copy(state = context.state.copy(locals = context.state.locals + (id.name -> PartialExpr.mkDynamic(ERef(RefId(id)))))))
      (IWithCont(id, params, nbody), context.copy(state = context.state.copy(locals = context.state.locals ++ params.map((id) => (id.name -> PartialExpr.mkDynamic(ERef(RefId(id))))))))
    }

  }

  def pe_enum: ENum => Result[PartialValue] = {
    case ENum(n) => PartialExpr.mkSimple(Num(n))
  }

  def pe_einum: EINum => Result[PartialValue] = {
    case EINum(n) => (PartialExpr.mkSimple(INum(n)))
  }

  def pe_ebiginum: EBigINum => Result[PartialValue] = {
    case EBigINum(b) => (PartialExpr.mkSimple(BigINum(b)))
  }

  def pe_estr: EStr => Result[PartialValue] = {
    case EStr(str) => (PartialExpr.mkSimple(Str(str)))
  }

  def pe_ebool: EBool => Result[PartialValue] = {
    case EBool(b) => (PartialExpr.mkSimple(Bool(b)))
  }

  def pe_eundef: EUndef.type => Result[PartialValue] = {
    case EUndef => (PartialExpr.mkSimple(Undef))
  }

  def pe_enull: ENull.type => Result[PartialValue] = {
    case ENull => (PartialExpr.mkSimple(Null))
  }

  def pe_eabsent: EAbsent.type => Result[PartialValue] = {
    case EAbsent => PartialExpr.mkSimple(Absent)
  }

  def pe_econst: EConst => Result[PartialValue] = {
    case EConst(name) => PartialExpr.mkDynamic(EConst(name)) // TODO
  }

  def pe_ecomp: EComp => Result[PartialValue] = {
    case EComp(ty, value, target) => for {
      tye <- pe(ty)
      ve <- pe(value)
      targete <- pe(target)
    } yield PartialExpr.mkDynamic(EComp(tye.expr, ve.expr, targete.expr))
  }

  def pe_emap: EMap => Result[PartialValue] = {
    case EMap(ty, props) => for {
      propse <- PartialStateMonad.join(props.map { case (ek, ev) => pe(ek).flatMap((vk) => pe(ev).flatMap((vv) => (vk, vv))) })
    } yield PartialExpr.mkDynamic(EMap(ty, propse.map { case (ek, ev) => (ek.expr, ev.expr) }))
  }

  def pe_elist: EList => Result[PartialValue] = {

    case EList(exprs) => for {
      le <- PartialStateMonad.join(exprs.map(pe))
    } yield (PartialExpr.mkDynamic(EList(le.map(_.expr))))
  }

  def pe_esymbol: ESymbol => Result[PartialValue] = {

    case ESymbol(desc) => for {
      desce <- pe(desc)
    } yield (PartialExpr.mkDynamic(ESymbol(desce.expr)))
  }

  def pe_epop: EPop => Result[PartialValue] = {
    case EPop(list, idx) => for {
      liste <- pe(list)
      idxe <- pe(idx)
    } yield (PartialExpr.mkDynamic(EPop(liste.expr, idxe.expr))) // TODO
  }

  def pe_eref: ERef => Result[PartialValue] = {

    case ERef(ref) => for {
      refr <- pe(ref)
      res <- (refr match {
        case RefId(id) => for {
          context <- PartialStateMonad.get
        } yield context.state.locals.get(id.name) match {
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

  def pe_euop: EUOp => Result[PartialValue] = {
    case EUOp(uop, expr) => (for {
      e <- pe(expr)
    } yield PartialExpr.mkPValue(e.valueOption.map((v) => Interp.interp(uop, v)), EUOp(uop, e.expr)))
  }

  def pe_ebop: EBOp => Result[PartialValue] = {
    case EBOp(bop, left, right) => (for {
      le <- pe(left)
      re <- pe(right)
    } yield PartialExpr.mkPValue(le.valueOption.flatMap((lv) => re.valueOption.map((rv) => Interp.interp(bop, lv, rv))), EBOp(bop, le.expr, re.expr)))
  }

  def pe_etypeof: ETypeOf => Result[PartialValue] = {
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

  def pe_eiscompletion: EIsCompletion => Result[PartialValue] = {
    case EIsCompletion(expr) => (for {
      e <- pe(expr)
    } yield PartialExpr.mkPValue(None, EIsCompletion(e.expr))) // TODO

  }

  def pe_eisinstanceof: EIsInstanceOf => Result[PartialValue] = {
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

  def pe_egetelems: EGetElems => Result[PartialValue] = {
    case EGetElems(base, name) => for {
      basee <- pe(base)
    } yield (PartialExpr.mkDynamic(EGetElems(basee.expr, name))) // TODO

  }

  def pe_egetsyntax: EGetSyntax => Result[PartialValue] = {
    case EGetSyntax(base) => for {
      basee <- pe(base)
    } yield (PartialExpr.mkDynamic(EGetSyntax(basee.expr))) // TODO

  }

  def pe_eparsesyntax: EParseSyntax => Result[PartialValue] = {
    case EParseSyntax(code, rule, parserParams) => for {
      codee <- pe(code)
      rulee <- pe(rule)
    } yield (PartialExpr.mkDynamic(EParseSyntax(codee.expr, rulee.expr, parserParams))) // TODO

  }

  def pe_econvert: EConvert => Result[PartialValue] = {
    case EConvert(source, target, flags) => for {
      sourcee <- pe(source)
      flagse <- PartialStateMonad.join(flags.map(pe))
    } yield (PartialExpr.mkDynamic(EConvert(sourcee.expr, target, flagse.map(_.expr)))) // TODO

  }

  def pe_econtains: EContains => Result[PartialValue] = {
    case EContains(list, elem) => for {
      liste <- pe(list)
      eleme <- pe(elem)
    } yield (PartialExpr.mkDynamic(EContains(liste.expr, eleme.expr))) // TODO

  }

  def pe_ereturnifabrupt: EReturnIfAbrupt => Result[PartialValue] = {
    case EReturnIfAbrupt(expr, check) => (for {
      ne <- pe(expr)
    } yield PartialExpr.mkPValue(ne.valueOption.flatMap(({
      case NormalComp(value) => value
      case pure: PureValue => pure
    }: PartialFunction[Value, Value]).lift), EReturnIfAbrupt(ne.expr, check)))
  }
  def pe_ecopy: ECopy => Result[PartialValue] = {
    case ECopy(obj) => for {
      obje <- pe(obj)
    } yield (PartialExpr.mkDynamic(ECopy(obje.expr))) // TODO

  }

  def pe_ekeys: EKeys => Result[PartialValue] = {
    case EKeys(mobj, intSorted) => for {
      mobje <- pe(mobj)
    } yield (PartialExpr.mkDynamic(EKeys(mobje.expr, intSorted))) // TODO

  }

  def pe_enotsupported: ENotSupported => Result[PartialValue] = {
    case ENotSupported(msg) => (PartialExpr.mkDynamic(ENotSupported(msg))) // TODO
  }


  def pe_refid: RefId => Result[Ref] = {
    case RefId(id) => RefId(id)
  }
  def pe_refprop: RefProp => Result[Ref] = {
    case RefProp(ref, expr) => for {
      refr <- pe(ref)
      expre <- pe(expr)
    } yield RefProp(refr, expre.expr)
  }
}

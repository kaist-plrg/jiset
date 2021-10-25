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

trait PassingPartialEval[LT <: LabelwiseContext[LT], GT <: GlobalContext[GT]] extends PartialEval[LT, GT] {
  import psm._

  def pe_iseq: ISeq => Result[Option[Inst]] = {
    case ISeq(insts) => join(insts.map(pe)).map((l) => Some(ISeq(l.flatten)))
  }
  def pe_iaccess: IAccess => Result[Option[Inst]] = {
    case IAccess(id, bexpr, expr, args) => for {
      baseE <- pe(bexpr)
      propE <- pe(expr)
      argsE <- join(args.map(pe))
      _ <- (context: S) => context.labelwiseContext.setId(id.name, SymbolicValueFactory.mkDynamic(ERef(RefId(id))))
    } yield Some(IAccess(id, baseE.expr, propE.expr, argsE.map(_.expr)))
  }
  def pe_iapp: IApp => Result[Option[Inst]] = {
    case IApp(id, fexpr, args) => for {
      fexpr <- pe(fexpr)
      argsE <- join(args.map(pe))
      _ <- (context: S) => context.updateLabelwise((u) => u.setId(id.name, SymbolicValueFactory.mkDynamic(ERef(RefId(id)))))
    } yield Some(IApp(id, fexpr.expr, argsE.map(_.expr)))
  }
  def pe_iappend: IAppend => Result[Option[Inst]] = {
    case IAppend(expr, list) => for {
      expre <- pe(expr)
      liste <- pe(list)
    } yield Some(IAppend(expre.expr, liste.expr))
  }
  def pe_iassert: IAssert => Result[Option[Inst]] = {
    case IAssert(expr) => for {
      expre <- pe(expr)
    } yield Some(IAssert(expre.expr))
  }
  def pe_iassign: IAssign => Result[Option[Inst]] = {
    case IAssign(RefId(id), expr) => for {
      v <- pe(expr)
      _ <- (context: S) => context.updateLabelwise((u) => u.setId(id.name, SymbolicValueFactory.mkDynamic(ERef(RefId(id)))))
    } yield Some(IAssign(RefId(id), v.expr)) // TODO
    case IAssign(ref, expr) => for {
      refr <- pe(ref)
      expre <- pe(expr)
    } yield { Some(IAssign(refr, expre.expr)) } // TODO
  }
  def pe_iclo: IClo => Result[Option[Inst]] = {
    case IClo(id, params, captured, body) => for {
      _ <- (context: S) => context.updateLabelwise((u) => u.setId(id.name, SymbolicValueFactory.mkDynamic(ERef(RefId(id)))))
    } yield Some(IClo(id, params, captured, body))
  }
  def pe_icont: ICont => Result[Option[Inst]] = {
    case ICont(id, params, body) => (context: S) => {
      val (nbody, _) = pe(body)(params.foldLeft(context) {
        case (nc, id) => nc.updateLabelwise((u) => u.setId(id.name, SymbolicValueFactory.mkDynamic(ERef(RefId(id)))))
      })
      (Some(ICont(id, params, nbody.getOrElse(IExpr(EStr("empty"))))), context.updateLabelwise((u) => u.setId(id.name, SymbolicValueFactory.mkDynamic(ERef(RefId(id))))))
    }
  }
  def pe_idelete: IDelete => Result[Option[Inst]] = {
    case IDelete(ref) => for {
      refr <- pe(ref)
    } yield Some(IDelete(refr))
  }
  def pe_iexpr: IExpr => Result[Option[Inst]] = {
    case IExpr(expr) => for {
      e <- pe(expr)
    } yield Some(IExpr(e.expr))
  }

  def pe_iif: IIf => Result[Option[Inst]] = {
    case IIf(cond, thenInst, elseInst) => (dcontext: S) => {
      val (c, dcontext1) = pe(cond)(dcontext)
      val (thenInstI, dcontext2) = pe(thenInst)(dcontext1)
      val (elseInstI, dcontext3) = pe(elseInst)(SpecializeContextImpl(dcontext1.labelwiseContext, dcontext2.globalContext))
      val (itrue, ifalse, nstate) = dcontext2.labelwiseContext merge dcontext3.labelwiseContext

      (Some(IIf(
        c.expr,
        ISeq(thenInstI.toList ++ itrue),
        ISeq(elseInstI.toList ++ ifalse)
      )), SpecializeContextImpl(nstate, dcontext3.globalContext))
    }
  }

  def pe_ilet: ILet => Result[Option[Inst]] = {
    case ILet(id, expr) => for {
      v <- pe(expr)
      _ <- (context: S) => context.updateLabelwise((u) => u.setId(id.name, SymbolicValueFactory.mkDynamic(ERef(RefId(id)))))
    } yield Some(ILet(id, v.expr))
  }

  def pe_iprepend: IPrepend => Result[Option[Inst]] = {
    case IPrepend(expr, list) => for {
      expre <- pe(expr)
      liste <- pe(list)
    } yield Some(IPrepend(expre.expr, liste.expr))
  }

  def pe_iprint: IPrint => Result[Option[Inst]] = {
    case IPrint(expr) => for {
      expre <- pe(expr)
    } yield Some(IPrint(expre.expr))
  }

  def pe_ireturn: IReturn => Result[Option[Inst]] = {
    case IReturn(expr) => for {
      ne <- pe(expr)
      _ <- (context: S) => context.updateLabelwise((u) => u.setRet(Some(ne)))
    } yield Some(IReturn(ne.expr))

  }

  def pe_ithrow: IThrow => Result[Option[Inst]] = {
    case IThrow(name) => Some(IThrow(name))
  }

  def pe_iwhile: IWhile => Result[Option[Inst]] = {
    case IWhile(cond, body) => for {
      _ <- (context: S) => context.toDynamic
    } yield Some(IWhile(cond, body)) // TODO
  }

  def pe_iwithcont: IWithCont => Result[Option[Inst]] = {
    case IWithCont(id, params, body) => (context: S) => {
      val (nbody, _) = pe(body)(context.updateLabelwise((u) => u.setId(id.name, SymbolicValueFactory.mkDynamic(ERef(RefId(id))))))
      (Some(IWithCont(id, params, nbody.getOrElse(IExpr(EStr("empty"))))), params.foldLeft(context) {
        case (nc, id) => nc.updateLabelwise((u) => u.setId(id.name, SymbolicValueFactory.mkDynamic(ERef(RefId(id)))))
      })
    }
  }

  def pe_enum: ENum => Result[SymbolicValue] = {
    case ENum(n) => SymbolicValueFactory.mkDynamic(ENum(n))
  }

  def pe_einum: EINum => Result[SymbolicValue] = {
    case EINum(n) => SymbolicValueFactory.mkDynamic(EINum(n))
  }

  def pe_ebiginum: EBigINum => Result[SymbolicValue] = {
    case EBigINum(b) => SymbolicValueFactory.mkDynamic(EBigINum(b))
  }

  def pe_estr: EStr => Result[SymbolicValue] = {
    case EStr(str) => SymbolicValueFactory.mkDynamic(EStr(str))
  }

  def pe_ebool: EBool => Result[SymbolicValue] = {
    case EBool(b) => SymbolicValueFactory.mkDynamic(EBool(b))
  }

  def pe_eundef: EUndef.type => Result[SymbolicValue] = {
    case EUndef => SymbolicValueFactory.mkDynamic(EUndef)
  }

  def pe_enull: ENull.type => Result[SymbolicValue] = {
    case ENull => SymbolicValueFactory.mkDynamic(ENull)
  }

  def pe_eabsent: EAbsent.type => Result[SymbolicValue] = {
    case EAbsent => SymbolicValueFactory.mkDynamic(EAbsent)
  }

  def pe_econst: EConst => Result[SymbolicValue] = {
    case EConst(name) => SymbolicValueFactory.mkDynamic(EConst(name))
  }

  def pe_ecomp: EComp => Result[SymbolicValue] = {
    case EComp(ty, value, target) => for {
      tye <- pe(ty)
      ve <- pe(value)
      targete <- pe(target)
    } yield SymbolicValueFactory.mkDynamic(EComp(tye.expr, ve.expr, targete.expr))
  }

  def pe_emap: EMap => Result[SymbolicValue] = {
    case EMap(ty, props) => for {
      propse <- join(props.map { case (ek, ev) => pe(ek).flatMap((vk) => pe(ev).flatMap((vv) => (vk, vv))) })
    } yield SymbolicValueFactory.mkDynamic(EMap(ty, propse.map { case (ek, ev) => (ek.expr, ev.expr) }))
  }

  def pe_elist: EList => Result[SymbolicValue] = {
    case EList(exprs) => for {
      le <- join(exprs.map(pe))
    } yield (SymbolicValueFactory.mkDynamic(EList(le.map(_.expr))))
  }

  def pe_esymbol: ESymbol => Result[SymbolicValue] = {
    case ESymbol(desc) => for {
      desce <- pe(desc)
    } yield (SymbolicValueFactory.mkDynamic(ESymbol(desce.expr)))
  }

  def pe_epop: EPop => Result[SymbolicValue] = {
    case EPop(list, idx) => for {
      liste <- pe(list)
      idxe <- pe(idx)
    } yield (SymbolicValueFactory.mkDynamic(EPop(liste.expr, idxe.expr)))
  }

  def pe_eref: ERef => Result[SymbolicValue] = {
    case ERef(ref) => for {
      refr <- pe(ref)
    } yield SymbolicValueFactory.mkDynamic(ERef(refr))
  }

  def pe_euop: EUOp => Result[SymbolicValue] = {
    case EUOp(uop, expr) => for {
      e <- pe(expr)
    } yield SymbolicValueFactory.mkDynamic(EUOp(uop, e.expr))
  }

  def pe_ebop: EBOp => Result[SymbolicValue] = {
    case EBOp(bop, left, right) => for {
      le <- pe(left)
      re <- pe(right)
    } yield SymbolicValueFactory.mkDynamic(EBOp(bop, le.expr, re.expr))
  }

  def pe_etypeof: ETypeOf => Result[SymbolicValue] = {
    case ETypeOf(expr) => for {
      e <- pe(expr)
    } yield SymbolicValueFactory.mkDynamic(ETypeOf(e.expr))

  }

  def pe_eiscompletion: EIsCompletion => Result[SymbolicValue] = {
    case EIsCompletion(expr) => for {
      e <- pe(expr)
    } yield SymbolicValueFactory.mkDynamic(EIsCompletion(e.expr))
  }

  def pe_eisinstanceof: EIsInstanceOf => Result[SymbolicValue] = {
    case EIsInstanceOf(base, name) => for {
      be <- pe(base)
    } yield SymbolicValueFactory.mkDynamic(EIsInstanceOf(be.expr, name))
  }

  def pe_egetelems: EGetElems => Result[SymbolicValue] = {
    case EGetElems(base, name) => for {
      basee <- pe(base)
    } yield SymbolicValueFactory.mkDynamic(EGetElems(basee.expr, name))

  }

  def pe_egetsyntax: EGetSyntax => Result[SymbolicValue] = {
    case EGetSyntax(base) => for {
      basee <- pe(base)
    } yield SymbolicValueFactory.mkDynamic(EGetSyntax(basee.expr))

  }

  def pe_eparsesyntax: EParseSyntax => Result[SymbolicValue] = {
    case EParseSyntax(code, rule, parserParams) => for {
      codee <- pe(code)
      rulee <- pe(rule)
    } yield SymbolicValueFactory.mkDynamic(EParseSyntax(codee.expr, rulee.expr, parserParams))

  }

  def pe_econvert: EConvert => Result[SymbolicValue] = {
    case EConvert(source, target, flags) => for {
      sourcee <- pe(source)
      flagse <- join(flags.map(pe))
    } yield SymbolicValueFactory.mkDynamic(EConvert(sourcee.expr, target, flagse.map(_.expr)))

  }

  def pe_econtains: EContains => Result[SymbolicValue] = {
    case EContains(list, elem) => for {
      liste <- pe(list)
      eleme <- pe(elem)
    } yield SymbolicValueFactory.mkDynamic(EContains(liste.expr, eleme.expr))

  }

  def pe_ereturnifabrupt: EReturnIfAbrupt => Result[SymbolicValue] = {
    case EReturnIfAbrupt(expr, check) => for {
      ne <- pe(expr)
    } yield SymbolicValueFactory.mkDynamic(EReturnIfAbrupt(ne.expr, check))
  }
  def pe_ecopy: ECopy => Result[SymbolicValue] = {
    case ECopy(obj) => for {
      obje <- pe(obj)
    } yield SymbolicValueFactory.mkDynamic(ECopy(obje.expr))

  }

  def pe_ekeys: EKeys => Result[SymbolicValue] = {
    case EKeys(mobj, intSorted) => for {
      mobje <- pe(mobj)
    } yield SymbolicValueFactory.mkDynamic(EKeys(mobje.expr, intSorted))
  }

  def pe_enotsupported: ENotSupported => Result[SymbolicValue] = {
    case ENotSupported(msg) => SymbolicValueFactory.mkDynamic(ENotSupported(msg))
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

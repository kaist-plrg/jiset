package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.util.Useful.error
import kr.ac.kaist.jiset.util.Span

object ASTDiff {
  def diffError(l: Any, r: Any): Unit = error(s"$l != $r")
  def diff[T](l: Option[T], r: Option[T], d: (T, T) => Unit): Unit = (l, r) match {
    case (Some(l), Some(r)) => d(l, r)
    case (None, None) =>
    case _ => diffError(l, r)
  }
  def diff(l: Span, r: Span): Unit = if (l != r) diffError(l, r)
  def diff(l: List[Boolean], r: List[Boolean]): Unit = if (l != r) diffError(l, r)
  def diff(l: Lexical, r: Lexical): Unit = {
    if (l.kind != r.kind) diffError(l.kind, r.kind)
    if (l.str != r.str) diffError(l.str, r.str)
  }

  def diff(l: IdentifierReference, r: IdentifierReference): Unit = (l, r) match {
    case (IdentifierReference0(l0, lp, ls), IdentifierReference0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (IdentifierReference1(lp, ls), IdentifierReference1(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (IdentifierReference2(lp, ls), IdentifierReference2(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: BindingIdentifier, r: BindingIdentifier): Unit = (l, r) match {
    case (BindingIdentifier0(l0, lp, ls), BindingIdentifier0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (BindingIdentifier1(lp, ls), BindingIdentifier1(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (BindingIdentifier2(lp, ls), BindingIdentifier2(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: LabelIdentifier, r: LabelIdentifier): Unit = (l, r) match {
    case (LabelIdentifier0(l0, lp, ls), LabelIdentifier0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (LabelIdentifier1(lp, ls), LabelIdentifier1(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (LabelIdentifier2(lp, ls), LabelIdentifier2(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: Identifier, r: Identifier): Unit = (l, r) match {
    case (Identifier0(l0, lp, ls), Identifier0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: PrimaryExpression, r: PrimaryExpression): Unit = (l, r) match {
    case (PrimaryExpression0(lp, ls), PrimaryExpression0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (PrimaryExpression1(l0, lp, ls), PrimaryExpression1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (PrimaryExpression2(l0, lp, ls), PrimaryExpression2(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (PrimaryExpression3(l0, lp, ls), PrimaryExpression3(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (PrimaryExpression4(l0, lp, ls), PrimaryExpression4(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (PrimaryExpression5(l0, lp, ls), PrimaryExpression5(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (PrimaryExpression6(l0, lp, ls), PrimaryExpression6(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (PrimaryExpression7(l0, lp, ls), PrimaryExpression7(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (PrimaryExpression8(l0, lp, ls), PrimaryExpression8(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (PrimaryExpression9(l0, lp, ls), PrimaryExpression9(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (PrimaryExpression10(l0, lp, ls), PrimaryExpression10(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (PrimaryExpression11(l0, lp, ls), PrimaryExpression11(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (PrimaryExpression12(l0, lp, ls), PrimaryExpression12(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: CoverParenthesizedExpressionAndArrowParameterList, r: CoverParenthesizedExpressionAndArrowParameterList): Unit = (l, r) match {
    case (CoverParenthesizedExpressionAndArrowParameterList0(l1, lp, ls), CoverParenthesizedExpressionAndArrowParameterList0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (CoverParenthesizedExpressionAndArrowParameterList1(l1, lp, ls), CoverParenthesizedExpressionAndArrowParameterList1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (CoverParenthesizedExpressionAndArrowParameterList2(lp, ls), CoverParenthesizedExpressionAndArrowParameterList2(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (CoverParenthesizedExpressionAndArrowParameterList3(l2, lp, ls), CoverParenthesizedExpressionAndArrowParameterList3(r2, rp, rs)) =>
      diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (CoverParenthesizedExpressionAndArrowParameterList4(l2, lp, ls), CoverParenthesizedExpressionAndArrowParameterList4(r2, rp, rs)) =>
      diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (CoverParenthesizedExpressionAndArrowParameterList5(l1, l4, lp, ls), CoverParenthesizedExpressionAndArrowParameterList5(r1, r4, rp, rs)) =>
      diff(l1, r1); diff(l4, r4); diff(lp, rp); diff(ls, rs)
    case (CoverParenthesizedExpressionAndArrowParameterList6(l1, l4, lp, ls), CoverParenthesizedExpressionAndArrowParameterList6(r1, r4, rp, rs)) =>
      diff(l1, r1); diff(l4, r4); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ParenthesizedExpression, r: ParenthesizedExpression): Unit = (l, r) match {
    case (ParenthesizedExpression0(l1, lp, ls), ParenthesizedExpression0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: Literal, r: Literal): Unit = (l, r) match {
    case (Literal0(l0, lp, ls), Literal0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Literal1(l0, lp, ls), Literal1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Literal2(l0, lp, ls), Literal2(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Literal3(l0, lp, ls), Literal3(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ArrayLiteral, r: ArrayLiteral): Unit = (l, r) match {
    case (ArrayLiteral0(l1, lp, ls), ArrayLiteral0(r1, rp, rs)) =>
      diff[Elision](l1, r1, diff); diff(lp, rp); diff(ls, rs)
    case (ArrayLiteral1(l1, lp, ls), ArrayLiteral1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (ArrayLiteral2(l1, l3, lp, ls), ArrayLiteral2(r1, r3, rp, rs)) =>
      diff(l1, r1); diff[Elision](l3, r3, diff); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ElementList, r: ElementList): Unit = (l, r) match {
    case (ElementList0(l0, l1, lp, ls), ElementList0(r0, r1, rp, rs)) =>
      diff[Elision](l0, r0, diff); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (ElementList1(l0, l1, lp, ls), ElementList1(r0, r1, rp, rs)) =>
      diff[Elision](l0, r0, diff); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (ElementList2(l0, l2, l3, lp, ls), ElementList2(r0, r2, r3, rp, rs)) =>
      diff(l0, r0); diff[Elision](l2, r2, diff); diff(l3, r3); diff(lp, rp); diff(ls, rs)
    case (ElementList3(l0, l2, l3, lp, ls), ElementList3(r0, r2, r3, rp, rs)) =>
      diff(l0, r0); diff[Elision](l2, r2, diff); diff(l3, r3); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: Elision, r: Elision): Unit = (l, r) match {
    case (Elision0(lp, ls), Elision0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (Elision1(l0, lp, ls), Elision1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: SpreadElement, r: SpreadElement): Unit = (l, r) match {
    case (SpreadElement0(l1, lp, ls), SpreadElement0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ObjectLiteral, r: ObjectLiteral): Unit = (l, r) match {
    case (ObjectLiteral0(lp, ls), ObjectLiteral0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (ObjectLiteral1(l1, lp, ls), ObjectLiteral1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (ObjectLiteral2(l1, lp, ls), ObjectLiteral2(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: PropertyDefinitionList, r: PropertyDefinitionList): Unit = (l, r) match {
    case (PropertyDefinitionList0(l0, lp, ls), PropertyDefinitionList0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (PropertyDefinitionList1(l0, l2, lp, ls), PropertyDefinitionList1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: PropertyDefinition, r: PropertyDefinition): Unit = (l, r) match {
    case (PropertyDefinition0(l0, lp, ls), PropertyDefinition0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (PropertyDefinition1(l0, lp, ls), PropertyDefinition1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (PropertyDefinition2(l0, l2, lp, ls), PropertyDefinition2(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (PropertyDefinition3(l0, lp, ls), PropertyDefinition3(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (PropertyDefinition4(l1, lp, ls), PropertyDefinition4(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: PropertyName, r: PropertyName): Unit = (l, r) match {
    case (PropertyName0(l0, lp, ls), PropertyName0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (PropertyName1(l0, lp, ls), PropertyName1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: LiteralPropertyName, r: LiteralPropertyName): Unit = (l, r) match {
    case (LiteralPropertyName0(l0, lp, ls), LiteralPropertyName0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (LiteralPropertyName1(l0, lp, ls), LiteralPropertyName1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (LiteralPropertyName2(l0, lp, ls), LiteralPropertyName2(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ComputedPropertyName, r: ComputedPropertyName): Unit = (l, r) match {
    case (ComputedPropertyName0(l1, lp, ls), ComputedPropertyName0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: CoverInitializedName, r: CoverInitializedName): Unit = (l, r) match {
    case (CoverInitializedName0(l0, l1, lp, ls), CoverInitializedName0(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: Initializer, r: Initializer): Unit = (l, r) match {
    case (Initializer0(l1, lp, ls), Initializer0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: TemplateLiteral, r: TemplateLiteral): Unit = (l, r) match {
    case (TemplateLiteral0(l0, lp, ls), TemplateLiteral0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (TemplateLiteral1(l0, lp, ls), TemplateLiteral1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: SubstitutionTemplate, r: SubstitutionTemplate): Unit = (l, r) match {
    case (SubstitutionTemplate0(l0, l1, l2, lp, ls), SubstitutionTemplate0(r0, r1, r2, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: TemplateSpans, r: TemplateSpans): Unit = (l, r) match {
    case (TemplateSpans0(l0, lp, ls), TemplateSpans0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (TemplateSpans1(l0, l1, lp, ls), TemplateSpans1(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: TemplateMiddleList, r: TemplateMiddleList): Unit = (l, r) match {
    case (TemplateMiddleList0(l0, l1, lp, ls), TemplateMiddleList0(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (TemplateMiddleList1(l0, l1, l2, lp, ls), TemplateMiddleList1(r0, r1, r2, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: MemberExpression, r: MemberExpression): Unit = (l, r) match {
    case (MemberExpression0(l0, lp, ls), MemberExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (MemberExpression1(l0, l2, lp, ls), MemberExpression1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (MemberExpression2(l0, l2, lp, ls), MemberExpression2(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (MemberExpression3(l0, l1, lp, ls), MemberExpression3(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (MemberExpression4(l0, lp, ls), MemberExpression4(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (MemberExpression5(l0, lp, ls), MemberExpression5(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (MemberExpression6(l1, l2, lp, ls), MemberExpression6(r1, r2, rp, rs)) =>
      diff(l1, r1); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: SuperProperty, r: SuperProperty): Unit = (l, r) match {
    case (SuperProperty0(l2, lp, ls), SuperProperty0(r2, rp, rs)) =>
      diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (SuperProperty1(l2, lp, ls), SuperProperty1(r2, rp, rs)) =>
      diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: MetaProperty, r: MetaProperty): Unit = (l, r) match {
    case (MetaProperty0(l0, lp, ls), MetaProperty0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (MetaProperty1(l0, lp, ls), MetaProperty1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: NewTarget, r: NewTarget): Unit = (l, r) match {
    case (NewTarget0(lp, ls), NewTarget0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ImportMeta, r: ImportMeta): Unit = (l, r) match {
    case (ImportMeta0(lp, ls), ImportMeta0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: NewExpression, r: NewExpression): Unit = (l, r) match {
    case (NewExpression0(l0, lp, ls), NewExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (NewExpression1(l1, lp, ls), NewExpression1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: CallExpression, r: CallExpression): Unit = (l, r) match {
    case (CallExpression0(l0, lp, ls), CallExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (CallExpression1(l0, lp, ls), CallExpression1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (CallExpression2(l0, lp, ls), CallExpression2(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (CallExpression3(l0, l1, lp, ls), CallExpression3(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (CallExpression4(l0, l2, lp, ls), CallExpression4(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (CallExpression5(l0, l2, lp, ls), CallExpression5(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (CallExpression6(l0, l1, lp, ls), CallExpression6(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: SuperCall, r: SuperCall): Unit = (l, r) match {
    case (SuperCall0(l1, lp, ls), SuperCall0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ImportCall, r: ImportCall): Unit = (l, r) match {
    case (ImportCall0(l2, lp, ls), ImportCall0(r2, rp, rs)) =>
      diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: Arguments, r: Arguments): Unit = (l, r) match {
    case (Arguments0(lp, ls), Arguments0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (Arguments1(l1, lp, ls), Arguments1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (Arguments2(l1, lp, ls), Arguments2(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ArgumentList, r: ArgumentList): Unit = (l, r) match {
    case (ArgumentList0(l0, lp, ls), ArgumentList0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ArgumentList1(l1, lp, ls), ArgumentList1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (ArgumentList2(l0, l2, lp, ls), ArgumentList2(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (ArgumentList3(l0, l3, lp, ls), ArgumentList3(r0, r3, rp, rs)) =>
      diff(l0, r0); diff(l3, r3); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: OptionalExpression, r: OptionalExpression): Unit = (l, r) match {
    case (OptionalExpression0(l0, l1, lp, ls), OptionalExpression0(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (OptionalExpression1(l0, l1, lp, ls), OptionalExpression1(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (OptionalExpression2(l0, l1, lp, ls), OptionalExpression2(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: OptionalChain, r: OptionalChain): Unit = (l, r) match {
    case (OptionalChain0(l1, lp, ls), OptionalChain0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (OptionalChain1(l2, lp, ls), OptionalChain1(r2, rp, rs)) =>
      diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (OptionalChain2(l1, lp, ls), OptionalChain2(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (OptionalChain3(l1, lp, ls), OptionalChain3(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (OptionalChain4(l0, l1, lp, ls), OptionalChain4(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (OptionalChain5(l0, l2, lp, ls), OptionalChain5(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (OptionalChain6(l0, l2, lp, ls), OptionalChain6(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (OptionalChain7(l0, l1, lp, ls), OptionalChain7(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: LeftHandSideExpression, r: LeftHandSideExpression): Unit = (l, r) match {
    case (LeftHandSideExpression0(l0, lp, ls), LeftHandSideExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (LeftHandSideExpression1(l0, lp, ls), LeftHandSideExpression1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (LeftHandSideExpression2(l0, lp, ls), LeftHandSideExpression2(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: CallMemberExpression, r: CallMemberExpression): Unit = (l, r) match {
    case (CallMemberExpression0(l0, l1, lp, ls), CallMemberExpression0(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: UpdateExpression, r: UpdateExpression): Unit = (l, r) match {
    case (UpdateExpression0(l0, lp, ls), UpdateExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (UpdateExpression1(l0, lp, ls), UpdateExpression1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (UpdateExpression2(l0, lp, ls), UpdateExpression2(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (UpdateExpression3(l1, lp, ls), UpdateExpression3(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (UpdateExpression4(l1, lp, ls), UpdateExpression4(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: UnaryExpression, r: UnaryExpression): Unit = (l, r) match {
    case (UnaryExpression0(l0, lp, ls), UnaryExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (UnaryExpression1(l1, lp, ls), UnaryExpression1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (UnaryExpression2(l1, lp, ls), UnaryExpression2(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (UnaryExpression3(l1, lp, ls), UnaryExpression3(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (UnaryExpression4(l1, lp, ls), UnaryExpression4(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (UnaryExpression5(l1, lp, ls), UnaryExpression5(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (UnaryExpression6(l1, lp, ls), UnaryExpression6(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (UnaryExpression7(l1, lp, ls), UnaryExpression7(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (UnaryExpression8(l0, lp, ls), UnaryExpression8(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ExponentiationExpression, r: ExponentiationExpression): Unit = (l, r) match {
    case (ExponentiationExpression0(l0, lp, ls), ExponentiationExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ExponentiationExpression1(l0, l2, lp, ls), ExponentiationExpression1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: MultiplicativeExpression, r: MultiplicativeExpression): Unit = (l, r) match {
    case (MultiplicativeExpression0(l0, lp, ls), MultiplicativeExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (MultiplicativeExpression1(l0, l1, l2, lp, ls), MultiplicativeExpression1(r0, r1, r2, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: MultiplicativeOperator, r: MultiplicativeOperator): Unit = (l, r) match {
    case (MultiplicativeOperator0(lp, ls), MultiplicativeOperator0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (MultiplicativeOperator1(lp, ls), MultiplicativeOperator1(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (MultiplicativeOperator2(lp, ls), MultiplicativeOperator2(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AdditiveExpression, r: AdditiveExpression): Unit = (l, r) match {
    case (AdditiveExpression0(l0, lp, ls), AdditiveExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (AdditiveExpression1(l0, l2, lp, ls), AdditiveExpression1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (AdditiveExpression2(l0, l2, lp, ls), AdditiveExpression2(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ShiftExpression, r: ShiftExpression): Unit = (l, r) match {
    case (ShiftExpression0(l0, lp, ls), ShiftExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ShiftExpression1(l0, l2, lp, ls), ShiftExpression1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (ShiftExpression2(l0, l2, lp, ls), ShiftExpression2(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (ShiftExpression3(l0, l2, lp, ls), ShiftExpression3(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: RelationalExpression, r: RelationalExpression): Unit = (l, r) match {
    case (RelationalExpression0(l0, lp, ls), RelationalExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (RelationalExpression1(l0, l2, lp, ls), RelationalExpression1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (RelationalExpression2(l0, l2, lp, ls), RelationalExpression2(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (RelationalExpression3(l0, l2, lp, ls), RelationalExpression3(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (RelationalExpression4(l0, l2, lp, ls), RelationalExpression4(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (RelationalExpression5(l0, l2, lp, ls), RelationalExpression5(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (RelationalExpression6(l0, l2, lp, ls), RelationalExpression6(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: EqualityExpression, r: EqualityExpression): Unit = (l, r) match {
    case (EqualityExpression0(l0, lp, ls), EqualityExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (EqualityExpression1(l0, l2, lp, ls), EqualityExpression1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (EqualityExpression2(l0, l2, lp, ls), EqualityExpression2(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (EqualityExpression3(l0, l2, lp, ls), EqualityExpression3(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (EqualityExpression4(l0, l2, lp, ls), EqualityExpression4(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: BitwiseANDExpression, r: BitwiseANDExpression): Unit = (l, r) match {
    case (BitwiseANDExpression0(l0, lp, ls), BitwiseANDExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (BitwiseANDExpression1(l0, l2, lp, ls), BitwiseANDExpression1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: BitwiseXORExpression, r: BitwiseXORExpression): Unit = (l, r) match {
    case (BitwiseXORExpression0(l0, lp, ls), BitwiseXORExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (BitwiseXORExpression1(l0, l2, lp, ls), BitwiseXORExpression1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: BitwiseORExpression, r: BitwiseORExpression): Unit = (l, r) match {
    case (BitwiseORExpression0(l0, lp, ls), BitwiseORExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (BitwiseORExpression1(l0, l2, lp, ls), BitwiseORExpression1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: LogicalANDExpression, r: LogicalANDExpression): Unit = (l, r) match {
    case (LogicalANDExpression0(l0, lp, ls), LogicalANDExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (LogicalANDExpression1(l0, l2, lp, ls), LogicalANDExpression1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: LogicalORExpression, r: LogicalORExpression): Unit = (l, r) match {
    case (LogicalORExpression0(l0, lp, ls), LogicalORExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (LogicalORExpression1(l0, l2, lp, ls), LogicalORExpression1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: CoalesceExpression, r: CoalesceExpression): Unit = (l, r) match {
    case (CoalesceExpression0(l0, l2, lp, ls), CoalesceExpression0(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: CoalesceExpressionHead, r: CoalesceExpressionHead): Unit = (l, r) match {
    case (CoalesceExpressionHead0(l0, lp, ls), CoalesceExpressionHead0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (CoalesceExpressionHead1(l0, lp, ls), CoalesceExpressionHead1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ShortCircuitExpression, r: ShortCircuitExpression): Unit = (l, r) match {
    case (ShortCircuitExpression0(l0, lp, ls), ShortCircuitExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ShortCircuitExpression1(l0, lp, ls), ShortCircuitExpression1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ConditionalExpression, r: ConditionalExpression): Unit = (l, r) match {
    case (ConditionalExpression0(l0, lp, ls), ConditionalExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ConditionalExpression1(l0, l2, l4, lp, ls), ConditionalExpression1(r0, r2, r4, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(l4, r4); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentExpression, r: AssignmentExpression): Unit = (l, r) match {
    case (AssignmentExpression0(l0, lp, ls), AssignmentExpression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (AssignmentExpression1(l0, lp, ls), AssignmentExpression1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (AssignmentExpression2(l0, lp, ls), AssignmentExpression2(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (AssignmentExpression3(l0, lp, ls), AssignmentExpression3(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (AssignmentExpression4(l0, l2, lp, ls), AssignmentExpression4(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (AssignmentExpression5(l0, l1, l2, lp, ls), AssignmentExpression5(r0, r1, r2, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (AssignmentExpression6(l0, l2, lp, ls), AssignmentExpression6(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (AssignmentExpression7(l0, l2, lp, ls), AssignmentExpression7(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (AssignmentExpression8(l0, l2, lp, ls), AssignmentExpression8(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentOperator, r: AssignmentOperator): Unit = (l, r) match {
    case (AssignmentOperator0(lp, ls), AssignmentOperator0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (AssignmentOperator1(lp, ls), AssignmentOperator1(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (AssignmentOperator2(lp, ls), AssignmentOperator2(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (AssignmentOperator3(lp, ls), AssignmentOperator3(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (AssignmentOperator4(lp, ls), AssignmentOperator4(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (AssignmentOperator5(lp, ls), AssignmentOperator5(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (AssignmentOperator6(lp, ls), AssignmentOperator6(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (AssignmentOperator7(lp, ls), AssignmentOperator7(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (AssignmentOperator8(lp, ls), AssignmentOperator8(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (AssignmentOperator9(lp, ls), AssignmentOperator9(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (AssignmentOperator10(lp, ls), AssignmentOperator10(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (AssignmentOperator11(lp, ls), AssignmentOperator11(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentPattern, r: AssignmentPattern): Unit = (l, r) match {
    case (AssignmentPattern0(l0, lp, ls), AssignmentPattern0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (AssignmentPattern1(l0, lp, ls), AssignmentPattern1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ObjectAssignmentPattern, r: ObjectAssignmentPattern): Unit = (l, r) match {
    case (ObjectAssignmentPattern0(lp, ls), ObjectAssignmentPattern0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (ObjectAssignmentPattern1(l1, lp, ls), ObjectAssignmentPattern1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (ObjectAssignmentPattern2(l1, lp, ls), ObjectAssignmentPattern2(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (ObjectAssignmentPattern3(l1, l3, lp, ls), ObjectAssignmentPattern3(r1, r3, rp, rs)) =>
      diff(l1, r1); diff[AssignmentRestProperty](l3, r3, diff); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ArrayAssignmentPattern, r: ArrayAssignmentPattern): Unit = (l, r) match {
    case (ArrayAssignmentPattern0(l1, l2, lp, ls), ArrayAssignmentPattern0(r1, r2, rp, rs)) =>
      diff[Elision](l1, r1, diff); diff[AssignmentRestElement](l2, r2, diff); diff(lp, rp); diff(ls, rs)
    case (ArrayAssignmentPattern1(l1, lp, ls), ArrayAssignmentPattern1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (ArrayAssignmentPattern2(l1, l3, l4, lp, ls), ArrayAssignmentPattern2(r1, r3, r4, rp, rs)) =>
      diff(l1, r1); diff[Elision](l3, r3, diff); diff[AssignmentRestElement](l4, r4, diff); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentRestProperty, r: AssignmentRestProperty): Unit = (l, r) match {
    case (AssignmentRestProperty0(l1, lp, ls), AssignmentRestProperty0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentPropertyList, r: AssignmentPropertyList): Unit = (l, r) match {
    case (AssignmentPropertyList0(l0, lp, ls), AssignmentPropertyList0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (AssignmentPropertyList1(l0, l2, lp, ls), AssignmentPropertyList1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentElementList, r: AssignmentElementList): Unit = (l, r) match {
    case (AssignmentElementList0(l0, lp, ls), AssignmentElementList0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (AssignmentElementList1(l0, l2, lp, ls), AssignmentElementList1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentElisionElement, r: AssignmentElisionElement): Unit = (l, r) match {
    case (AssignmentElisionElement0(l0, l1, lp, ls), AssignmentElisionElement0(r0, r1, rp, rs)) =>
      diff[Elision](l0, r0, diff); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentProperty, r: AssignmentProperty): Unit = (l, r) match {
    case (AssignmentProperty0(l0, l1, lp, ls), AssignmentProperty0(r0, r1, rp, rs)) =>
      diff(l0, r0); diff[Initializer](l1, r1, diff); diff(lp, rp); diff(ls, rs)
    case (AssignmentProperty1(l0, l2, lp, ls), AssignmentProperty1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentElement, r: AssignmentElement): Unit = (l, r) match {
    case (AssignmentElement0(l0, l1, lp, ls), AssignmentElement0(r0, r1, rp, rs)) =>
      diff(l0, r0); diff[Initializer](l1, r1, diff); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentRestElement, r: AssignmentRestElement): Unit = (l, r) match {
    case (AssignmentRestElement0(l1, lp, ls), AssignmentRestElement0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: DestructuringAssignmentTarget, r: DestructuringAssignmentTarget): Unit = (l, r) match {
    case (DestructuringAssignmentTarget0(l0, lp, ls), DestructuringAssignmentTarget0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: Expression, r: Expression): Unit = (l, r) match {
    case (Expression0(l0, lp, ls), Expression0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Expression1(l0, l2, lp, ls), Expression1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: Statement, r: Statement): Unit = (l, r) match {
    case (Statement0(l0, lp, ls), Statement0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Statement1(l0, lp, ls), Statement1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Statement2(l0, lp, ls), Statement2(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Statement3(l0, lp, ls), Statement3(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Statement4(l0, lp, ls), Statement4(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Statement5(l0, lp, ls), Statement5(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Statement6(l0, lp, ls), Statement6(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Statement7(l0, lp, ls), Statement7(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Statement8(l0, lp, ls), Statement8(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Statement9(l0, lp, ls), Statement9(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Statement10(l0, lp, ls), Statement10(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Statement11(l0, lp, ls), Statement11(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Statement12(l0, lp, ls), Statement12(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Statement13(l0, lp, ls), Statement13(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: Declaration, r: Declaration): Unit = (l, r) match {
    case (Declaration0(l0, lp, ls), Declaration0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Declaration1(l0, lp, ls), Declaration1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (Declaration2(l0, lp, ls), Declaration2(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: HoistableDeclaration, r: HoistableDeclaration): Unit = (l, r) match {
    case (HoistableDeclaration0(l0, lp, ls), HoistableDeclaration0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (HoistableDeclaration1(l0, lp, ls), HoistableDeclaration1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (HoistableDeclaration2(l0, lp, ls), HoistableDeclaration2(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (HoistableDeclaration3(l0, lp, ls), HoistableDeclaration3(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: BreakableStatement, r: BreakableStatement): Unit = (l, r) match {
    case (BreakableStatement0(l0, lp, ls), BreakableStatement0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (BreakableStatement1(l0, lp, ls), BreakableStatement1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: BlockStatement, r: BlockStatement): Unit = (l, r) match {
    case (BlockStatement0(l0, lp, ls), BlockStatement0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: Block, r: Block): Unit = (l, r) match {
    case (Block0(l1, lp, ls), Block0(r1, rp, rs)) =>
      diff[StatementList](l1, r1, diff); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: StatementList, r: StatementList): Unit = (l, r) match {
    case (StatementList0(l0, lp, ls), StatementList0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (StatementList1(l0, l1, lp, ls), StatementList1(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: StatementListItem, r: StatementListItem): Unit = (l, r) match {
    case (StatementListItem0(l0, lp, ls), StatementListItem0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (StatementListItem1(l0, lp, ls), StatementListItem1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: LexicalDeclaration, r: LexicalDeclaration): Unit = (l, r) match {
    case (LexicalDeclaration0(l0, l1, lp, ls), LexicalDeclaration0(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: LetOrConst, r: LetOrConst): Unit = (l, r) match {
    case (LetOrConst0(lp, ls), LetOrConst0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (LetOrConst1(lp, ls), LetOrConst1(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: BindingList, r: BindingList): Unit = (l, r) match {
    case (BindingList0(l0, lp, ls), BindingList0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (BindingList1(l0, l2, lp, ls), BindingList1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: LexicalBinding, r: LexicalBinding): Unit = (l, r) match {
    case (LexicalBinding0(l0, l1, lp, ls), LexicalBinding0(r0, r1, rp, rs)) =>
      diff(l0, r0); diff[Initializer](l1, r1, diff); diff(lp, rp); diff(ls, rs)
    case (LexicalBinding1(l0, l1, lp, ls), LexicalBinding1(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: VariableStatement, r: VariableStatement): Unit = (l, r) match {
    case (VariableStatement0(l1, lp, ls), VariableStatement0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: VariableDeclarationList, r: VariableDeclarationList): Unit = (l, r) match {
    case (VariableDeclarationList0(l0, lp, ls), VariableDeclarationList0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (VariableDeclarationList1(l0, l2, lp, ls), VariableDeclarationList1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: VariableDeclaration, r: VariableDeclaration): Unit = (l, r) match {
    case (VariableDeclaration0(l0, l1, lp, ls), VariableDeclaration0(r0, r1, rp, rs)) =>
      diff(l0, r0); diff[Initializer](l1, r1, diff); diff(lp, rp); diff(ls, rs)
    case (VariableDeclaration1(l0, l1, lp, ls), VariableDeclaration1(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: BindingPattern, r: BindingPattern): Unit = (l, r) match {
    case (BindingPattern0(l0, lp, ls), BindingPattern0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (BindingPattern1(l0, lp, ls), BindingPattern1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ObjectBindingPattern, r: ObjectBindingPattern): Unit = (l, r) match {
    case (ObjectBindingPattern0(lp, ls), ObjectBindingPattern0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (ObjectBindingPattern1(l1, lp, ls), ObjectBindingPattern1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (ObjectBindingPattern2(l1, lp, ls), ObjectBindingPattern2(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (ObjectBindingPattern3(l1, l3, lp, ls), ObjectBindingPattern3(r1, r3, rp, rs)) =>
      diff(l1, r1); diff[BindingRestProperty](l3, r3, diff); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ArrayBindingPattern, r: ArrayBindingPattern): Unit = (l, r) match {
    case (ArrayBindingPattern0(l1, l2, lp, ls), ArrayBindingPattern0(r1, r2, rp, rs)) =>
      diff[Elision](l1, r1, diff); diff[BindingRestElement](l2, r2, diff); diff(lp, rp); diff(ls, rs)
    case (ArrayBindingPattern1(l1, lp, ls), ArrayBindingPattern1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (ArrayBindingPattern2(l1, l3, l4, lp, ls), ArrayBindingPattern2(r1, r3, r4, rp, rs)) =>
      diff(l1, r1); diff[Elision](l3, r3, diff); diff[BindingRestElement](l4, r4, diff); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: BindingRestProperty, r: BindingRestProperty): Unit = (l, r) match {
    case (BindingRestProperty0(l1, lp, ls), BindingRestProperty0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: BindingPropertyList, r: BindingPropertyList): Unit = (l, r) match {
    case (BindingPropertyList0(l0, lp, ls), BindingPropertyList0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (BindingPropertyList1(l0, l2, lp, ls), BindingPropertyList1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: BindingElementList, r: BindingElementList): Unit = (l, r) match {
    case (BindingElementList0(l0, lp, ls), BindingElementList0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (BindingElementList1(l0, l2, lp, ls), BindingElementList1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: BindingElisionElement, r: BindingElisionElement): Unit = (l, r) match {
    case (BindingElisionElement0(l0, l1, lp, ls), BindingElisionElement0(r0, r1, rp, rs)) =>
      diff[Elision](l0, r0, diff); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: BindingProperty, r: BindingProperty): Unit = (l, r) match {
    case (BindingProperty0(l0, lp, ls), BindingProperty0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (BindingProperty1(l0, l2, lp, ls), BindingProperty1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: BindingElement, r: BindingElement): Unit = (l, r) match {
    case (BindingElement0(l0, lp, ls), BindingElement0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (BindingElement1(l0, l1, lp, ls), BindingElement1(r0, r1, rp, rs)) =>
      diff(l0, r0); diff[Initializer](l1, r1, diff); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: SingleNameBinding, r: SingleNameBinding): Unit = (l, r) match {
    case (SingleNameBinding0(l0, l1, lp, ls), SingleNameBinding0(r0, r1, rp, rs)) =>
      diff(l0, r0); diff[Initializer](l1, r1, diff); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: BindingRestElement, r: BindingRestElement): Unit = (l, r) match {
    case (BindingRestElement0(l1, lp, ls), BindingRestElement0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (BindingRestElement1(l1, lp, ls), BindingRestElement1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: EmptyStatement, r: EmptyStatement): Unit = (l, r) match {
    case (EmptyStatement0(lp, ls), EmptyStatement0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ExpressionStatement, r: ExpressionStatement): Unit = (l, r) match {
    case (ExpressionStatement0(l1, lp, ls), ExpressionStatement0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: IfStatement, r: IfStatement): Unit = (l, r) match {
    case (IfStatement0(l2, l4, l6, lp, ls), IfStatement0(r2, r4, r6, rp, rs)) =>
      diff(l2, r2); diff(l4, r4); diff(l6, r6); diff(lp, rp); diff(ls, rs)
    case (IfStatement1(l2, l4, lp, ls), IfStatement1(r2, r4, rp, rs)) =>
      diff(l2, r2); diff(l4, r4); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: IterationStatement, r: IterationStatement): Unit = (l, r) match {
    case (IterationStatement0(l0, lp, ls), IterationStatement0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (IterationStatement1(l0, lp, ls), IterationStatement1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (IterationStatement2(l0, lp, ls), IterationStatement2(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (IterationStatement3(l0, lp, ls), IterationStatement3(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: DoWhileStatement, r: DoWhileStatement): Unit = (l, r) match {
    case (DoWhileStatement0(l1, l4, lp, ls), DoWhileStatement0(r1, r4, rp, rs)) =>
      diff(l1, r1); diff(l4, r4); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: WhileStatement, r: WhileStatement): Unit = (l, r) match {
    case (WhileStatement0(l2, l4, lp, ls), WhileStatement0(r2, r4, rp, rs)) =>
      diff(l2, r2); diff(l4, r4); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ForStatement, r: ForStatement): Unit = (l, r) match {
    case (ForStatement0(l3, l5, l7, l9, lp, ls), ForStatement0(r3, r5, r7, r9, rp, rs)) =>
      diff[Expression](l3, r3, diff); diff[Expression](l5, r5, diff); diff[Expression](l7, r7, diff); diff(l9, r9); diff(lp, rp); diff(ls, rs)
    case (ForStatement1(l3, l5, l7, l9, lp, ls), ForStatement1(r3, r5, r7, r9, rp, rs)) =>
      diff(l3, r3); diff[Expression](l5, r5, diff); diff[Expression](l7, r7, diff); diff(l9, r9); diff(lp, rp); diff(ls, rs)
    case (ForStatement2(l2, l3, l5, l7, lp, ls), ForStatement2(r2, r3, r5, r7, rp, rs)) =>
      diff(l2, r2); diff[Expression](l3, r3, diff); diff[Expression](l5, r5, diff); diff(l7, r7); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ForInOfStatement, r: ForInOfStatement): Unit = (l, r) match {
    case (ForInOfStatement0(l3, l5, l7, lp, ls), ForInOfStatement0(r3, r5, r7, rp, rs)) =>
      diff(l3, r3); diff(l5, r5); diff(l7, r7); diff(lp, rp); diff(ls, rs)
    case (ForInOfStatement1(l3, l5, l7, lp, ls), ForInOfStatement1(r3, r5, r7, rp, rs)) =>
      diff(l3, r3); diff(l5, r5); diff(l7, r7); diff(lp, rp); diff(ls, rs)
    case (ForInOfStatement2(l2, l4, l6, lp, ls), ForInOfStatement2(r2, r4, r6, rp, rs)) =>
      diff(l2, r2); diff(l4, r4); diff(l6, r6); diff(lp, rp); diff(ls, rs)
    case (ForInOfStatement3(l3, l5, l7, lp, ls), ForInOfStatement3(r3, r5, r7, rp, rs)) =>
      diff(l3, r3); diff(l5, r5); diff(l7, r7); diff(lp, rp); diff(ls, rs)
    case (ForInOfStatement4(l3, l5, l7, lp, ls), ForInOfStatement4(r3, r5, r7, rp, rs)) =>
      diff(l3, r3); diff(l5, r5); diff(l7, r7); diff(lp, rp); diff(ls, rs)
    case (ForInOfStatement5(l2, l4, l6, lp, ls), ForInOfStatement5(r2, r4, r6, rp, rs)) =>
      diff(l2, r2); diff(l4, r4); diff(l6, r6); diff(lp, rp); diff(ls, rs)
    case (ForInOfStatement6(l4, l6, l8, lp, ls), ForInOfStatement6(r4, r6, r8, rp, rs)) =>
      diff(l4, r4); diff(l6, r6); diff(l8, r8); diff(lp, rp); diff(ls, rs)
    case (ForInOfStatement7(l4, l6, l8, lp, ls), ForInOfStatement7(r4, r6, r8, rp, rs)) =>
      diff(l4, r4); diff(l6, r6); diff(l8, r8); diff(lp, rp); diff(ls, rs)
    case (ForInOfStatement8(l3, l5, l7, lp, ls), ForInOfStatement8(r3, r5, r7, rp, rs)) =>
      diff(l3, r3); diff(l5, r5); diff(l7, r7); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ForDeclaration, r: ForDeclaration): Unit = (l, r) match {
    case (ForDeclaration0(l0, l1, lp, ls), ForDeclaration0(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ForBinding, r: ForBinding): Unit = (l, r) match {
    case (ForBinding0(l0, lp, ls), ForBinding0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ForBinding1(l0, lp, ls), ForBinding1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ContinueStatement, r: ContinueStatement): Unit = (l, r) match {
    case (ContinueStatement0(lp, ls), ContinueStatement0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (ContinueStatement1(l2, lp, ls), ContinueStatement1(r2, rp, rs)) =>
      diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: BreakStatement, r: BreakStatement): Unit = (l, r) match {
    case (BreakStatement0(lp, ls), BreakStatement0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (BreakStatement1(l2, lp, ls), BreakStatement1(r2, rp, rs)) =>
      diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ReturnStatement, r: ReturnStatement): Unit = (l, r) match {
    case (ReturnStatement0(lp, ls), ReturnStatement0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (ReturnStatement1(l2, lp, ls), ReturnStatement1(r2, rp, rs)) =>
      diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: WithStatement, r: WithStatement): Unit = (l, r) match {
    case (WithStatement0(l2, l4, lp, ls), WithStatement0(r2, r4, rp, rs)) =>
      diff(l2, r2); diff(l4, r4); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: SwitchStatement, r: SwitchStatement): Unit = (l, r) match {
    case (SwitchStatement0(l2, l4, lp, ls), SwitchStatement0(r2, r4, rp, rs)) =>
      diff(l2, r2); diff(l4, r4); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: CaseBlock, r: CaseBlock): Unit = (l, r) match {
    case (CaseBlock0(l1, lp, ls), CaseBlock0(r1, rp, rs)) =>
      diff[CaseClauses](l1, r1, diff); diff(lp, rp); diff(ls, rs)
    case (CaseBlock1(l1, l2, l3, lp, ls), CaseBlock1(r1, r2, r3, rp, rs)) =>
      diff[CaseClauses](l1, r1, diff); diff(l2, r2); diff[CaseClauses](l3, r3, diff); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: CaseClauses, r: CaseClauses): Unit = (l, r) match {
    case (CaseClauses0(l0, lp, ls), CaseClauses0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (CaseClauses1(l0, l1, lp, ls), CaseClauses1(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: CaseClause, r: CaseClause): Unit = (l, r) match {
    case (CaseClause0(l1, l3, lp, ls), CaseClause0(r1, r3, rp, rs)) =>
      diff(l1, r1); diff[StatementList](l3, r3, diff); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: DefaultClause, r: DefaultClause): Unit = (l, r) match {
    case (DefaultClause0(l2, lp, ls), DefaultClause0(r2, rp, rs)) =>
      diff[StatementList](l2, r2, diff); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: LabelledStatement, r: LabelledStatement): Unit = (l, r) match {
    case (LabelledStatement0(l0, l2, lp, ls), LabelledStatement0(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: LabelledItem, r: LabelledItem): Unit = (l, r) match {
    case (LabelledItem0(l0, lp, ls), LabelledItem0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (LabelledItem1(l0, lp, ls), LabelledItem1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ThrowStatement, r: ThrowStatement): Unit = (l, r) match {
    case (ThrowStatement0(l2, lp, ls), ThrowStatement0(r2, rp, rs)) =>
      diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: TryStatement, r: TryStatement): Unit = (l, r) match {
    case (TryStatement0(l1, l2, lp, ls), TryStatement0(r1, r2, rp, rs)) =>
      diff(l1, r1); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (TryStatement1(l1, l2, lp, ls), TryStatement1(r1, r2, rp, rs)) =>
      diff(l1, r1); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (TryStatement2(l1, l2, l3, lp, ls), TryStatement2(r1, r2, r3, rp, rs)) =>
      diff(l1, r1); diff(l2, r2); diff(l3, r3); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: Catch, r: Catch): Unit = (l, r) match {
    case (Catch0(l2, l4, lp, ls), Catch0(r2, r4, rp, rs)) =>
      diff(l2, r2); diff(l4, r4); diff(lp, rp); diff(ls, rs)
    case (Catch1(l1, lp, ls), Catch1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: Finally, r: Finally): Unit = (l, r) match {
    case (Finally0(l1, lp, ls), Finally0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: CatchParameter, r: CatchParameter): Unit = (l, r) match {
    case (CatchParameter0(l0, lp, ls), CatchParameter0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (CatchParameter1(l0, lp, ls), CatchParameter1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: DebuggerStatement, r: DebuggerStatement): Unit = (l, r) match {
    case (DebuggerStatement0(lp, ls), DebuggerStatement0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: UniqueFormalParameters, r: UniqueFormalParameters): Unit = (l, r) match {
    case (UniqueFormalParameters0(l0, lp, ls), UniqueFormalParameters0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: FormalParameters, r: FormalParameters): Unit = (l, r) match {
    case (FormalParameters0(lp, ls), FormalParameters0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (FormalParameters1(l0, lp, ls), FormalParameters1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (FormalParameters2(l0, lp, ls), FormalParameters2(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (FormalParameters3(l0, lp, ls), FormalParameters3(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (FormalParameters4(l0, l2, lp, ls), FormalParameters4(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: FormalParameterList, r: FormalParameterList): Unit = (l, r) match {
    case (FormalParameterList0(l0, lp, ls), FormalParameterList0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (FormalParameterList1(l0, l2, lp, ls), FormalParameterList1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: FunctionRestParameter, r: FunctionRestParameter): Unit = (l, r) match {
    case (FunctionRestParameter0(l0, lp, ls), FunctionRestParameter0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: FormalParameter, r: FormalParameter): Unit = (l, r) match {
    case (FormalParameter0(l0, lp, ls), FormalParameter0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: FunctionDeclaration, r: FunctionDeclaration): Unit = (l, r) match {
    case (FunctionDeclaration0(l1, l3, l6, lp, ls), FunctionDeclaration0(r1, r3, r6, rp, rs)) =>
      diff(l1, r1); diff(l3, r3); diff(l6, r6); diff(lp, rp); diff(ls, rs)
    case (FunctionDeclaration1(l2, l5, lp, ls), FunctionDeclaration1(r2, r5, rp, rs)) =>
      diff(l2, r2); diff(l5, r5); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: FunctionExpression, r: FunctionExpression): Unit = (l, r) match {
    case (FunctionExpression0(l1, l3, l6, lp, ls), FunctionExpression0(r1, r3, r6, rp, rs)) =>
      diff[BindingIdentifier](l1, r1, diff); diff(l3, r3); diff(l6, r6); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: FunctionBody, r: FunctionBody): Unit = (l, r) match {
    case (FunctionBody0(l0, lp, ls), FunctionBody0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: FunctionStatementList, r: FunctionStatementList): Unit = (l, r) match {
    case (FunctionStatementList0(l0, lp, ls), FunctionStatementList0(r0, rp, rs)) =>
      diff[StatementList](l0, r0, diff); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ArrowFunction, r: ArrowFunction): Unit = (l, r) match {
    case (ArrowFunction0(l0, l3, lp, ls), ArrowFunction0(r0, r3, rp, rs)) =>
      diff(l0, r0); diff(l3, r3); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ArrowParameters, r: ArrowParameters): Unit = (l, r) match {
    case (ArrowParameters0(l0, lp, ls), ArrowParameters0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ArrowParameters1(l0, lp, ls), ArrowParameters1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ConciseBody, r: ConciseBody): Unit = (l, r) match {
    case (ConciseBody0(l1, lp, ls), ConciseBody0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (ConciseBody1(l1, lp, ls), ConciseBody1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ExpressionBody, r: ExpressionBody): Unit = (l, r) match {
    case (ExpressionBody0(l0, lp, ls), ExpressionBody0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ArrowFormalParameters, r: ArrowFormalParameters): Unit = (l, r) match {
    case (ArrowFormalParameters0(l1, lp, ls), ArrowFormalParameters0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: MethodDefinition, r: MethodDefinition): Unit = (l, r) match {
    case (MethodDefinition0(l0, l2, l5, lp, ls), MethodDefinition0(r0, r2, r5, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(l5, r5); diff(lp, rp); diff(ls, rs)
    case (MethodDefinition1(l0, lp, ls), MethodDefinition1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (MethodDefinition2(l0, lp, ls), MethodDefinition2(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (MethodDefinition3(l0, lp, ls), MethodDefinition3(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (MethodDefinition4(l1, l5, lp, ls), MethodDefinition4(r1, r5, rp, rs)) =>
      diff(l1, r1); diff(l5, r5); diff(lp, rp); diff(ls, rs)
    case (MethodDefinition5(l1, l3, l6, lp, ls), MethodDefinition5(r1, r3, r6, rp, rs)) =>
      diff(l1, r1); diff(l3, r3); diff(l6, r6); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: PropertySetParameterList, r: PropertySetParameterList): Unit = (l, r) match {
    case (PropertySetParameterList0(l0, lp, ls), PropertySetParameterList0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: GeneratorMethod, r: GeneratorMethod): Unit = (l, r) match {
    case (GeneratorMethod0(l1, l3, l6, lp, ls), GeneratorMethod0(r1, r3, r6, rp, rs)) =>
      diff(l1, r1); diff(l3, r3); diff(l6, r6); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: GeneratorDeclaration, r: GeneratorDeclaration): Unit = (l, r) match {
    case (GeneratorDeclaration0(l2, l4, l7, lp, ls), GeneratorDeclaration0(r2, r4, r7, rp, rs)) =>
      diff(l2, r2); diff(l4, r4); diff(l7, r7); diff(lp, rp); diff(ls, rs)
    case (GeneratorDeclaration1(l3, l6, lp, ls), GeneratorDeclaration1(r3, r6, rp, rs)) =>
      diff(l3, r3); diff(l6, r6); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: GeneratorExpression, r: GeneratorExpression): Unit = (l, r) match {
    case (GeneratorExpression0(l2, l4, l7, lp, ls), GeneratorExpression0(r2, r4, r7, rp, rs)) =>
      diff[BindingIdentifier](l2, r2, diff); diff(l4, r4); diff(l7, r7); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: GeneratorBody, r: GeneratorBody): Unit = (l, r) match {
    case (GeneratorBody0(l0, lp, ls), GeneratorBody0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: YieldExpression, r: YieldExpression): Unit = (l, r) match {
    case (YieldExpression0(lp, ls), YieldExpression0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (YieldExpression1(l2, lp, ls), YieldExpression1(r2, rp, rs)) =>
      diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (YieldExpression2(l3, lp, ls), YieldExpression2(r3, rp, rs)) =>
      diff(l3, r3); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncGeneratorMethod, r: AsyncGeneratorMethod): Unit = (l, r) match {
    case (AsyncGeneratorMethod0(l3, l5, l8, lp, ls), AsyncGeneratorMethod0(r3, r5, r8, rp, rs)) =>
      diff(l3, r3); diff(l5, r5); diff(l8, r8); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncGeneratorDeclaration, r: AsyncGeneratorDeclaration): Unit = (l, r) match {
    case (AsyncGeneratorDeclaration0(l4, l6, l9, lp, ls), AsyncGeneratorDeclaration0(r4, r6, r9, rp, rs)) =>
      diff(l4, r4); diff(l6, r6); diff(l9, r9); diff(lp, rp); diff(ls, rs)
    case (AsyncGeneratorDeclaration1(l5, l8, lp, ls), AsyncGeneratorDeclaration1(r5, r8, rp, rs)) =>
      diff(l5, r5); diff(l8, r8); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncGeneratorExpression, r: AsyncGeneratorExpression): Unit = (l, r) match {
    case (AsyncGeneratorExpression0(l4, l6, l9, lp, ls), AsyncGeneratorExpression0(r4, r6, r9, rp, rs)) =>
      diff[BindingIdentifier](l4, r4, diff); diff(l6, r6); diff(l9, r9); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncGeneratorBody, r: AsyncGeneratorBody): Unit = (l, r) match {
    case (AsyncGeneratorBody0(l0, lp, ls), AsyncGeneratorBody0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ClassDeclaration, r: ClassDeclaration): Unit = (l, r) match {
    case (ClassDeclaration0(l1, l2, lp, ls), ClassDeclaration0(r1, r2, rp, rs)) =>
      diff(l1, r1); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (ClassDeclaration1(l1, lp, ls), ClassDeclaration1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ClassExpression, r: ClassExpression): Unit = (l, r) match {
    case (ClassExpression0(l1, l2, lp, ls), ClassExpression0(r1, r2, rp, rs)) =>
      diff[BindingIdentifier](l1, r1, diff); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ClassTail, r: ClassTail): Unit = (l, r) match {
    case (ClassTail0(l0, l2, lp, ls), ClassTail0(r0, r2, rp, rs)) =>
      diff[ClassHeritage](l0, r0, diff); diff[ClassBody](l2, r2, diff); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ClassHeritage, r: ClassHeritage): Unit = (l, r) match {
    case (ClassHeritage0(l1, lp, ls), ClassHeritage0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ClassBody, r: ClassBody): Unit = (l, r) match {
    case (ClassBody0(l0, lp, ls), ClassBody0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ClassElementList, r: ClassElementList): Unit = (l, r) match {
    case (ClassElementList0(l0, lp, ls), ClassElementList0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ClassElementList1(l0, l1, lp, ls), ClassElementList1(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ClassElement, r: ClassElement): Unit = (l, r) match {
    case (ClassElement0(l0, lp, ls), ClassElement0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ClassElement1(l1, lp, ls), ClassElement1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (ClassElement2(lp, ls), ClassElement2(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncFunctionDeclaration, r: AsyncFunctionDeclaration): Unit = (l, r) match {
    case (AsyncFunctionDeclaration0(l3, l5, l8, lp, ls), AsyncFunctionDeclaration0(r3, r5, r8, rp, rs)) =>
      diff(l3, r3); diff(l5, r5); diff(l8, r8); diff(lp, rp); diff(ls, rs)
    case (AsyncFunctionDeclaration1(l4, l7, lp, ls), AsyncFunctionDeclaration1(r4, r7, rp, rs)) =>
      diff(l4, r4); diff(l7, r7); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncFunctionExpression, r: AsyncFunctionExpression): Unit = (l, r) match {
    case (AsyncFunctionExpression0(l3, l5, l8, lp, ls), AsyncFunctionExpression0(r3, r5, r8, rp, rs)) =>
      diff[BindingIdentifier](l3, r3, diff); diff(l5, r5); diff(l8, r8); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncMethod, r: AsyncMethod): Unit = (l, r) match {
    case (AsyncMethod0(l2, l4, l7, lp, ls), AsyncMethod0(r2, r4, r7, rp, rs)) =>
      diff(l2, r2); diff(l4, r4); diff(l7, r7); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncFunctionBody, r: AsyncFunctionBody): Unit = (l, r) match {
    case (AsyncFunctionBody0(l0, lp, ls), AsyncFunctionBody0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AwaitExpression, r: AwaitExpression): Unit = (l, r) match {
    case (AwaitExpression0(l1, lp, ls), AwaitExpression0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncArrowFunction, r: AsyncArrowFunction): Unit = (l, r) match {
    case (AsyncArrowFunction0(l2, l5, lp, ls), AsyncArrowFunction0(r2, r5, rp, rs)) =>
      diff(l2, r2); diff(l5, r5); diff(lp, rp); diff(ls, rs)
    case (AsyncArrowFunction1(l0, l3, lp, ls), AsyncArrowFunction1(r0, r3, rp, rs)) =>
      diff(l0, r0); diff(l3, r3); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncConciseBody, r: AsyncConciseBody): Unit = (l, r) match {
    case (AsyncConciseBody0(l1, lp, ls), AsyncConciseBody0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (AsyncConciseBody1(l1, lp, ls), AsyncConciseBody1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncArrowBindingIdentifier, r: AsyncArrowBindingIdentifier): Unit = (l, r) match {
    case (AsyncArrowBindingIdentifier0(l0, lp, ls), AsyncArrowBindingIdentifier0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: CoverCallExpressionAndAsyncArrowHead, r: CoverCallExpressionAndAsyncArrowHead): Unit = (l, r) match {
    case (CoverCallExpressionAndAsyncArrowHead0(l0, l1, lp, ls), CoverCallExpressionAndAsyncArrowHead0(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncArrowHead, r: AsyncArrowHead): Unit = (l, r) match {
    case (AsyncArrowHead0(l2, lp, ls), AsyncArrowHead0(r2, rp, rs)) =>
      diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: Script, r: Script): Unit = (l, r) match {
    case (Script0(l0, lp, ls), Script0(r0, rp, rs)) =>
      diff[ScriptBody](l0, r0, diff); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ScriptBody, r: ScriptBody): Unit = (l, r) match {
    case (ScriptBody0(l0, lp, ls), ScriptBody0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: Module, r: Module): Unit = (l, r) match {
    case (Module0(l0, lp, ls), Module0(r0, rp, rs)) =>
      diff[ModuleBody](l0, r0, diff); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ModuleBody, r: ModuleBody): Unit = (l, r) match {
    case (ModuleBody0(l0, lp, ls), ModuleBody0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ModuleItemList, r: ModuleItemList): Unit = (l, r) match {
    case (ModuleItemList0(l0, lp, ls), ModuleItemList0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ModuleItemList1(l0, l1, lp, ls), ModuleItemList1(r0, r1, rp, rs)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ModuleItem, r: ModuleItem): Unit = (l, r) match {
    case (ModuleItem0(l0, lp, ls), ModuleItem0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ModuleItem1(l0, lp, ls), ModuleItem1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ModuleItem2(l0, lp, ls), ModuleItem2(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ImportDeclaration, r: ImportDeclaration): Unit = (l, r) match {
    case (ImportDeclaration0(l1, l2, lp, ls), ImportDeclaration0(r1, r2, rp, rs)) =>
      diff(l1, r1); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (ImportDeclaration1(l1, lp, ls), ImportDeclaration1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ImportClause, r: ImportClause): Unit = (l, r) match {
    case (ImportClause0(l0, lp, ls), ImportClause0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ImportClause1(l0, lp, ls), ImportClause1(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ImportClause2(l0, lp, ls), ImportClause2(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ImportClause3(l0, l2, lp, ls), ImportClause3(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (ImportClause4(l0, l2, lp, ls), ImportClause4(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ImportedDefaultBinding, r: ImportedDefaultBinding): Unit = (l, r) match {
    case (ImportedDefaultBinding0(l0, lp, ls), ImportedDefaultBinding0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: NameSpaceImport, r: NameSpaceImport): Unit = (l, r) match {
    case (NameSpaceImport0(l2, lp, ls), NameSpaceImport0(r2, rp, rs)) =>
      diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: NamedImports, r: NamedImports): Unit = (l, r) match {
    case (NamedImports0(lp, ls), NamedImports0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (NamedImports1(l1, lp, ls), NamedImports1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (NamedImports2(l1, lp, ls), NamedImports2(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: FromClause, r: FromClause): Unit = (l, r) match {
    case (FromClause0(l1, lp, ls), FromClause0(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ImportsList, r: ImportsList): Unit = (l, r) match {
    case (ImportsList0(l0, lp, ls), ImportsList0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ImportsList1(l0, l2, lp, ls), ImportsList1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ImportSpecifier, r: ImportSpecifier): Unit = (l, r) match {
    case (ImportSpecifier0(l0, lp, ls), ImportSpecifier0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ImportSpecifier1(l0, l2, lp, ls), ImportSpecifier1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ModuleSpecifier, r: ModuleSpecifier): Unit = (l, r) match {
    case (ModuleSpecifier0(l0, lp, ls), ModuleSpecifier0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ImportedBinding, r: ImportedBinding): Unit = (l, r) match {
    case (ImportedBinding0(l0, lp, ls), ImportedBinding0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ExportDeclaration, r: ExportDeclaration): Unit = (l, r) match {
    case (ExportDeclaration0(l1, l2, lp, ls), ExportDeclaration0(r1, r2, rp, rs)) =>
      diff(l1, r1); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (ExportDeclaration1(l1, lp, ls), ExportDeclaration1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (ExportDeclaration2(l1, lp, ls), ExportDeclaration2(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (ExportDeclaration3(l1, lp, ls), ExportDeclaration3(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (ExportDeclaration4(l2, lp, ls), ExportDeclaration4(r2, rp, rs)) =>
      diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (ExportDeclaration5(l2, lp, ls), ExportDeclaration5(r2, rp, rs)) =>
      diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (ExportDeclaration6(l3, lp, ls), ExportDeclaration6(r3, rp, rs)) =>
      diff(l3, r3); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ExportFromClause, r: ExportFromClause): Unit = (l, r) match {
    case (ExportFromClause0(lp, ls), ExportFromClause0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (ExportFromClause1(l2, lp, ls), ExportFromClause1(r2, rp, rs)) =>
      diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case (ExportFromClause2(l0, lp, ls), ExportFromClause2(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: NamedExports, r: NamedExports): Unit = (l, r) match {
    case (NamedExports0(lp, ls), NamedExports0(rp, rs)) =>
      diff(lp, rp); diff(ls, rs)
    case (NamedExports1(l1, lp, ls), NamedExports1(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case (NamedExports2(l1, lp, ls), NamedExports2(r1, rp, rs)) =>
      diff(l1, r1); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ExportsList, r: ExportsList): Unit = (l, r) match {
    case (ExportsList0(l0, lp, ls), ExportsList0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ExportsList1(l0, l2, lp, ls), ExportsList1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
  def diff(l: ExportSpecifier, r: ExportSpecifier): Unit = (l, r) match {
    case (ExportSpecifier0(l0, lp, ls), ExportSpecifier0(r0, rp, rs)) =>
      diff(l0, r0); diff(lp, rp); diff(ls, rs)
    case (ExportSpecifier1(l0, l2, lp, ls), ExportSpecifier1(r0, r2, rp, rs)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp); diff(ls, rs)
    case _ => diffError(l, r)
  }
}

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
  def diff(l: List[Boolean], r: List[Boolean]): Unit = if (l != r) diffError(l, r)
  def diff(l: Lexical, r: Lexical): Unit = {
    if (l.kind != r.kind) diffError(l.kind, r.kind)
    if (l.str != r.str) diffError(l.str, r.str)
  }

  def diff(l: IdentifierReference, r: IdentifierReference): Unit = (l, r) match {
    case (IdentifierReference0(l0, lp, _), IdentifierReference0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (IdentifierReference1(lp, _), IdentifierReference1(rp, _)) =>
      diff(lp, rp)
    case (IdentifierReference2(lp, _), IdentifierReference2(rp, _)) =>
      diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: BindingIdentifier, r: BindingIdentifier): Unit = (l, r) match {
    case (BindingIdentifier0(l0, lp, _), BindingIdentifier0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (BindingIdentifier1(lp, _), BindingIdentifier1(rp, _)) =>
      diff(lp, rp)
    case (BindingIdentifier2(lp, _), BindingIdentifier2(rp, _)) =>
      diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: LabelIdentifier, r: LabelIdentifier): Unit = (l, r) match {
    case (LabelIdentifier0(l0, lp, _), LabelIdentifier0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (LabelIdentifier1(lp, _), LabelIdentifier1(rp, _)) =>
      diff(lp, rp)
    case (LabelIdentifier2(lp, _), LabelIdentifier2(rp, _)) =>
      diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: Identifier, r: Identifier): Unit = (l, r) match {
    case (Identifier0(l0, lp, _), Identifier0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: PrimaryExpression, r: PrimaryExpression): Unit = (l, r) match {
    case (PrimaryExpression0(lp, _), PrimaryExpression0(rp, _)) =>
      diff(lp, rp)
    case (PrimaryExpression1(l0, lp, _), PrimaryExpression1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PrimaryExpression2(l0, lp, _), PrimaryExpression2(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PrimaryExpression3(l0, lp, _), PrimaryExpression3(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PrimaryExpression4(l0, lp, _), PrimaryExpression4(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PrimaryExpression5(l0, lp, _), PrimaryExpression5(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PrimaryExpression6(l0, lp, _), PrimaryExpression6(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PrimaryExpression7(l0, lp, _), PrimaryExpression7(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PrimaryExpression8(l0, lp, _), PrimaryExpression8(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PrimaryExpression9(l0, lp, _), PrimaryExpression9(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PrimaryExpression10(l0, lp, _), PrimaryExpression10(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PrimaryExpression11(l0, lp, _), PrimaryExpression11(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PrimaryExpression12(l0, lp, _), PrimaryExpression12(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: CoverParenthesizedExpressionAndArrowParameterList, r: CoverParenthesizedExpressionAndArrowParameterList): Unit = (l, r) match {
    case (CoverParenthesizedExpressionAndArrowParameterList0(l1, lp, _), CoverParenthesizedExpressionAndArrowParameterList0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (CoverParenthesizedExpressionAndArrowParameterList1(l1, lp, _), CoverParenthesizedExpressionAndArrowParameterList1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (CoverParenthesizedExpressionAndArrowParameterList2(lp, _), CoverParenthesizedExpressionAndArrowParameterList2(rp, _)) =>
      diff(lp, rp)
    case (CoverParenthesizedExpressionAndArrowParameterList3(l2, lp, _), CoverParenthesizedExpressionAndArrowParameterList3(r2, rp, _)) =>
      diff(l2, r2); diff(lp, rp)
    case (CoverParenthesizedExpressionAndArrowParameterList4(l2, lp, _), CoverParenthesizedExpressionAndArrowParameterList4(r2, rp, _)) =>
      diff(l2, r2); diff(lp, rp)
    case (CoverParenthesizedExpressionAndArrowParameterList5(l1, l4, lp, _), CoverParenthesizedExpressionAndArrowParameterList5(r1, r4, rp, _)) =>
      diff(l1, r1); diff(l4, r4); diff(lp, rp)
    case (CoverParenthesizedExpressionAndArrowParameterList6(l1, l4, lp, _), CoverParenthesizedExpressionAndArrowParameterList6(r1, r4, rp, _)) =>
      diff(l1, r1); diff(l4, r4); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ParenthesizedExpression, r: ParenthesizedExpression): Unit = (l, r) match {
    case (ParenthesizedExpression0(l1, lp, _), ParenthesizedExpression0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: Literal, r: Literal): Unit = (l, r) match {
    case (Literal0(l0, lp, _), Literal0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Literal1(l0, lp, _), Literal1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Literal2(l0, lp, _), Literal2(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Literal3(l0, lp, _), Literal3(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ArrayLiteral, r: ArrayLiteral): Unit = (l, r) match {
    case (ArrayLiteral0(l1, lp, _), ArrayLiteral0(r1, rp, _)) =>
      diff[Elision](l1, r1, diff); diff(lp, rp)
    case (ArrayLiteral1(l1, lp, _), ArrayLiteral1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (ArrayLiteral2(l1, l3, lp, _), ArrayLiteral2(r1, r3, rp, _)) =>
      diff(l1, r1); diff[Elision](l3, r3, diff); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ElementList, r: ElementList): Unit = (l, r) match {
    case (ElementList0(l0, l1, lp, _), ElementList0(r0, r1, rp, _)) =>
      diff[Elision](l0, r0, diff); diff(l1, r1); diff(lp, rp)
    case (ElementList1(l0, l1, lp, _), ElementList1(r0, r1, rp, _)) =>
      diff[Elision](l0, r0, diff); diff(l1, r1); diff(lp, rp)
    case (ElementList2(l0, l2, l3, lp, _), ElementList2(r0, r2, r3, rp, _)) =>
      diff(l0, r0); diff[Elision](l2, r2, diff); diff(l3, r3); diff(lp, rp)
    case (ElementList3(l0, l2, l3, lp, _), ElementList3(r0, r2, r3, rp, _)) =>
      diff(l0, r0); diff[Elision](l2, r2, diff); diff(l3, r3); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: Elision, r: Elision): Unit = (l, r) match {
    case (Elision0(lp, _), Elision0(rp, _)) =>
      diff(lp, rp)
    case (Elision1(l0, lp, _), Elision1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: SpreadElement, r: SpreadElement): Unit = (l, r) match {
    case (SpreadElement0(l1, lp, _), SpreadElement0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ObjectLiteral, r: ObjectLiteral): Unit = (l, r) match {
    case (ObjectLiteral0(lp, _), ObjectLiteral0(rp, _)) =>
      diff(lp, rp)
    case (ObjectLiteral1(l1, lp, _), ObjectLiteral1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (ObjectLiteral2(l1, lp, _), ObjectLiteral2(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: PropertyDefinitionList, r: PropertyDefinitionList): Unit = (l, r) match {
    case (PropertyDefinitionList0(l0, lp, _), PropertyDefinitionList0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PropertyDefinitionList1(l0, l2, lp, _), PropertyDefinitionList1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: PropertyDefinition, r: PropertyDefinition): Unit = (l, r) match {
    case (PropertyDefinition0(l0, lp, _), PropertyDefinition0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PropertyDefinition1(l0, lp, _), PropertyDefinition1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PropertyDefinition2(l0, l2, lp, _), PropertyDefinition2(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (PropertyDefinition3(l0, lp, _), PropertyDefinition3(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PropertyDefinition4(l1, lp, _), PropertyDefinition4(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: PropertyName, r: PropertyName): Unit = (l, r) match {
    case (PropertyName0(l0, lp, _), PropertyName0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PropertyName1(l0, lp, _), PropertyName1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: LiteralPropertyName, r: LiteralPropertyName): Unit = (l, r) match {
    case (LiteralPropertyName0(l0, lp, _), LiteralPropertyName0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (LiteralPropertyName1(l0, lp, _), LiteralPropertyName1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (LiteralPropertyName2(l0, lp, _), LiteralPropertyName2(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ComputedPropertyName, r: ComputedPropertyName): Unit = (l, r) match {
    case (ComputedPropertyName0(l1, lp, _), ComputedPropertyName0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: CoverInitializedName, r: CoverInitializedName): Unit = (l, r) match {
    case (CoverInitializedName0(l0, l1, lp, _), CoverInitializedName0(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: Initializer, r: Initializer): Unit = (l, r) match {
    case (Initializer0(l1, lp, _), Initializer0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: TemplateLiteral, r: TemplateLiteral): Unit = (l, r) match {
    case (TemplateLiteral0(l0, lp, _), TemplateLiteral0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (TemplateLiteral1(l0, lp, _), TemplateLiteral1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: SubstitutionTemplate, r: SubstitutionTemplate): Unit = (l, r) match {
    case (SubstitutionTemplate0(l0, l1, l2, lp, _), SubstitutionTemplate0(r0, r1, r2, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: TemplateSpans, r: TemplateSpans): Unit = (l, r) match {
    case (TemplateSpans0(l0, lp, _), TemplateSpans0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (TemplateSpans1(l0, l1, lp, _), TemplateSpans1(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: TemplateMiddleList, r: TemplateMiddleList): Unit = (l, r) match {
    case (TemplateMiddleList0(l0, l1, lp, _), TemplateMiddleList0(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case (TemplateMiddleList1(l0, l1, l2, lp, _), TemplateMiddleList1(r0, r1, r2, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: MemberExpression, r: MemberExpression): Unit = (l, r) match {
    case (MemberExpression0(l0, lp, _), MemberExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (MemberExpression1(l0, l2, lp, _), MemberExpression1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (MemberExpression2(l0, l2, lp, _), MemberExpression2(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (MemberExpression3(l0, l1, lp, _), MemberExpression3(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case (MemberExpression4(l0, lp, _), MemberExpression4(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (MemberExpression5(l0, lp, _), MemberExpression5(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (MemberExpression6(l1, l2, lp, _), MemberExpression6(r1, r2, rp, _)) =>
      diff(l1, r1); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: SuperProperty, r: SuperProperty): Unit = (l, r) match {
    case (SuperProperty0(l2, lp, _), SuperProperty0(r2, rp, _)) =>
      diff(l2, r2); diff(lp, rp)
    case (SuperProperty1(l2, lp, _), SuperProperty1(r2, rp, _)) =>
      diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: MetaProperty, r: MetaProperty): Unit = (l, r) match {
    case (MetaProperty0(l0, lp, _), MetaProperty0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (MetaProperty1(l0, lp, _), MetaProperty1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: NewTarget, r: NewTarget): Unit = (l, r) match {
    case (NewTarget0(lp, _), NewTarget0(rp, _)) =>
      diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ImportMeta, r: ImportMeta): Unit = (l, r) match {
    case (ImportMeta0(lp, _), ImportMeta0(rp, _)) =>
      diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: NewExpression, r: NewExpression): Unit = (l, r) match {
    case (NewExpression0(l0, lp, _), NewExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (NewExpression1(l1, lp, _), NewExpression1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: CallExpression, r: CallExpression): Unit = (l, r) match {
    case (CallExpression0(l0, lp, _), CallExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (CallExpression1(l0, lp, _), CallExpression1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (CallExpression2(l0, lp, _), CallExpression2(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (CallExpression3(l0, l1, lp, _), CallExpression3(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case (CallExpression4(l0, l2, lp, _), CallExpression4(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (CallExpression5(l0, l2, lp, _), CallExpression5(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (CallExpression6(l0, l1, lp, _), CallExpression6(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: SuperCall, r: SuperCall): Unit = (l, r) match {
    case (SuperCall0(l1, lp, _), SuperCall0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ImportCall, r: ImportCall): Unit = (l, r) match {
    case (ImportCall0(l2, lp, _), ImportCall0(r2, rp, _)) =>
      diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: Arguments, r: Arguments): Unit = (l, r) match {
    case (Arguments0(lp, _), Arguments0(rp, _)) =>
      diff(lp, rp)
    case (Arguments1(l1, lp, _), Arguments1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (Arguments2(l1, lp, _), Arguments2(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ArgumentList, r: ArgumentList): Unit = (l, r) match {
    case (ArgumentList0(l0, lp, _), ArgumentList0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ArgumentList1(l1, lp, _), ArgumentList1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (ArgumentList2(l0, l2, lp, _), ArgumentList2(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (ArgumentList3(l0, l3, lp, _), ArgumentList3(r0, r3, rp, _)) =>
      diff(l0, r0); diff(l3, r3); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: OptionalExpression, r: OptionalExpression): Unit = (l, r) match {
    case (OptionalExpression0(l0, l1, lp, _), OptionalExpression0(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case (OptionalExpression1(l0, l1, lp, _), OptionalExpression1(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case (OptionalExpression2(l0, l1, lp, _), OptionalExpression2(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: OptionalChain, r: OptionalChain): Unit = (l, r) match {
    case (OptionalChain0(l1, lp, _), OptionalChain0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (OptionalChain1(l2, lp, _), OptionalChain1(r2, rp, _)) =>
      diff(l2, r2); diff(lp, rp)
    case (OptionalChain2(l1, lp, _), OptionalChain2(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (OptionalChain3(l1, lp, _), OptionalChain3(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (OptionalChain4(l0, l1, lp, _), OptionalChain4(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case (OptionalChain5(l0, l2, lp, _), OptionalChain5(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (OptionalChain6(l0, l2, lp, _), OptionalChain6(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (OptionalChain7(l0, l1, lp, _), OptionalChain7(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: LeftHandSideExpression, r: LeftHandSideExpression): Unit = (l, r) match {
    case (LeftHandSideExpression0(l0, lp, _), LeftHandSideExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (LeftHandSideExpression1(l0, lp, _), LeftHandSideExpression1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (LeftHandSideExpression2(l0, lp, _), LeftHandSideExpression2(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: CallMemberExpression, r: CallMemberExpression): Unit = (l, r) match {
    case (CallMemberExpression0(l0, l1, lp, _), CallMemberExpression0(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: UpdateExpression, r: UpdateExpression): Unit = (l, r) match {
    case (UpdateExpression0(l0, lp, _), UpdateExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (UpdateExpression1(l0, lp, _), UpdateExpression1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (UpdateExpression2(l0, lp, _), UpdateExpression2(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (UpdateExpression3(l1, lp, _), UpdateExpression3(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (UpdateExpression4(l1, lp, _), UpdateExpression4(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: UnaryExpression, r: UnaryExpression): Unit = (l, r) match {
    case (UnaryExpression0(l0, lp, _), UnaryExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (UnaryExpression1(l1, lp, _), UnaryExpression1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (UnaryExpression2(l1, lp, _), UnaryExpression2(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (UnaryExpression3(l1, lp, _), UnaryExpression3(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (UnaryExpression4(l1, lp, _), UnaryExpression4(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (UnaryExpression5(l1, lp, _), UnaryExpression5(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (UnaryExpression6(l1, lp, _), UnaryExpression6(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (UnaryExpression7(l1, lp, _), UnaryExpression7(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (UnaryExpression8(l0, lp, _), UnaryExpression8(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ExponentiationExpression, r: ExponentiationExpression): Unit = (l, r) match {
    case (ExponentiationExpression0(l0, lp, _), ExponentiationExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ExponentiationExpression1(l0, l2, lp, _), ExponentiationExpression1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: MultiplicativeExpression, r: MultiplicativeExpression): Unit = (l, r) match {
    case (MultiplicativeExpression0(l0, lp, _), MultiplicativeExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (MultiplicativeExpression1(l0, l1, l2, lp, _), MultiplicativeExpression1(r0, r1, r2, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: MultiplicativeOperator, r: MultiplicativeOperator): Unit = (l, r) match {
    case (MultiplicativeOperator0(lp, _), MultiplicativeOperator0(rp, _)) =>
      diff(lp, rp)
    case (MultiplicativeOperator1(lp, _), MultiplicativeOperator1(rp, _)) =>
      diff(lp, rp)
    case (MultiplicativeOperator2(lp, _), MultiplicativeOperator2(rp, _)) =>
      diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AdditiveExpression, r: AdditiveExpression): Unit = (l, r) match {
    case (AdditiveExpression0(l0, lp, _), AdditiveExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (AdditiveExpression1(l0, l2, lp, _), AdditiveExpression1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (AdditiveExpression2(l0, l2, lp, _), AdditiveExpression2(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ShiftExpression, r: ShiftExpression): Unit = (l, r) match {
    case (ShiftExpression0(l0, lp, _), ShiftExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ShiftExpression1(l0, l2, lp, _), ShiftExpression1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (ShiftExpression2(l0, l2, lp, _), ShiftExpression2(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (ShiftExpression3(l0, l2, lp, _), ShiftExpression3(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: RelationalExpression, r: RelationalExpression): Unit = (l, r) match {
    case (RelationalExpression0(l0, lp, _), RelationalExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (RelationalExpression1(l0, l2, lp, _), RelationalExpression1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (RelationalExpression2(l0, l2, lp, _), RelationalExpression2(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (RelationalExpression3(l0, l2, lp, _), RelationalExpression3(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (RelationalExpression4(l0, l2, lp, _), RelationalExpression4(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (RelationalExpression5(l0, l2, lp, _), RelationalExpression5(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (RelationalExpression6(l0, l2, lp, _), RelationalExpression6(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: EqualityExpression, r: EqualityExpression): Unit = (l, r) match {
    case (EqualityExpression0(l0, lp, _), EqualityExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (EqualityExpression1(l0, l2, lp, _), EqualityExpression1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (EqualityExpression2(l0, l2, lp, _), EqualityExpression2(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (EqualityExpression3(l0, l2, lp, _), EqualityExpression3(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (EqualityExpression4(l0, l2, lp, _), EqualityExpression4(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: BitwiseANDExpression, r: BitwiseANDExpression): Unit = (l, r) match {
    case (BitwiseANDExpression0(l0, lp, _), BitwiseANDExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (BitwiseANDExpression1(l0, l2, lp, _), BitwiseANDExpression1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: BitwiseXORExpression, r: BitwiseXORExpression): Unit = (l, r) match {
    case (BitwiseXORExpression0(l0, lp, _), BitwiseXORExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (BitwiseXORExpression1(l0, l2, lp, _), BitwiseXORExpression1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: BitwiseORExpression, r: BitwiseORExpression): Unit = (l, r) match {
    case (BitwiseORExpression0(l0, lp, _), BitwiseORExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (BitwiseORExpression1(l0, l2, lp, _), BitwiseORExpression1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: LogicalANDExpression, r: LogicalANDExpression): Unit = (l, r) match {
    case (LogicalANDExpression0(l0, lp, _), LogicalANDExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (LogicalANDExpression1(l0, l2, lp, _), LogicalANDExpression1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: LogicalORExpression, r: LogicalORExpression): Unit = (l, r) match {
    case (LogicalORExpression0(l0, lp, _), LogicalORExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (LogicalORExpression1(l0, l2, lp, _), LogicalORExpression1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: CoalesceExpression, r: CoalesceExpression): Unit = (l, r) match {
    case (CoalesceExpression0(l0, l2, lp, _), CoalesceExpression0(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: CoalesceExpressionHead, r: CoalesceExpressionHead): Unit = (l, r) match {
    case (CoalesceExpressionHead0(l0, lp, _), CoalesceExpressionHead0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (CoalesceExpressionHead1(l0, lp, _), CoalesceExpressionHead1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: PipelineExpression, r: PipelineExpression): Unit = (l, r) match {
    case (PipelineExpression0(l0, lp, _), PipelineExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (PipelineExpression1(l0, l2, lp, _), PipelineExpression1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ShortCircuitExpression, r: ShortCircuitExpression): Unit = (l, r) match {
    case (ShortCircuitExpression0(l0, lp, _), ShortCircuitExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ShortCircuitExpression1(l0, lp, _), ShortCircuitExpression1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ConditionalExpression, r: ConditionalExpression): Unit = (l, r) match {
    case (ConditionalExpression0(l0, lp, _), ConditionalExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ConditionalExpression1(l0, l2, l4, lp, _), ConditionalExpression1(r0, r2, r4, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(l4, r4); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentExpression, r: AssignmentExpression): Unit = (l, r) match {
    case (AssignmentExpression0(l0, lp, _), AssignmentExpression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (AssignmentExpression1(l0, lp, _), AssignmentExpression1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (AssignmentExpression2(l0, lp, _), AssignmentExpression2(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (AssignmentExpression3(l0, lp, _), AssignmentExpression3(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (AssignmentExpression4(l0, l2, lp, _), AssignmentExpression4(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (AssignmentExpression5(l0, l1, l2, lp, _), AssignmentExpression5(r0, r1, r2, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(l2, r2); diff(lp, rp)
    case (AssignmentExpression6(l0, l2, lp, _), AssignmentExpression6(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (AssignmentExpression7(l0, l2, lp, _), AssignmentExpression7(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (AssignmentExpression8(l0, l2, lp, _), AssignmentExpression8(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentOperator, r: AssignmentOperator): Unit = (l, r) match {
    case (AssignmentOperator0(lp, _), AssignmentOperator0(rp, _)) =>
      diff(lp, rp)
    case (AssignmentOperator1(lp, _), AssignmentOperator1(rp, _)) =>
      diff(lp, rp)
    case (AssignmentOperator2(lp, _), AssignmentOperator2(rp, _)) =>
      diff(lp, rp)
    case (AssignmentOperator3(lp, _), AssignmentOperator3(rp, _)) =>
      diff(lp, rp)
    case (AssignmentOperator4(lp, _), AssignmentOperator4(rp, _)) =>
      diff(lp, rp)
    case (AssignmentOperator5(lp, _), AssignmentOperator5(rp, _)) =>
      diff(lp, rp)
    case (AssignmentOperator6(lp, _), AssignmentOperator6(rp, _)) =>
      diff(lp, rp)
    case (AssignmentOperator7(lp, _), AssignmentOperator7(rp, _)) =>
      diff(lp, rp)
    case (AssignmentOperator8(lp, _), AssignmentOperator8(rp, _)) =>
      diff(lp, rp)
    case (AssignmentOperator9(lp, _), AssignmentOperator9(rp, _)) =>
      diff(lp, rp)
    case (AssignmentOperator10(lp, _), AssignmentOperator10(rp, _)) =>
      diff(lp, rp)
    case (AssignmentOperator11(lp, _), AssignmentOperator11(rp, _)) =>
      diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentPattern, r: AssignmentPattern): Unit = (l, r) match {
    case (AssignmentPattern0(l0, lp, _), AssignmentPattern0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (AssignmentPattern1(l0, lp, _), AssignmentPattern1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ObjectAssignmentPattern, r: ObjectAssignmentPattern): Unit = (l, r) match {
    case (ObjectAssignmentPattern0(lp, _), ObjectAssignmentPattern0(rp, _)) =>
      diff(lp, rp)
    case (ObjectAssignmentPattern1(l1, lp, _), ObjectAssignmentPattern1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (ObjectAssignmentPattern2(l1, lp, _), ObjectAssignmentPattern2(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (ObjectAssignmentPattern3(l1, l3, lp, _), ObjectAssignmentPattern3(r1, r3, rp, _)) =>
      diff(l1, r1); diff[AssignmentRestProperty](l3, r3, diff); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ArrayAssignmentPattern, r: ArrayAssignmentPattern): Unit = (l, r) match {
    case (ArrayAssignmentPattern0(l1, l2, lp, _), ArrayAssignmentPattern0(r1, r2, rp, _)) =>
      diff[Elision](l1, r1, diff); diff[AssignmentRestElement](l2, r2, diff); diff(lp, rp)
    case (ArrayAssignmentPattern1(l1, lp, _), ArrayAssignmentPattern1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (ArrayAssignmentPattern2(l1, l3, l4, lp, _), ArrayAssignmentPattern2(r1, r3, r4, rp, _)) =>
      diff(l1, r1); diff[Elision](l3, r3, diff); diff[AssignmentRestElement](l4, r4, diff); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentRestProperty, r: AssignmentRestProperty): Unit = (l, r) match {
    case (AssignmentRestProperty0(l1, lp, _), AssignmentRestProperty0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentPropertyList, r: AssignmentPropertyList): Unit = (l, r) match {
    case (AssignmentPropertyList0(l0, lp, _), AssignmentPropertyList0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (AssignmentPropertyList1(l0, l2, lp, _), AssignmentPropertyList1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentElementList, r: AssignmentElementList): Unit = (l, r) match {
    case (AssignmentElementList0(l0, lp, _), AssignmentElementList0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (AssignmentElementList1(l0, l2, lp, _), AssignmentElementList1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentElisionElement, r: AssignmentElisionElement): Unit = (l, r) match {
    case (AssignmentElisionElement0(l0, l1, lp, _), AssignmentElisionElement0(r0, r1, rp, _)) =>
      diff[Elision](l0, r0, diff); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentProperty, r: AssignmentProperty): Unit = (l, r) match {
    case (AssignmentProperty0(l0, l1, lp, _), AssignmentProperty0(r0, r1, rp, _)) =>
      diff(l0, r0); diff[Initializer](l1, r1, diff); diff(lp, rp)
    case (AssignmentProperty1(l0, l2, lp, _), AssignmentProperty1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentElement, r: AssignmentElement): Unit = (l, r) match {
    case (AssignmentElement0(l0, l1, lp, _), AssignmentElement0(r0, r1, rp, _)) =>
      diff(l0, r0); diff[Initializer](l1, r1, diff); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AssignmentRestElement, r: AssignmentRestElement): Unit = (l, r) match {
    case (AssignmentRestElement0(l1, lp, _), AssignmentRestElement0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: DestructuringAssignmentTarget, r: DestructuringAssignmentTarget): Unit = (l, r) match {
    case (DestructuringAssignmentTarget0(l0, lp, _), DestructuringAssignmentTarget0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: Expression, r: Expression): Unit = (l, r) match {
    case (Expression0(l0, lp, _), Expression0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Expression1(l0, l2, lp, _), Expression1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: Statement, r: Statement): Unit = (l, r) match {
    case (Statement0(l0, lp, _), Statement0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Statement1(l0, lp, _), Statement1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Statement2(l0, lp, _), Statement2(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Statement3(l0, lp, _), Statement3(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Statement4(l0, lp, _), Statement4(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Statement5(l0, lp, _), Statement5(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Statement6(l0, lp, _), Statement6(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Statement7(l0, lp, _), Statement7(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Statement8(l0, lp, _), Statement8(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Statement9(l0, lp, _), Statement9(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Statement10(l0, lp, _), Statement10(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Statement11(l0, lp, _), Statement11(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Statement12(l0, lp, _), Statement12(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Statement13(l0, lp, _), Statement13(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: Declaration, r: Declaration): Unit = (l, r) match {
    case (Declaration0(l0, lp, _), Declaration0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Declaration1(l0, lp, _), Declaration1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (Declaration2(l0, lp, _), Declaration2(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: HoistableDeclaration, r: HoistableDeclaration): Unit = (l, r) match {
    case (HoistableDeclaration0(l0, lp, _), HoistableDeclaration0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (HoistableDeclaration1(l0, lp, _), HoistableDeclaration1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (HoistableDeclaration2(l0, lp, _), HoistableDeclaration2(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (HoistableDeclaration3(l0, lp, _), HoistableDeclaration3(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: BreakableStatement, r: BreakableStatement): Unit = (l, r) match {
    case (BreakableStatement0(l0, lp, _), BreakableStatement0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (BreakableStatement1(l0, lp, _), BreakableStatement1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: BlockStatement, r: BlockStatement): Unit = (l, r) match {
    case (BlockStatement0(l0, lp, _), BlockStatement0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: Block, r: Block): Unit = (l, r) match {
    case (Block0(l1, lp, _), Block0(r1, rp, _)) =>
      diff[StatementList](l1, r1, diff); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: StatementList, r: StatementList): Unit = (l, r) match {
    case (StatementList0(l0, lp, _), StatementList0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (StatementList1(l0, l1, lp, _), StatementList1(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: StatementListItem, r: StatementListItem): Unit = (l, r) match {
    case (StatementListItem0(l0, lp, _), StatementListItem0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (StatementListItem1(l0, lp, _), StatementListItem1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: LexicalDeclaration, r: LexicalDeclaration): Unit = (l, r) match {
    case (LexicalDeclaration0(l0, l1, lp, _), LexicalDeclaration0(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: LetOrConst, r: LetOrConst): Unit = (l, r) match {
    case (LetOrConst0(lp, _), LetOrConst0(rp, _)) =>
      diff(lp, rp)
    case (LetOrConst1(lp, _), LetOrConst1(rp, _)) =>
      diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: BindingList, r: BindingList): Unit = (l, r) match {
    case (BindingList0(l0, lp, _), BindingList0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (BindingList1(l0, l2, lp, _), BindingList1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: LexicalBinding, r: LexicalBinding): Unit = (l, r) match {
    case (LexicalBinding0(l0, l1, lp, _), LexicalBinding0(r0, r1, rp, _)) =>
      diff(l0, r0); diff[Initializer](l1, r1, diff); diff(lp, rp)
    case (LexicalBinding1(l0, l1, lp, _), LexicalBinding1(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: VariableStatement, r: VariableStatement): Unit = (l, r) match {
    case (VariableStatement0(l1, lp, _), VariableStatement0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: VariableDeclarationList, r: VariableDeclarationList): Unit = (l, r) match {
    case (VariableDeclarationList0(l0, lp, _), VariableDeclarationList0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (VariableDeclarationList1(l0, l2, lp, _), VariableDeclarationList1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: VariableDeclaration, r: VariableDeclaration): Unit = (l, r) match {
    case (VariableDeclaration0(l0, l1, lp, _), VariableDeclaration0(r0, r1, rp, _)) =>
      diff(l0, r0); diff[Initializer](l1, r1, diff); diff(lp, rp)
    case (VariableDeclaration1(l0, l1, lp, _), VariableDeclaration1(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: BindingPattern, r: BindingPattern): Unit = (l, r) match {
    case (BindingPattern0(l0, lp, _), BindingPattern0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (BindingPattern1(l0, lp, _), BindingPattern1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ObjectBindingPattern, r: ObjectBindingPattern): Unit = (l, r) match {
    case (ObjectBindingPattern0(lp, _), ObjectBindingPattern0(rp, _)) =>
      diff(lp, rp)
    case (ObjectBindingPattern1(l1, lp, _), ObjectBindingPattern1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (ObjectBindingPattern2(l1, lp, _), ObjectBindingPattern2(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (ObjectBindingPattern3(l1, l3, lp, _), ObjectBindingPattern3(r1, r3, rp, _)) =>
      diff(l1, r1); diff[BindingRestProperty](l3, r3, diff); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ArrayBindingPattern, r: ArrayBindingPattern): Unit = (l, r) match {
    case (ArrayBindingPattern0(l1, l2, lp, _), ArrayBindingPattern0(r1, r2, rp, _)) =>
      diff[Elision](l1, r1, diff); diff[BindingRestElement](l2, r2, diff); diff(lp, rp)
    case (ArrayBindingPattern1(l1, lp, _), ArrayBindingPattern1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (ArrayBindingPattern2(l1, l3, l4, lp, _), ArrayBindingPattern2(r1, r3, r4, rp, _)) =>
      diff(l1, r1); diff[Elision](l3, r3, diff); diff[BindingRestElement](l4, r4, diff); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: BindingRestProperty, r: BindingRestProperty): Unit = (l, r) match {
    case (BindingRestProperty0(l1, lp, _), BindingRestProperty0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: BindingPropertyList, r: BindingPropertyList): Unit = (l, r) match {
    case (BindingPropertyList0(l0, lp, _), BindingPropertyList0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (BindingPropertyList1(l0, l2, lp, _), BindingPropertyList1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: BindingElementList, r: BindingElementList): Unit = (l, r) match {
    case (BindingElementList0(l0, lp, _), BindingElementList0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (BindingElementList1(l0, l2, lp, _), BindingElementList1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: BindingElisionElement, r: BindingElisionElement): Unit = (l, r) match {
    case (BindingElisionElement0(l0, l1, lp, _), BindingElisionElement0(r0, r1, rp, _)) =>
      diff[Elision](l0, r0, diff); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: BindingProperty, r: BindingProperty): Unit = (l, r) match {
    case (BindingProperty0(l0, lp, _), BindingProperty0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (BindingProperty1(l0, l2, lp, _), BindingProperty1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: BindingElement, r: BindingElement): Unit = (l, r) match {
    case (BindingElement0(l0, lp, _), BindingElement0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (BindingElement1(l0, l1, lp, _), BindingElement1(r0, r1, rp, _)) =>
      diff(l0, r0); diff[Initializer](l1, r1, diff); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: SingleNameBinding, r: SingleNameBinding): Unit = (l, r) match {
    case (SingleNameBinding0(l0, l1, lp, _), SingleNameBinding0(r0, r1, rp, _)) =>
      diff(l0, r0); diff[Initializer](l1, r1, diff); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: BindingRestElement, r: BindingRestElement): Unit = (l, r) match {
    case (BindingRestElement0(l1, lp, _), BindingRestElement0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (BindingRestElement1(l1, lp, _), BindingRestElement1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: EmptyStatement, r: EmptyStatement): Unit = (l, r) match {
    case (EmptyStatement0(lp, _), EmptyStatement0(rp, _)) =>
      diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ExpressionStatement, r: ExpressionStatement): Unit = (l, r) match {
    case (ExpressionStatement0(l1, lp, _), ExpressionStatement0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: IfStatement, r: IfStatement): Unit = (l, r) match {
    case (IfStatement0(l2, l4, l6, lp, _), IfStatement0(r2, r4, r6, rp, _)) =>
      diff(l2, r2); diff(l4, r4); diff(l6, r6); diff(lp, rp)
    case (IfStatement1(l2, l4, lp, _), IfStatement1(r2, r4, rp, _)) =>
      diff(l2, r2); diff(l4, r4); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: IterationStatement, r: IterationStatement): Unit = (l, r) match {
    case (IterationStatement0(l0, lp, _), IterationStatement0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (IterationStatement1(l0, lp, _), IterationStatement1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (IterationStatement2(l0, lp, _), IterationStatement2(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (IterationStatement3(l0, lp, _), IterationStatement3(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: DoWhileStatement, r: DoWhileStatement): Unit = (l, r) match {
    case (DoWhileStatement0(l1, l4, lp, _), DoWhileStatement0(r1, r4, rp, _)) =>
      diff(l1, r1); diff(l4, r4); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: WhileStatement, r: WhileStatement): Unit = (l, r) match {
    case (WhileStatement0(l2, l4, lp, _), WhileStatement0(r2, r4, rp, _)) =>
      diff(l2, r2); diff(l4, r4); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ForStatement, r: ForStatement): Unit = (l, r) match {
    case (ForStatement0(l3, l5, l7, l9, lp, _), ForStatement0(r3, r5, r7, r9, rp, _)) =>
      diff[Expression](l3, r3, diff); diff[Expression](l5, r5, diff); diff[Expression](l7, r7, diff); diff(l9, r9); diff(lp, rp)
    case (ForStatement1(l3, l5, l7, l9, lp, _), ForStatement1(r3, r5, r7, r9, rp, _)) =>
      diff(l3, r3); diff[Expression](l5, r5, diff); diff[Expression](l7, r7, diff); diff(l9, r9); diff(lp, rp)
    case (ForStatement2(l2, l3, l5, l7, lp, _), ForStatement2(r2, r3, r5, r7, rp, _)) =>
      diff(l2, r2); diff[Expression](l3, r3, diff); diff[Expression](l5, r5, diff); diff(l7, r7); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ForInOfStatement, r: ForInOfStatement): Unit = (l, r) match {
    case (ForInOfStatement0(l3, l5, l7, lp, _), ForInOfStatement0(r3, r5, r7, rp, _)) =>
      diff(l3, r3); diff(l5, r5); diff(l7, r7); diff(lp, rp)
    case (ForInOfStatement1(l3, l5, l7, lp, _), ForInOfStatement1(r3, r5, r7, rp, _)) =>
      diff(l3, r3); diff(l5, r5); diff(l7, r7); diff(lp, rp)
    case (ForInOfStatement2(l2, l4, l6, lp, _), ForInOfStatement2(r2, r4, r6, rp, _)) =>
      diff(l2, r2); diff(l4, r4); diff(l6, r6); diff(lp, rp)
    case (ForInOfStatement3(l3, l5, l7, lp, _), ForInOfStatement3(r3, r5, r7, rp, _)) =>
      diff(l3, r3); diff(l5, r5); diff(l7, r7); diff(lp, rp)
    case (ForInOfStatement4(l3, l5, l7, lp, _), ForInOfStatement4(r3, r5, r7, rp, _)) =>
      diff(l3, r3); diff(l5, r5); diff(l7, r7); diff(lp, rp)
    case (ForInOfStatement5(l2, l4, l6, lp, _), ForInOfStatement5(r2, r4, r6, rp, _)) =>
      diff(l2, r2); diff(l4, r4); diff(l6, r6); diff(lp, rp)
    case (ForInOfStatement6(l4, l6, l8, lp, _), ForInOfStatement6(r4, r6, r8, rp, _)) =>
      diff(l4, r4); diff(l6, r6); diff(l8, r8); diff(lp, rp)
    case (ForInOfStatement7(l4, l6, l8, lp, _), ForInOfStatement7(r4, r6, r8, rp, _)) =>
      diff(l4, r4); diff(l6, r6); diff(l8, r8); diff(lp, rp)
    case (ForInOfStatement8(l3, l5, l7, lp, _), ForInOfStatement8(r3, r5, r7, rp, _)) =>
      diff(l3, r3); diff(l5, r5); diff(l7, r7); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ForDeclaration, r: ForDeclaration): Unit = (l, r) match {
    case (ForDeclaration0(l0, l1, lp, _), ForDeclaration0(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ForBinding, r: ForBinding): Unit = (l, r) match {
    case (ForBinding0(l0, lp, _), ForBinding0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ForBinding1(l0, lp, _), ForBinding1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ContinueStatement, r: ContinueStatement): Unit = (l, r) match {
    case (ContinueStatement0(lp, _), ContinueStatement0(rp, _)) =>
      diff(lp, rp)
    case (ContinueStatement1(l2, lp, _), ContinueStatement1(r2, rp, _)) =>
      diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: BreakStatement, r: BreakStatement): Unit = (l, r) match {
    case (BreakStatement0(lp, _), BreakStatement0(rp, _)) =>
      diff(lp, rp)
    case (BreakStatement1(l2, lp, _), BreakStatement1(r2, rp, _)) =>
      diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ReturnStatement, r: ReturnStatement): Unit = (l, r) match {
    case (ReturnStatement0(lp, _), ReturnStatement0(rp, _)) =>
      diff(lp, rp)
    case (ReturnStatement1(l2, lp, _), ReturnStatement1(r2, rp, _)) =>
      diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: WithStatement, r: WithStatement): Unit = (l, r) match {
    case (WithStatement0(l2, l4, lp, _), WithStatement0(r2, r4, rp, _)) =>
      diff(l2, r2); diff(l4, r4); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: SwitchStatement, r: SwitchStatement): Unit = (l, r) match {
    case (SwitchStatement0(l2, l4, lp, _), SwitchStatement0(r2, r4, rp, _)) =>
      diff(l2, r2); diff(l4, r4); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: CaseBlock, r: CaseBlock): Unit = (l, r) match {
    case (CaseBlock0(l1, lp, _), CaseBlock0(r1, rp, _)) =>
      diff[CaseClauses](l1, r1, diff); diff(lp, rp)
    case (CaseBlock1(l1, l2, l3, lp, _), CaseBlock1(r1, r2, r3, rp, _)) =>
      diff[CaseClauses](l1, r1, diff); diff(l2, r2); diff[CaseClauses](l3, r3, diff); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: CaseClauses, r: CaseClauses): Unit = (l, r) match {
    case (CaseClauses0(l0, lp, _), CaseClauses0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (CaseClauses1(l0, l1, lp, _), CaseClauses1(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: CaseClause, r: CaseClause): Unit = (l, r) match {
    case (CaseClause0(l1, l3, lp, _), CaseClause0(r1, r3, rp, _)) =>
      diff(l1, r1); diff[StatementList](l3, r3, diff); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: DefaultClause, r: DefaultClause): Unit = (l, r) match {
    case (DefaultClause0(l2, lp, _), DefaultClause0(r2, rp, _)) =>
      diff[StatementList](l2, r2, diff); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: LabelledStatement, r: LabelledStatement): Unit = (l, r) match {
    case (LabelledStatement0(l0, l2, lp, _), LabelledStatement0(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: LabelledItem, r: LabelledItem): Unit = (l, r) match {
    case (LabelledItem0(l0, lp, _), LabelledItem0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (LabelledItem1(l0, lp, _), LabelledItem1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ThrowStatement, r: ThrowStatement): Unit = (l, r) match {
    case (ThrowStatement0(l2, lp, _), ThrowStatement0(r2, rp, _)) =>
      diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: TryStatement, r: TryStatement): Unit = (l, r) match {
    case (TryStatement0(l1, l2, lp, _), TryStatement0(r1, r2, rp, _)) =>
      diff(l1, r1); diff(l2, r2); diff(lp, rp)
    case (TryStatement1(l1, l2, lp, _), TryStatement1(r1, r2, rp, _)) =>
      diff(l1, r1); diff(l2, r2); diff(lp, rp)
    case (TryStatement2(l1, l2, l3, lp, _), TryStatement2(r1, r2, r3, rp, _)) =>
      diff(l1, r1); diff(l2, r2); diff(l3, r3); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: Catch, r: Catch): Unit = (l, r) match {
    case (Catch0(l2, l4, lp, _), Catch0(r2, r4, rp, _)) =>
      diff(l2, r2); diff(l4, r4); diff(lp, rp)
    case (Catch1(l1, lp, _), Catch1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: Finally, r: Finally): Unit = (l, r) match {
    case (Finally0(l1, lp, _), Finally0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: CatchParameter, r: CatchParameter): Unit = (l, r) match {
    case (CatchParameter0(l0, lp, _), CatchParameter0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (CatchParameter1(l0, lp, _), CatchParameter1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: DebuggerStatement, r: DebuggerStatement): Unit = (l, r) match {
    case (DebuggerStatement0(lp, _), DebuggerStatement0(rp, _)) =>
      diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: UniqueFormalParameters, r: UniqueFormalParameters): Unit = (l, r) match {
    case (UniqueFormalParameters0(l0, lp, _), UniqueFormalParameters0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: FormalParameters, r: FormalParameters): Unit = (l, r) match {
    case (FormalParameters0(lp, _), FormalParameters0(rp, _)) =>
      diff(lp, rp)
    case (FormalParameters1(l0, lp, _), FormalParameters1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (FormalParameters2(l0, lp, _), FormalParameters2(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (FormalParameters3(l0, lp, _), FormalParameters3(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (FormalParameters4(l0, l2, lp, _), FormalParameters4(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: FormalParameterList, r: FormalParameterList): Unit = (l, r) match {
    case (FormalParameterList0(l0, lp, _), FormalParameterList0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (FormalParameterList1(l0, l2, lp, _), FormalParameterList1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: FunctionRestParameter, r: FunctionRestParameter): Unit = (l, r) match {
    case (FunctionRestParameter0(l0, lp, _), FunctionRestParameter0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: FormalParameter, r: FormalParameter): Unit = (l, r) match {
    case (FormalParameter0(l0, lp, _), FormalParameter0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: FunctionDeclaration, r: FunctionDeclaration): Unit = (l, r) match {
    case (FunctionDeclaration0(l1, l3, l6, lp, _), FunctionDeclaration0(r1, r3, r6, rp, _)) =>
      diff(l1, r1); diff(l3, r3); diff(l6, r6); diff(lp, rp)
    case (FunctionDeclaration1(l2, l5, lp, _), FunctionDeclaration1(r2, r5, rp, _)) =>
      diff(l2, r2); diff(l5, r5); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: FunctionExpression, r: FunctionExpression): Unit = (l, r) match {
    case (FunctionExpression0(l1, l3, l6, lp, _), FunctionExpression0(r1, r3, r6, rp, _)) =>
      diff[BindingIdentifier](l1, r1, diff); diff(l3, r3); diff(l6, r6); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: FunctionBody, r: FunctionBody): Unit = (l, r) match {
    case (FunctionBody0(l0, lp, _), FunctionBody0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: FunctionStatementList, r: FunctionStatementList): Unit = (l, r) match {
    case (FunctionStatementList0(l0, lp, _), FunctionStatementList0(r0, rp, _)) =>
      diff[StatementList](l0, r0, diff); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ArrowFunction, r: ArrowFunction): Unit = (l, r) match {
    case (ArrowFunction0(l0, l3, lp, _), ArrowFunction0(r0, r3, rp, _)) =>
      diff(l0, r0); diff(l3, r3); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ArrowParameters, r: ArrowParameters): Unit = (l, r) match {
    case (ArrowParameters0(l0, lp, _), ArrowParameters0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ArrowParameters1(l0, lp, _), ArrowParameters1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ConciseBody, r: ConciseBody): Unit = (l, r) match {
    case (ConciseBody0(l1, lp, _), ConciseBody0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (ConciseBody1(l1, lp, _), ConciseBody1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ExpressionBody, r: ExpressionBody): Unit = (l, r) match {
    case (ExpressionBody0(l0, lp, _), ExpressionBody0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ArrowFormalParameters, r: ArrowFormalParameters): Unit = (l, r) match {
    case (ArrowFormalParameters0(l1, lp, _), ArrowFormalParameters0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: MethodDefinition, r: MethodDefinition): Unit = (l, r) match {
    case (MethodDefinition0(l0, l2, l5, lp, _), MethodDefinition0(r0, r2, r5, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(l5, r5); diff(lp, rp)
    case (MethodDefinition1(l0, lp, _), MethodDefinition1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (MethodDefinition2(l0, lp, _), MethodDefinition2(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (MethodDefinition3(l0, lp, _), MethodDefinition3(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (MethodDefinition4(l1, l5, lp, _), MethodDefinition4(r1, r5, rp, _)) =>
      diff(l1, r1); diff(l5, r5); diff(lp, rp)
    case (MethodDefinition5(l1, l3, l6, lp, _), MethodDefinition5(r1, r3, r6, rp, _)) =>
      diff(l1, r1); diff(l3, r3); diff(l6, r6); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: PropertySetParameterList, r: PropertySetParameterList): Unit = (l, r) match {
    case (PropertySetParameterList0(l0, lp, _), PropertySetParameterList0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: GeneratorMethod, r: GeneratorMethod): Unit = (l, r) match {
    case (GeneratorMethod0(l1, l3, l6, lp, _), GeneratorMethod0(r1, r3, r6, rp, _)) =>
      diff(l1, r1); diff(l3, r3); diff(l6, r6); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: GeneratorDeclaration, r: GeneratorDeclaration): Unit = (l, r) match {
    case (GeneratorDeclaration0(l2, l4, l7, lp, _), GeneratorDeclaration0(r2, r4, r7, rp, _)) =>
      diff(l2, r2); diff(l4, r4); diff(l7, r7); diff(lp, rp)
    case (GeneratorDeclaration1(l3, l6, lp, _), GeneratorDeclaration1(r3, r6, rp, _)) =>
      diff(l3, r3); diff(l6, r6); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: GeneratorExpression, r: GeneratorExpression): Unit = (l, r) match {
    case (GeneratorExpression0(l2, l4, l7, lp, _), GeneratorExpression0(r2, r4, r7, rp, _)) =>
      diff[BindingIdentifier](l2, r2, diff); diff(l4, r4); diff(l7, r7); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: GeneratorBody, r: GeneratorBody): Unit = (l, r) match {
    case (GeneratorBody0(l0, lp, _), GeneratorBody0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: YieldExpression, r: YieldExpression): Unit = (l, r) match {
    case (YieldExpression0(lp, _), YieldExpression0(rp, _)) =>
      diff(lp, rp)
    case (YieldExpression1(l2, lp, _), YieldExpression1(r2, rp, _)) =>
      diff(l2, r2); diff(lp, rp)
    case (YieldExpression2(l3, lp, _), YieldExpression2(r3, rp, _)) =>
      diff(l3, r3); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncGeneratorMethod, r: AsyncGeneratorMethod): Unit = (l, r) match {
    case (AsyncGeneratorMethod0(l3, l5, l8, lp, _), AsyncGeneratorMethod0(r3, r5, r8, rp, _)) =>
      diff(l3, r3); diff(l5, r5); diff(l8, r8); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncGeneratorDeclaration, r: AsyncGeneratorDeclaration): Unit = (l, r) match {
    case (AsyncGeneratorDeclaration0(l4, l6, l9, lp, _), AsyncGeneratorDeclaration0(r4, r6, r9, rp, _)) =>
      diff(l4, r4); diff(l6, r6); diff(l9, r9); diff(lp, rp)
    case (AsyncGeneratorDeclaration1(l5, l8, lp, _), AsyncGeneratorDeclaration1(r5, r8, rp, _)) =>
      diff(l5, r5); diff(l8, r8); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncGeneratorExpression, r: AsyncGeneratorExpression): Unit = (l, r) match {
    case (AsyncGeneratorExpression0(l4, l6, l9, lp, _), AsyncGeneratorExpression0(r4, r6, r9, rp, _)) =>
      diff[BindingIdentifier](l4, r4, diff); diff(l6, r6); diff(l9, r9); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncGeneratorBody, r: AsyncGeneratorBody): Unit = (l, r) match {
    case (AsyncGeneratorBody0(l0, lp, _), AsyncGeneratorBody0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ClassDeclaration, r: ClassDeclaration): Unit = (l, r) match {
    case (ClassDeclaration0(l1, l2, lp, _), ClassDeclaration0(r1, r2, rp, _)) =>
      diff(l1, r1); diff(l2, r2); diff(lp, rp)
    case (ClassDeclaration1(l1, lp, _), ClassDeclaration1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ClassExpression, r: ClassExpression): Unit = (l, r) match {
    case (ClassExpression0(l1, l2, lp, _), ClassExpression0(r1, r2, rp, _)) =>
      diff[BindingIdentifier](l1, r1, diff); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ClassTail, r: ClassTail): Unit = (l, r) match {
    case (ClassTail0(l0, l2, lp, _), ClassTail0(r0, r2, rp, _)) =>
      diff[ClassHeritage](l0, r0, diff); diff[ClassBody](l2, r2, diff); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ClassHeritage, r: ClassHeritage): Unit = (l, r) match {
    case (ClassHeritage0(l1, lp, _), ClassHeritage0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ClassBody, r: ClassBody): Unit = (l, r) match {
    case (ClassBody0(l0, lp, _), ClassBody0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ClassElementList, r: ClassElementList): Unit = (l, r) match {
    case (ClassElementList0(l0, lp, _), ClassElementList0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ClassElementList1(l0, l1, lp, _), ClassElementList1(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ClassElement, r: ClassElement): Unit = (l, r) match {
    case (ClassElement0(l0, lp, _), ClassElement0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ClassElement1(l1, lp, _), ClassElement1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (ClassElement2(lp, _), ClassElement2(rp, _)) =>
      diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncFunctionDeclaration, r: AsyncFunctionDeclaration): Unit = (l, r) match {
    case (AsyncFunctionDeclaration0(l3, l5, l8, lp, _), AsyncFunctionDeclaration0(r3, r5, r8, rp, _)) =>
      diff(l3, r3); diff(l5, r5); diff(l8, r8); diff(lp, rp)
    case (AsyncFunctionDeclaration1(l4, l7, lp, _), AsyncFunctionDeclaration1(r4, r7, rp, _)) =>
      diff(l4, r4); diff(l7, r7); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncFunctionExpression, r: AsyncFunctionExpression): Unit = (l, r) match {
    case (AsyncFunctionExpression0(l3, l5, l8, lp, _), AsyncFunctionExpression0(r3, r5, r8, rp, _)) =>
      diff[BindingIdentifier](l3, r3, diff); diff(l5, r5); diff(l8, r8); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncMethod, r: AsyncMethod): Unit = (l, r) match {
    case (AsyncMethod0(l2, l4, l7, lp, _), AsyncMethod0(r2, r4, r7, rp, _)) =>
      diff(l2, r2); diff(l4, r4); diff(l7, r7); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncFunctionBody, r: AsyncFunctionBody): Unit = (l, r) match {
    case (AsyncFunctionBody0(l0, lp, _), AsyncFunctionBody0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AwaitExpression, r: AwaitExpression): Unit = (l, r) match {
    case (AwaitExpression0(l1, lp, _), AwaitExpression0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncArrowFunction, r: AsyncArrowFunction): Unit = (l, r) match {
    case (AsyncArrowFunction0(l2, l5, lp, _), AsyncArrowFunction0(r2, r5, rp, _)) =>
      diff(l2, r2); diff(l5, r5); diff(lp, rp)
    case (AsyncArrowFunction1(l0, l3, lp, _), AsyncArrowFunction1(r0, r3, rp, _)) =>
      diff(l0, r0); diff(l3, r3); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncConciseBody, r: AsyncConciseBody): Unit = (l, r) match {
    case (AsyncConciseBody0(l1, lp, _), AsyncConciseBody0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (AsyncConciseBody1(l1, lp, _), AsyncConciseBody1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncArrowBindingIdentifier, r: AsyncArrowBindingIdentifier): Unit = (l, r) match {
    case (AsyncArrowBindingIdentifier0(l0, lp, _), AsyncArrowBindingIdentifier0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: CoverCallExpressionAndAsyncArrowHead, r: CoverCallExpressionAndAsyncArrowHead): Unit = (l, r) match {
    case (CoverCallExpressionAndAsyncArrowHead0(l0, l1, lp, _), CoverCallExpressionAndAsyncArrowHead0(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: AsyncArrowHead, r: AsyncArrowHead): Unit = (l, r) match {
    case (AsyncArrowHead0(l2, lp, _), AsyncArrowHead0(r2, rp, _)) =>
      diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: Script, r: Script): Unit = (l, r) match {
    case (Script0(l0, lp, _), Script0(r0, rp, _)) =>
      diff[ScriptBody](l0, r0, diff); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ScriptBody, r: ScriptBody): Unit = (l, r) match {
    case (ScriptBody0(l0, lp, _), ScriptBody0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: Module, r: Module): Unit = (l, r) match {
    case (Module0(l0, lp, _), Module0(r0, rp, _)) =>
      diff[ModuleBody](l0, r0, diff); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ModuleBody, r: ModuleBody): Unit = (l, r) match {
    case (ModuleBody0(l0, lp, _), ModuleBody0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ModuleItemList, r: ModuleItemList): Unit = (l, r) match {
    case (ModuleItemList0(l0, lp, _), ModuleItemList0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ModuleItemList1(l0, l1, lp, _), ModuleItemList1(r0, r1, rp, _)) =>
      diff(l0, r0); diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ModuleItem, r: ModuleItem): Unit = (l, r) match {
    case (ModuleItem0(l0, lp, _), ModuleItem0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ModuleItem1(l0, lp, _), ModuleItem1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ModuleItem2(l0, lp, _), ModuleItem2(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ImportDeclaration, r: ImportDeclaration): Unit = (l, r) match {
    case (ImportDeclaration0(l1, l2, lp, _), ImportDeclaration0(r1, r2, rp, _)) =>
      diff(l1, r1); diff(l2, r2); diff(lp, rp)
    case (ImportDeclaration1(l1, lp, _), ImportDeclaration1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ImportClause, r: ImportClause): Unit = (l, r) match {
    case (ImportClause0(l0, lp, _), ImportClause0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ImportClause1(l0, lp, _), ImportClause1(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ImportClause2(l0, lp, _), ImportClause2(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ImportClause3(l0, l2, lp, _), ImportClause3(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case (ImportClause4(l0, l2, lp, _), ImportClause4(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ImportedDefaultBinding, r: ImportedDefaultBinding): Unit = (l, r) match {
    case (ImportedDefaultBinding0(l0, lp, _), ImportedDefaultBinding0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: NameSpaceImport, r: NameSpaceImport): Unit = (l, r) match {
    case (NameSpaceImport0(l2, lp, _), NameSpaceImport0(r2, rp, _)) =>
      diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: NamedImports, r: NamedImports): Unit = (l, r) match {
    case (NamedImports0(lp, _), NamedImports0(rp, _)) =>
      diff(lp, rp)
    case (NamedImports1(l1, lp, _), NamedImports1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (NamedImports2(l1, lp, _), NamedImports2(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: FromClause, r: FromClause): Unit = (l, r) match {
    case (FromClause0(l1, lp, _), FromClause0(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ImportsList, r: ImportsList): Unit = (l, r) match {
    case (ImportsList0(l0, lp, _), ImportsList0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ImportsList1(l0, l2, lp, _), ImportsList1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ImportSpecifier, r: ImportSpecifier): Unit = (l, r) match {
    case (ImportSpecifier0(l0, lp, _), ImportSpecifier0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ImportSpecifier1(l0, l2, lp, _), ImportSpecifier1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ModuleSpecifier, r: ModuleSpecifier): Unit = (l, r) match {
    case (ModuleSpecifier0(l0, lp, _), ModuleSpecifier0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ImportedBinding, r: ImportedBinding): Unit = (l, r) match {
    case (ImportedBinding0(l0, lp, _), ImportedBinding0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ExportDeclaration, r: ExportDeclaration): Unit = (l, r) match {
    case (ExportDeclaration0(l1, l2, lp, _), ExportDeclaration0(r1, r2, rp, _)) =>
      diff(l1, r1); diff(l2, r2); diff(lp, rp)
    case (ExportDeclaration1(l1, lp, _), ExportDeclaration1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (ExportDeclaration2(l1, lp, _), ExportDeclaration2(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (ExportDeclaration3(l1, lp, _), ExportDeclaration3(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (ExportDeclaration4(l2, lp, _), ExportDeclaration4(r2, rp, _)) =>
      diff(l2, r2); diff(lp, rp)
    case (ExportDeclaration5(l2, lp, _), ExportDeclaration5(r2, rp, _)) =>
      diff(l2, r2); diff(lp, rp)
    case (ExportDeclaration6(l3, lp, _), ExportDeclaration6(r3, rp, _)) =>
      diff(l3, r3); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ExportFromClause, r: ExportFromClause): Unit = (l, r) match {
    case (ExportFromClause0(lp, _), ExportFromClause0(rp, _)) =>
      diff(lp, rp)
    case (ExportFromClause1(l2, lp, _), ExportFromClause1(r2, rp, _)) =>
      diff(l2, r2); diff(lp, rp)
    case (ExportFromClause2(l0, lp, _), ExportFromClause2(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: NamedExports, r: NamedExports): Unit = (l, r) match {
    case (NamedExports0(lp, _), NamedExports0(rp, _)) =>
      diff(lp, rp)
    case (NamedExports1(l1, lp, _), NamedExports1(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case (NamedExports2(l1, lp, _), NamedExports2(r1, rp, _)) =>
      diff(l1, r1); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ExportsList, r: ExportsList): Unit = (l, r) match {
    case (ExportsList0(l0, lp, _), ExportsList0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ExportsList1(l0, l2, lp, _), ExportsList1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
  def diff(l: ExportSpecifier, r: ExportSpecifier): Unit = (l, r) match {
    case (ExportSpecifier0(l0, lp, _), ExportSpecifier0(r0, rp, _)) =>
      diff(l0, r0); diff(lp, rp)
    case (ExportSpecifier1(l0, l2, lp, _), ExportSpecifier1(r0, r2, rp, _)) =>
      diff(l0, r0); diff(l2, r2); diff(lp, rp)
    case _ => diffError(l, r)
  }
}

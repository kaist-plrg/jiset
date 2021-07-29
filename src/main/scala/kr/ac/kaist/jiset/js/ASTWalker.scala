package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset.js.ast._

trait ASTWalker {
  def job(ast: AST): Unit = {}
  def walk[T](opt: Option[T], w: T => Unit): Unit = opt.map(w)
  def walk(lex: Lexical): Unit = {}

  def walk(ast: IdentifierReference): Unit = ast match {
    case IdentifierReference0(x0, _, _) =>
      job(ast); walk(x0)
    case IdentifierReference1(_, _) => job(ast)
    case IdentifierReference2(_, _) => job(ast)
  }
  def walk(ast: BindingIdentifier): Unit = ast match {
    case BindingIdentifier0(x0, _, _) =>
      job(ast); walk(x0)
    case BindingIdentifier1(_, _) => job(ast)
    case BindingIdentifier2(_, _) => job(ast)
  }
  def walk(ast: LabelIdentifier): Unit = ast match {
    case LabelIdentifier0(x0, _, _) =>
      job(ast); walk(x0)
    case LabelIdentifier1(_, _) => job(ast)
    case LabelIdentifier2(_, _) => job(ast)
  }
  def walk(ast: Identifier): Unit = ast match {
    case Identifier0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: PrimaryExpression): Unit = ast match {
    case PrimaryExpression0(_, _) => job(ast)
    case PrimaryExpression1(x0, _, _) =>
      job(ast); walk(x0)
    case PrimaryExpression2(x0, _, _) =>
      job(ast); walk(x0)
    case PrimaryExpression3(x0, _, _) =>
      job(ast); walk(x0)
    case PrimaryExpression4(x0, _, _) =>
      job(ast); walk(x0)
    case PrimaryExpression5(x0, _, _) =>
      job(ast); walk(x0)
    case PrimaryExpression6(x0, _, _) =>
      job(ast); walk(x0)
    case PrimaryExpression7(x0, _, _) =>
      job(ast); walk(x0)
    case PrimaryExpression8(x0, _, _) =>
      job(ast); walk(x0)
    case PrimaryExpression9(x0, _, _) =>
      job(ast); walk(x0)
    case PrimaryExpression10(x0, _, _) =>
      job(ast); walk(x0)
    case PrimaryExpression11(x0, _, _) =>
      job(ast); walk(x0)
    case PrimaryExpression12(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: CoverParenthesizedExpressionAndArrowParameterList): Unit = ast match {
    case CoverParenthesizedExpressionAndArrowParameterList0(x1, _, _) =>
      job(ast); walk(x1)
    case CoverParenthesizedExpressionAndArrowParameterList1(x1, _, _) =>
      job(ast); walk(x1)
    case CoverParenthesizedExpressionAndArrowParameterList2(_, _) => job(ast)
    case CoverParenthesizedExpressionAndArrowParameterList3(x2, _, _) =>
      job(ast); walk(x2)
    case CoverParenthesizedExpressionAndArrowParameterList4(x2, _, _) =>
      job(ast); walk(x2)
    case CoverParenthesizedExpressionAndArrowParameterList5(x1, x4, _, _) =>
      job(ast); walk(x1); walk(x4)
    case CoverParenthesizedExpressionAndArrowParameterList6(x1, x4, _, _) =>
      job(ast); walk(x1); walk(x4)
  }
  def walk(ast: ParenthesizedExpression): Unit = ast match {
    case ParenthesizedExpression0(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: Literal): Unit = ast match {
    case Literal0(x0, _, _) =>
      job(ast); walk(x0)
    case Literal1(x0, _, _) =>
      job(ast); walk(x0)
    case Literal2(x0, _, _) =>
      job(ast); walk(x0)
    case Literal3(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ArrayLiteral): Unit = ast match {
    case ArrayLiteral0(x1, _, _) =>
      job(ast); walk[Elision](x1, walk)
    case ArrayLiteral1(x1, _, _) =>
      job(ast); walk(x1)
    case ArrayLiteral2(x1, x3, _, _) =>
      job(ast); walk(x1); walk[Elision](x3, walk)
  }
  def walk(ast: ElementList): Unit = ast match {
    case ElementList0(x0, x1, _, _) =>
      job(ast); walk[Elision](x0, walk); walk(x1)
    case ElementList1(x0, x1, _, _) =>
      job(ast); walk[Elision](x0, walk); walk(x1)
    case ElementList2(x0, x2, x3, _, _) =>
      job(ast); walk(x0); walk[Elision](x2, walk); walk(x3)
    case ElementList3(x0, x2, x3, _, _) =>
      job(ast); walk(x0); walk[Elision](x2, walk); walk(x3)
  }
  def walk(ast: Elision): Unit = ast match {
    case Elision0(_, _) => job(ast)
    case Elision1(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: SpreadElement): Unit = ast match {
    case SpreadElement0(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: ObjectLiteral): Unit = ast match {
    case ObjectLiteral0(_, _) => job(ast)
    case ObjectLiteral1(x1, _, _) =>
      job(ast); walk(x1)
    case ObjectLiteral2(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: PropertyDefinitionList): Unit = ast match {
    case PropertyDefinitionList0(x0, _, _) =>
      job(ast); walk(x0)
    case PropertyDefinitionList1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: PropertyDefinition): Unit = ast match {
    case PropertyDefinition0(x0, _, _) =>
      job(ast); walk(x0)
    case PropertyDefinition1(x0, _, _) =>
      job(ast); walk(x0)
    case PropertyDefinition2(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case PropertyDefinition3(x0, _, _) =>
      job(ast); walk(x0)
    case PropertyDefinition4(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: PropertyName): Unit = ast match {
    case PropertyName0(x0, _, _) =>
      job(ast); walk(x0)
    case PropertyName1(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: LiteralPropertyName): Unit = ast match {
    case LiteralPropertyName0(x0, _, _) =>
      job(ast); walk(x0)
    case LiteralPropertyName1(x0, _, _) =>
      job(ast); walk(x0)
    case LiteralPropertyName2(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ComputedPropertyName): Unit = ast match {
    case ComputedPropertyName0(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: CoverInitializedName): Unit = ast match {
    case CoverInitializedName0(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
  }
  def walk(ast: Initializer): Unit = ast match {
    case Initializer0(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: TemplateLiteral): Unit = ast match {
    case TemplateLiteral0(x0, _, _) =>
      job(ast); walk(x0)
    case TemplateLiteral1(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: SubstitutionTemplate): Unit = ast match {
    case SubstitutionTemplate0(x0, x1, x2, _, _) =>
      job(ast); walk(x0); walk(x1); walk(x2)
  }
  def walk(ast: TemplateSpans): Unit = ast match {
    case TemplateSpans0(x0, _, _) =>
      job(ast); walk(x0)
    case TemplateSpans1(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
  }
  def walk(ast: TemplateMiddleList): Unit = ast match {
    case TemplateMiddleList0(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
    case TemplateMiddleList1(x0, x1, x2, _, _) =>
      job(ast); walk(x0); walk(x1); walk(x2)
  }
  def walk(ast: MemberExpression): Unit = ast match {
    case MemberExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case MemberExpression1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case MemberExpression2(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case MemberExpression3(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
    case MemberExpression4(x0, _, _) =>
      job(ast); walk(x0)
    case MemberExpression5(x0, _, _) =>
      job(ast); walk(x0)
    case MemberExpression6(x1, x2, _, _) =>
      job(ast); walk(x1); walk(x2)
  }
  def walk(ast: SuperProperty): Unit = ast match {
    case SuperProperty0(x2, _, _) =>
      job(ast); walk(x2)
    case SuperProperty1(x2, _, _) =>
      job(ast); walk(x2)
  }
  def walk(ast: MetaProperty): Unit = ast match {
    case MetaProperty0(x0, _, _) =>
      job(ast); walk(x0)
    case MetaProperty1(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: NewTarget): Unit = ast match {
    case NewTarget0(_, _) => job(ast)
  }
  def walk(ast: ImportMeta): Unit = ast match {
    case ImportMeta0(_, _) => job(ast)
  }
  def walk(ast: NewExpression): Unit = ast match {
    case NewExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case NewExpression1(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: CallExpression): Unit = ast match {
    case CallExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case CallExpression1(x0, _, _) =>
      job(ast); walk(x0)
    case CallExpression2(x0, _, _) =>
      job(ast); walk(x0)
    case CallExpression3(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
    case CallExpression4(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case CallExpression5(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case CallExpression6(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
  }
  def walk(ast: SuperCall): Unit = ast match {
    case SuperCall0(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: ImportCall): Unit = ast match {
    case ImportCall0(x2, _, _) =>
      job(ast); walk(x2)
  }
  def walk(ast: Arguments): Unit = ast match {
    case Arguments0(_, _) => job(ast)
    case Arguments1(x1, _, _) =>
      job(ast); walk(x1)
    case Arguments2(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: ArgumentList): Unit = ast match {
    case ArgumentList0(x0, _, _) =>
      job(ast); walk(x0)
    case ArgumentList1(x1, _, _) =>
      job(ast); walk(x1)
    case ArgumentList2(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case ArgumentList3(x0, x3, _, _) =>
      job(ast); walk(x0); walk(x3)
  }
  def walk(ast: OptionalExpression): Unit = ast match {
    case OptionalExpression0(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
    case OptionalExpression1(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
    case OptionalExpression2(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
  }
  def walk(ast: OptionalChain): Unit = ast match {
    case OptionalChain0(x1, _, _) =>
      job(ast); walk(x1)
    case OptionalChain1(x2, _, _) =>
      job(ast); walk(x2)
    case OptionalChain2(x1, _, _) =>
      job(ast); walk(x1)
    case OptionalChain3(x1, _, _) =>
      job(ast); walk(x1)
    case OptionalChain4(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
    case OptionalChain5(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case OptionalChain6(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case OptionalChain7(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
  }
  def walk(ast: LeftHandSideExpression): Unit = ast match {
    case LeftHandSideExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case LeftHandSideExpression1(x0, _, _) =>
      job(ast); walk(x0)
    case LeftHandSideExpression2(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: CallMemberExpression): Unit = ast match {
    case CallMemberExpression0(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
  }
  def walk(ast: UpdateExpression): Unit = ast match {
    case UpdateExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case UpdateExpression1(x0, _, _) =>
      job(ast); walk(x0)
    case UpdateExpression2(x0, _, _) =>
      job(ast); walk(x0)
    case UpdateExpression3(x1, _, _) =>
      job(ast); walk(x1)
    case UpdateExpression4(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: UnaryExpression): Unit = ast match {
    case UnaryExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case UnaryExpression1(x1, _, _) =>
      job(ast); walk(x1)
    case UnaryExpression2(x1, _, _) =>
      job(ast); walk(x1)
    case UnaryExpression3(x1, _, _) =>
      job(ast); walk(x1)
    case UnaryExpression4(x1, _, _) =>
      job(ast); walk(x1)
    case UnaryExpression5(x1, _, _) =>
      job(ast); walk(x1)
    case UnaryExpression6(x1, _, _) =>
      job(ast); walk(x1)
    case UnaryExpression7(x1, _, _) =>
      job(ast); walk(x1)
    case UnaryExpression8(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ExponentiationExpression): Unit = ast match {
    case ExponentiationExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case ExponentiationExpression1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: MultiplicativeExpression): Unit = ast match {
    case MultiplicativeExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case MultiplicativeExpression1(x0, x1, x2, _, _) =>
      job(ast); walk(x0); walk(x1); walk(x2)
  }
  def walk(ast: MultiplicativeOperator): Unit = ast match {
    case MultiplicativeOperator0(_, _) => job(ast)
    case MultiplicativeOperator1(_, _) => job(ast)
    case MultiplicativeOperator2(_, _) => job(ast)
  }
  def walk(ast: AdditiveExpression): Unit = ast match {
    case AdditiveExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case AdditiveExpression1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case AdditiveExpression2(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: ShiftExpression): Unit = ast match {
    case ShiftExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case ShiftExpression1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case ShiftExpression2(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case ShiftExpression3(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: RelationalExpression): Unit = ast match {
    case RelationalExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case RelationalExpression1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case RelationalExpression2(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case RelationalExpression3(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case RelationalExpression4(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case RelationalExpression5(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case RelationalExpression6(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: EqualityExpression): Unit = ast match {
    case EqualityExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case EqualityExpression1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case EqualityExpression2(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case EqualityExpression3(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case EqualityExpression4(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: BitwiseANDExpression): Unit = ast match {
    case BitwiseANDExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case BitwiseANDExpression1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: BitwiseXORExpression): Unit = ast match {
    case BitwiseXORExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case BitwiseXORExpression1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: BitwiseORExpression): Unit = ast match {
    case BitwiseORExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case BitwiseORExpression1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: LogicalANDExpression): Unit = ast match {
    case LogicalANDExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case LogicalANDExpression1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: LogicalORExpression): Unit = ast match {
    case LogicalORExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case LogicalORExpression1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: CoalesceExpression): Unit = ast match {
    case CoalesceExpression0(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: CoalesceExpressionHead): Unit = ast match {
    case CoalesceExpressionHead0(x0, _, _) =>
      job(ast); walk(x0)
    case CoalesceExpressionHead1(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ShortCircuitExpression): Unit = ast match {
    case ShortCircuitExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case ShortCircuitExpression1(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ConditionalExpression): Unit = ast match {
    case ConditionalExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case ConditionalExpression1(x0, x2, x4, _, _) =>
      job(ast); walk(x0); walk(x2); walk(x4)
  }
  def walk(ast: AssignmentExpression): Unit = ast match {
    case AssignmentExpression0(x0, _, _) =>
      job(ast); walk(x0)
    case AssignmentExpression1(x0, _, _) =>
      job(ast); walk(x0)
    case AssignmentExpression2(x0, _, _) =>
      job(ast); walk(x0)
    case AssignmentExpression3(x0, _, _) =>
      job(ast); walk(x0)
    case AssignmentExpression4(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case AssignmentExpression5(x0, x1, x2, _, _) =>
      job(ast); walk(x0); walk(x1); walk(x2)
    case AssignmentExpression6(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case AssignmentExpression7(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case AssignmentExpression8(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: AssignmentOperator): Unit = ast match {
    case AssignmentOperator0(_, _) => job(ast)
    case AssignmentOperator1(_, _) => job(ast)
    case AssignmentOperator2(_, _) => job(ast)
    case AssignmentOperator3(_, _) => job(ast)
    case AssignmentOperator4(_, _) => job(ast)
    case AssignmentOperator5(_, _) => job(ast)
    case AssignmentOperator6(_, _) => job(ast)
    case AssignmentOperator7(_, _) => job(ast)
    case AssignmentOperator8(_, _) => job(ast)
    case AssignmentOperator9(_, _) => job(ast)
    case AssignmentOperator10(_, _) => job(ast)
    case AssignmentOperator11(_, _) => job(ast)
  }
  def walk(ast: AssignmentPattern): Unit = ast match {
    case AssignmentPattern0(x0, _, _) =>
      job(ast); walk(x0)
    case AssignmentPattern1(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ObjectAssignmentPattern): Unit = ast match {
    case ObjectAssignmentPattern0(_, _) => job(ast)
    case ObjectAssignmentPattern1(x1, _, _) =>
      job(ast); walk(x1)
    case ObjectAssignmentPattern2(x1, _, _) =>
      job(ast); walk(x1)
    case ObjectAssignmentPattern3(x1, x3, _, _) =>
      job(ast); walk(x1); walk[AssignmentRestProperty](x3, walk)
  }
  def walk(ast: ArrayAssignmentPattern): Unit = ast match {
    case ArrayAssignmentPattern0(x1, x2, _, _) =>
      job(ast); walk[Elision](x1, walk); walk[AssignmentRestElement](x2, walk)
    case ArrayAssignmentPattern1(x1, _, _) =>
      job(ast); walk(x1)
    case ArrayAssignmentPattern2(x1, x3, x4, _, _) =>
      job(ast); walk(x1); walk[Elision](x3, walk); walk[AssignmentRestElement](x4, walk)
  }
  def walk(ast: AssignmentRestProperty): Unit = ast match {
    case AssignmentRestProperty0(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: AssignmentPropertyList): Unit = ast match {
    case AssignmentPropertyList0(x0, _, _) =>
      job(ast); walk(x0)
    case AssignmentPropertyList1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: AssignmentElementList): Unit = ast match {
    case AssignmentElementList0(x0, _, _) =>
      job(ast); walk(x0)
    case AssignmentElementList1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: AssignmentElisionElement): Unit = ast match {
    case AssignmentElisionElement0(x0, x1, _, _) =>
      job(ast); walk[Elision](x0, walk); walk(x1)
  }
  def walk(ast: AssignmentProperty): Unit = ast match {
    case AssignmentProperty0(x0, x1, _, _) =>
      job(ast); walk(x0); walk[Initializer](x1, walk)
    case AssignmentProperty1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: AssignmentElement): Unit = ast match {
    case AssignmentElement0(x0, x1, _, _) =>
      job(ast); walk(x0); walk[Initializer](x1, walk)
  }
  def walk(ast: AssignmentRestElement): Unit = ast match {
    case AssignmentRestElement0(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: DestructuringAssignmentTarget): Unit = ast match {
    case DestructuringAssignmentTarget0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: Expression): Unit = ast match {
    case Expression0(x0, _, _) =>
      job(ast); walk(x0)
    case Expression1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: Statement): Unit = ast match {
    case Statement0(x0, _, _) =>
      job(ast); walk(x0)
    case Statement1(x0, _, _) =>
      job(ast); walk(x0)
    case Statement2(x0, _, _) =>
      job(ast); walk(x0)
    case Statement3(x0, _, _) =>
      job(ast); walk(x0)
    case Statement4(x0, _, _) =>
      job(ast); walk(x0)
    case Statement5(x0, _, _) =>
      job(ast); walk(x0)
    case Statement6(x0, _, _) =>
      job(ast); walk(x0)
    case Statement7(x0, _, _) =>
      job(ast); walk(x0)
    case Statement8(x0, _, _) =>
      job(ast); walk(x0)
    case Statement9(x0, _, _) =>
      job(ast); walk(x0)
    case Statement10(x0, _, _) =>
      job(ast); walk(x0)
    case Statement11(x0, _, _) =>
      job(ast); walk(x0)
    case Statement12(x0, _, _) =>
      job(ast); walk(x0)
    case Statement13(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: Declaration): Unit = ast match {
    case Declaration0(x0, _, _) =>
      job(ast); walk(x0)
    case Declaration1(x0, _, _) =>
      job(ast); walk(x0)
    case Declaration2(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: HoistableDeclaration): Unit = ast match {
    case HoistableDeclaration0(x0, _, _) =>
      job(ast); walk(x0)
    case HoistableDeclaration1(x0, _, _) =>
      job(ast); walk(x0)
    case HoistableDeclaration2(x0, _, _) =>
      job(ast); walk(x0)
    case HoistableDeclaration3(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: BreakableStatement): Unit = ast match {
    case BreakableStatement0(x0, _, _) =>
      job(ast); walk(x0)
    case BreakableStatement1(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: BlockStatement): Unit = ast match {
    case BlockStatement0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: Block): Unit = ast match {
    case Block0(x1, _, _) =>
      job(ast); walk[StatementList](x1, walk)
  }
  def walk(ast: StatementList): Unit = ast match {
    case StatementList0(x0, _, _) =>
      job(ast); walk(x0)
    case StatementList1(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
  }
  def walk(ast: StatementListItem): Unit = ast match {
    case StatementListItem0(x0, _, _) =>
      job(ast); walk(x0)
    case StatementListItem1(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: LexicalDeclaration): Unit = ast match {
    case LexicalDeclaration0(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
  }
  def walk(ast: LetOrConst): Unit = ast match {
    case LetOrConst0(_, _) => job(ast)
    case LetOrConst1(_, _) => job(ast)
  }
  def walk(ast: BindingList): Unit = ast match {
    case BindingList0(x0, _, _) =>
      job(ast); walk(x0)
    case BindingList1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: LexicalBinding): Unit = ast match {
    case LexicalBinding0(x0, x1, _, _) =>
      job(ast); walk(x0); walk[Initializer](x1, walk)
    case LexicalBinding1(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
  }
  def walk(ast: VariableStatement): Unit = ast match {
    case VariableStatement0(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: VariableDeclarationList): Unit = ast match {
    case VariableDeclarationList0(x0, _, _) =>
      job(ast); walk(x0)
    case VariableDeclarationList1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: VariableDeclaration): Unit = ast match {
    case VariableDeclaration0(x0, x1, _, _) =>
      job(ast); walk(x0); walk[Initializer](x1, walk)
    case VariableDeclaration1(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
  }
  def walk(ast: BindingPattern): Unit = ast match {
    case BindingPattern0(x0, _, _) =>
      job(ast); walk(x0)
    case BindingPattern1(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ObjectBindingPattern): Unit = ast match {
    case ObjectBindingPattern0(_, _) => job(ast)
    case ObjectBindingPattern1(x1, _, _) =>
      job(ast); walk(x1)
    case ObjectBindingPattern2(x1, _, _) =>
      job(ast); walk(x1)
    case ObjectBindingPattern3(x1, x3, _, _) =>
      job(ast); walk(x1); walk[BindingRestProperty](x3, walk)
  }
  def walk(ast: ArrayBindingPattern): Unit = ast match {
    case ArrayBindingPattern0(x1, x2, _, _) =>
      job(ast); walk[Elision](x1, walk); walk[BindingRestElement](x2, walk)
    case ArrayBindingPattern1(x1, _, _) =>
      job(ast); walk(x1)
    case ArrayBindingPattern2(x1, x3, x4, _, _) =>
      job(ast); walk(x1); walk[Elision](x3, walk); walk[BindingRestElement](x4, walk)
  }
  def walk(ast: BindingRestProperty): Unit = ast match {
    case BindingRestProperty0(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: BindingPropertyList): Unit = ast match {
    case BindingPropertyList0(x0, _, _) =>
      job(ast); walk(x0)
    case BindingPropertyList1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: BindingElementList): Unit = ast match {
    case BindingElementList0(x0, _, _) =>
      job(ast); walk(x0)
    case BindingElementList1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: BindingElisionElement): Unit = ast match {
    case BindingElisionElement0(x0, x1, _, _) =>
      job(ast); walk[Elision](x0, walk); walk(x1)
  }
  def walk(ast: BindingProperty): Unit = ast match {
    case BindingProperty0(x0, _, _) =>
      job(ast); walk(x0)
    case BindingProperty1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: BindingElement): Unit = ast match {
    case BindingElement0(x0, _, _) =>
      job(ast); walk(x0)
    case BindingElement1(x0, x1, _, _) =>
      job(ast); walk(x0); walk[Initializer](x1, walk)
  }
  def walk(ast: SingleNameBinding): Unit = ast match {
    case SingleNameBinding0(x0, x1, _, _) =>
      job(ast); walk(x0); walk[Initializer](x1, walk)
  }
  def walk(ast: BindingRestElement): Unit = ast match {
    case BindingRestElement0(x1, _, _) =>
      job(ast); walk(x1)
    case BindingRestElement1(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: EmptyStatement): Unit = ast match {
    case EmptyStatement0(_, _) => job(ast)
  }
  def walk(ast: ExpressionStatement): Unit = ast match {
    case ExpressionStatement0(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: IfStatement): Unit = ast match {
    case IfStatement0(x2, x4, x6, _, _) =>
      job(ast); walk(x2); walk(x4); walk(x6)
    case IfStatement1(x2, x4, _, _) =>
      job(ast); walk(x2); walk(x4)
  }
  def walk(ast: IterationStatement): Unit = ast match {
    case IterationStatement0(x0, _, _) =>
      job(ast); walk(x0)
    case IterationStatement1(x0, _, _) =>
      job(ast); walk(x0)
    case IterationStatement2(x0, _, _) =>
      job(ast); walk(x0)
    case IterationStatement3(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: DoWhileStatement): Unit = ast match {
    case DoWhileStatement0(x1, x4, _, _) =>
      job(ast); walk(x1); walk(x4)
  }
  def walk(ast: WhileStatement): Unit = ast match {
    case WhileStatement0(x2, x4, _, _) =>
      job(ast); walk(x2); walk(x4)
  }
  def walk(ast: ForStatement): Unit = ast match {
    case ForStatement0(x3, x5, x7, x9, _, _) =>
      job(ast); walk[Expression](x3, walk); walk[Expression](x5, walk); walk[Expression](x7, walk); walk(x9)
    case ForStatement1(x3, x5, x7, x9, _, _) =>
      job(ast); walk(x3); walk[Expression](x5, walk); walk[Expression](x7, walk); walk(x9)
    case ForStatement2(x2, x3, x5, x7, _, _) =>
      job(ast); walk(x2); walk[Expression](x3, walk); walk[Expression](x5, walk); walk(x7)
  }
  def walk(ast: ForInOfStatement): Unit = ast match {
    case ForInOfStatement0(x3, x5, x7, _, _) =>
      job(ast); walk(x3); walk(x5); walk(x7)
    case ForInOfStatement1(x3, x5, x7, _, _) =>
      job(ast); walk(x3); walk(x5); walk(x7)
    case ForInOfStatement2(x2, x4, x6, _, _) =>
      job(ast); walk(x2); walk(x4); walk(x6)
    case ForInOfStatement3(x3, x5, x7, _, _) =>
      job(ast); walk(x3); walk(x5); walk(x7)
    case ForInOfStatement4(x3, x5, x7, _, _) =>
      job(ast); walk(x3); walk(x5); walk(x7)
    case ForInOfStatement5(x2, x4, x6, _, _) =>
      job(ast); walk(x2); walk(x4); walk(x6)
    case ForInOfStatement6(x4, x6, x8, _, _) =>
      job(ast); walk(x4); walk(x6); walk(x8)
    case ForInOfStatement7(x4, x6, x8, _, _) =>
      job(ast); walk(x4); walk(x6); walk(x8)
    case ForInOfStatement8(x3, x5, x7, _, _) =>
      job(ast); walk(x3); walk(x5); walk(x7)
  }
  def walk(ast: ForDeclaration): Unit = ast match {
    case ForDeclaration0(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
  }
  def walk(ast: ForBinding): Unit = ast match {
    case ForBinding0(x0, _, _) =>
      job(ast); walk(x0)
    case ForBinding1(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ContinueStatement): Unit = ast match {
    case ContinueStatement0(_, _) => job(ast)
    case ContinueStatement1(x2, _, _) =>
      job(ast); walk(x2)
  }
  def walk(ast: BreakStatement): Unit = ast match {
    case BreakStatement0(_, _) => job(ast)
    case BreakStatement1(x2, _, _) =>
      job(ast); walk(x2)
  }
  def walk(ast: ReturnStatement): Unit = ast match {
    case ReturnStatement0(_, _) => job(ast)
    case ReturnStatement1(x2, _, _) =>
      job(ast); walk(x2)
  }
  def walk(ast: WithStatement): Unit = ast match {
    case WithStatement0(x2, x4, _, _) =>
      job(ast); walk(x2); walk(x4)
  }
  def walk(ast: SwitchStatement): Unit = ast match {
    case SwitchStatement0(x2, x4, _, _) =>
      job(ast); walk(x2); walk(x4)
  }
  def walk(ast: CaseBlock): Unit = ast match {
    case CaseBlock0(x1, _, _) =>
      job(ast); walk[CaseClauses](x1, walk)
    case CaseBlock1(x1, x2, x3, _, _) =>
      job(ast); walk[CaseClauses](x1, walk); walk(x2); walk[CaseClauses](x3, walk)
  }
  def walk(ast: CaseClauses): Unit = ast match {
    case CaseClauses0(x0, _, _) =>
      job(ast); walk(x0)
    case CaseClauses1(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
  }
  def walk(ast: CaseClause): Unit = ast match {
    case CaseClause0(x1, x3, _, _) =>
      job(ast); walk(x1); walk[StatementList](x3, walk)
  }
  def walk(ast: DefaultClause): Unit = ast match {
    case DefaultClause0(x2, _, _) =>
      job(ast); walk[StatementList](x2, walk)
  }
  def walk(ast: LabelledStatement): Unit = ast match {
    case LabelledStatement0(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: LabelledItem): Unit = ast match {
    case LabelledItem0(x0, _, _) =>
      job(ast); walk(x0)
    case LabelledItem1(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ThrowStatement): Unit = ast match {
    case ThrowStatement0(x2, _, _) =>
      job(ast); walk(x2)
  }
  def walk(ast: TryStatement): Unit = ast match {
    case TryStatement0(x1, x2, _, _) =>
      job(ast); walk(x1); walk(x2)
    case TryStatement1(x1, x2, _, _) =>
      job(ast); walk(x1); walk(x2)
    case TryStatement2(x1, x2, x3, _, _) =>
      job(ast); walk(x1); walk(x2); walk(x3)
  }
  def walk(ast: Catch): Unit = ast match {
    case Catch0(x2, x4, _, _) =>
      job(ast); walk(x2); walk(x4)
    case Catch1(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: Finally): Unit = ast match {
    case Finally0(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: CatchParameter): Unit = ast match {
    case CatchParameter0(x0, _, _) =>
      job(ast); walk(x0)
    case CatchParameter1(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: DebuggerStatement): Unit = ast match {
    case DebuggerStatement0(_, _) => job(ast)
  }
  def walk(ast: UniqueFormalParameters): Unit = ast match {
    case UniqueFormalParameters0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: FormalParameters): Unit = ast match {
    case FormalParameters0(_, _) => job(ast)
    case FormalParameters1(x0, _, _) =>
      job(ast); walk(x0)
    case FormalParameters2(x0, _, _) =>
      job(ast); walk(x0)
    case FormalParameters3(x0, _, _) =>
      job(ast); walk(x0)
    case FormalParameters4(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: FormalParameterList): Unit = ast match {
    case FormalParameterList0(x0, _, _) =>
      job(ast); walk(x0)
    case FormalParameterList1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: FunctionRestParameter): Unit = ast match {
    case FunctionRestParameter0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: FormalParameter): Unit = ast match {
    case FormalParameter0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: FunctionDeclaration): Unit = ast match {
    case FunctionDeclaration0(x1, x3, x6, _, _) =>
      job(ast); walk(x1); walk(x3); walk(x6)
    case FunctionDeclaration1(x2, x5, _, _) =>
      job(ast); walk(x2); walk(x5)
  }
  def walk(ast: FunctionExpression): Unit = ast match {
    case FunctionExpression0(x1, x3, x6, _, _) =>
      job(ast); walk[BindingIdentifier](x1, walk); walk(x3); walk(x6)
  }
  def walk(ast: FunctionBody): Unit = ast match {
    case FunctionBody0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: FunctionStatementList): Unit = ast match {
    case FunctionStatementList0(x0, _, _) =>
      job(ast); walk[StatementList](x0, walk)
  }
  def walk(ast: ArrowFunction): Unit = ast match {
    case ArrowFunction0(x0, x3, _, _) =>
      job(ast); walk(x0); walk(x3)
  }
  def walk(ast: ArrowParameters): Unit = ast match {
    case ArrowParameters0(x0, _, _) =>
      job(ast); walk(x0)
    case ArrowParameters1(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ConciseBody): Unit = ast match {
    case ConciseBody0(x1, _, _) =>
      job(ast); walk(x1)
    case ConciseBody1(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: ExpressionBody): Unit = ast match {
    case ExpressionBody0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ArrowFormalParameters): Unit = ast match {
    case ArrowFormalParameters0(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: MethodDefinition): Unit = ast match {
    case MethodDefinition0(x0, x2, x5, _, _) =>
      job(ast); walk(x0); walk(x2); walk(x5)
    case MethodDefinition1(x0, _, _) =>
      job(ast); walk(x0)
    case MethodDefinition2(x0, _, _) =>
      job(ast); walk(x0)
    case MethodDefinition3(x0, _, _) =>
      job(ast); walk(x0)
    case MethodDefinition4(x1, x5, _, _) =>
      job(ast); walk(x1); walk(x5)
    case MethodDefinition5(x1, x3, x6, _, _) =>
      job(ast); walk(x1); walk(x3); walk(x6)
  }
  def walk(ast: PropertySetParameterList): Unit = ast match {
    case PropertySetParameterList0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: GeneratorMethod): Unit = ast match {
    case GeneratorMethod0(x1, x3, x6, _, _) =>
      job(ast); walk(x1); walk(x3); walk(x6)
  }
  def walk(ast: GeneratorDeclaration): Unit = ast match {
    case GeneratorDeclaration0(x2, x4, x7, _, _) =>
      job(ast); walk(x2); walk(x4); walk(x7)
    case GeneratorDeclaration1(x3, x6, _, _) =>
      job(ast); walk(x3); walk(x6)
  }
  def walk(ast: GeneratorExpression): Unit = ast match {
    case GeneratorExpression0(x2, x4, x7, _, _) =>
      job(ast); walk[BindingIdentifier](x2, walk); walk(x4); walk(x7)
  }
  def walk(ast: GeneratorBody): Unit = ast match {
    case GeneratorBody0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: YieldExpression): Unit = ast match {
    case YieldExpression0(_, _) => job(ast)
    case YieldExpression1(x2, _, _) =>
      job(ast); walk(x2)
    case YieldExpression2(x3, _, _) =>
      job(ast); walk(x3)
  }
  def walk(ast: AsyncGeneratorMethod): Unit = ast match {
    case AsyncGeneratorMethod0(x3, x5, x8, _, _) =>
      job(ast); walk(x3); walk(x5); walk(x8)
  }
  def walk(ast: AsyncGeneratorDeclaration): Unit = ast match {
    case AsyncGeneratorDeclaration0(x4, x6, x9, _, _) =>
      job(ast); walk(x4); walk(x6); walk(x9)
    case AsyncGeneratorDeclaration1(x5, x8, _, _) =>
      job(ast); walk(x5); walk(x8)
  }
  def walk(ast: AsyncGeneratorExpression): Unit = ast match {
    case AsyncGeneratorExpression0(x4, x6, x9, _, _) =>
      job(ast); walk[BindingIdentifier](x4, walk); walk(x6); walk(x9)
  }
  def walk(ast: AsyncGeneratorBody): Unit = ast match {
    case AsyncGeneratorBody0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ClassDeclaration): Unit = ast match {
    case ClassDeclaration0(x1, x2, _, _) =>
      job(ast); walk(x1); walk(x2)
    case ClassDeclaration1(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: ClassExpression): Unit = ast match {
    case ClassExpression0(x1, x2, _, _) =>
      job(ast); walk[BindingIdentifier](x1, walk); walk(x2)
  }
  def walk(ast: ClassTail): Unit = ast match {
    case ClassTail0(x0, x2, _, _) =>
      job(ast); walk[ClassHeritage](x0, walk); walk[ClassBody](x2, walk)
  }
  def walk(ast: ClassHeritage): Unit = ast match {
    case ClassHeritage0(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: ClassBody): Unit = ast match {
    case ClassBody0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ClassElementList): Unit = ast match {
    case ClassElementList0(x0, _, _) =>
      job(ast); walk(x0)
    case ClassElementList1(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
  }
  def walk(ast: ClassElement): Unit = ast match {
    case ClassElement0(x0, _, _) =>
      job(ast); walk(x0)
    case ClassElement1(x1, _, _) =>
      job(ast); walk(x1)
    case ClassElement2(_, _) => job(ast)
  }
  def walk(ast: AsyncFunctionDeclaration): Unit = ast match {
    case AsyncFunctionDeclaration0(x3, x5, x8, _, _) =>
      job(ast); walk(x3); walk(x5); walk(x8)
    case AsyncFunctionDeclaration1(x4, x7, _, _) =>
      job(ast); walk(x4); walk(x7)
  }
  def walk(ast: AsyncFunctionExpression): Unit = ast match {
    case AsyncFunctionExpression0(x3, x5, x8, _, _) =>
      job(ast); walk[BindingIdentifier](x3, walk); walk(x5); walk(x8)
  }
  def walk(ast: AsyncMethod): Unit = ast match {
    case AsyncMethod0(x2, x4, x7, _, _) =>
      job(ast); walk(x2); walk(x4); walk(x7)
  }
  def walk(ast: AsyncFunctionBody): Unit = ast match {
    case AsyncFunctionBody0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: AwaitExpression): Unit = ast match {
    case AwaitExpression0(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: AsyncArrowFunction): Unit = ast match {
    case AsyncArrowFunction0(x2, x5, _, _) =>
      job(ast); walk(x2); walk(x5)
    case AsyncArrowFunction1(x0, x3, _, _) =>
      job(ast); walk(x0); walk(x3)
  }
  def walk(ast: AsyncConciseBody): Unit = ast match {
    case AsyncConciseBody0(x1, _, _) =>
      job(ast); walk(x1)
    case AsyncConciseBody1(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: AsyncArrowBindingIdentifier): Unit = ast match {
    case AsyncArrowBindingIdentifier0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: CoverCallExpressionAndAsyncArrowHead): Unit = ast match {
    case CoverCallExpressionAndAsyncArrowHead0(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
  }
  def walk(ast: AsyncArrowHead): Unit = ast match {
    case AsyncArrowHead0(x2, _, _) =>
      job(ast); walk(x2)
  }
  def walk(ast: Script): Unit = ast match {
    case Script0(x0, _, _) =>
      job(ast); walk[ScriptBody](x0, walk)
  }
  def walk(ast: ScriptBody): Unit = ast match {
    case ScriptBody0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: Module): Unit = ast match {
    case Module0(x0, _, _) =>
      job(ast); walk[ModuleBody](x0, walk)
  }
  def walk(ast: ModuleBody): Unit = ast match {
    case ModuleBody0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ModuleItemList): Unit = ast match {
    case ModuleItemList0(x0, _, _) =>
      job(ast); walk(x0)
    case ModuleItemList1(x0, x1, _, _) =>
      job(ast); walk(x0); walk(x1)
  }
  def walk(ast: ModuleItem): Unit = ast match {
    case ModuleItem0(x0, _, _) =>
      job(ast); walk(x0)
    case ModuleItem1(x0, _, _) =>
      job(ast); walk(x0)
    case ModuleItem2(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ImportDeclaration): Unit = ast match {
    case ImportDeclaration0(x1, x2, _, _) =>
      job(ast); walk(x1); walk(x2)
    case ImportDeclaration1(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: ImportClause): Unit = ast match {
    case ImportClause0(x0, _, _) =>
      job(ast); walk(x0)
    case ImportClause1(x0, _, _) =>
      job(ast); walk(x0)
    case ImportClause2(x0, _, _) =>
      job(ast); walk(x0)
    case ImportClause3(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
    case ImportClause4(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: ImportedDefaultBinding): Unit = ast match {
    case ImportedDefaultBinding0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: NameSpaceImport): Unit = ast match {
    case NameSpaceImport0(x2, _, _) =>
      job(ast); walk(x2)
  }
  def walk(ast: NamedImports): Unit = ast match {
    case NamedImports0(_, _) => job(ast)
    case NamedImports1(x1, _, _) =>
      job(ast); walk(x1)
    case NamedImports2(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: FromClause): Unit = ast match {
    case FromClause0(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: ImportsList): Unit = ast match {
    case ImportsList0(x0, _, _) =>
      job(ast); walk(x0)
    case ImportsList1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: ImportSpecifier): Unit = ast match {
    case ImportSpecifier0(x0, _, _) =>
      job(ast); walk(x0)
    case ImportSpecifier1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: ModuleSpecifier): Unit = ast match {
    case ModuleSpecifier0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ImportedBinding): Unit = ast match {
    case ImportedBinding0(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: ExportDeclaration): Unit = ast match {
    case ExportDeclaration0(x1, x2, _, _) =>
      job(ast); walk(x1); walk(x2)
    case ExportDeclaration1(x1, _, _) =>
      job(ast); walk(x1)
    case ExportDeclaration2(x1, _, _) =>
      job(ast); walk(x1)
    case ExportDeclaration3(x1, _, _) =>
      job(ast); walk(x1)
    case ExportDeclaration4(x2, _, _) =>
      job(ast); walk(x2)
    case ExportDeclaration5(x2, _, _) =>
      job(ast); walk(x2)
    case ExportDeclaration6(x3, _, _) =>
      job(ast); walk(x3)
  }
  def walk(ast: ExportFromClause): Unit = ast match {
    case ExportFromClause0(_, _) => job(ast)
    case ExportFromClause1(x2, _, _) =>
      job(ast); walk(x2)
    case ExportFromClause2(x0, _, _) =>
      job(ast); walk(x0)
  }
  def walk(ast: NamedExports): Unit = ast match {
    case NamedExports0(_, _) => job(ast)
    case NamedExports1(x1, _, _) =>
      job(ast); walk(x1)
    case NamedExports2(x1, _, _) =>
      job(ast); walk(x1)
  }
  def walk(ast: ExportsList): Unit = ast match {
    case ExportsList0(x0, _, _) =>
      job(ast); walk(x0)
    case ExportsList1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
  def walk(ast: ExportSpecifier): Unit = ast match {
    case ExportSpecifier0(x0, _, _) =>
      job(ast); walk(x0)
    case ExportSpecifier1(x0, x2, _, _) =>
      job(ast); walk(x0); walk(x2)
  }
}

package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset.js.ast._

trait ASTTransformer {
  def job(ast: AST): Unit = {}
  def transform[T](opt: Option[T], w: T => T): Option[T] = opt.map(w)
  def transform(lex: Lexical): Lexical = lex

  def transform(ast: IdentifierReference): IdentifierReference = ast match {
    case IdentifierReference0(x0, params, span) =>
      job(ast); IdentifierReference0(transform(x0), params, span)
    case IdentifierReference1(params, span) => { job(ast); ast }
    case IdentifierReference2(params, span) => { job(ast); ast }
  }
  def transform(ast: BindingIdentifier): BindingIdentifier = ast match {
    case BindingIdentifier0(x0, params, span) =>
      job(ast); BindingIdentifier0(transform(x0), params, span)
    case BindingIdentifier1(params, span) => { job(ast); ast }
    case BindingIdentifier2(params, span) => { job(ast); ast }
  }
  def transform(ast: LabelIdentifier): LabelIdentifier = ast match {
    case LabelIdentifier0(x0, params, span) =>
      job(ast); LabelIdentifier0(transform(x0), params, span)
    case LabelIdentifier1(params, span) => { job(ast); ast }
    case LabelIdentifier2(params, span) => { job(ast); ast }
  }
  def transform(ast: Identifier): Identifier = ast match {
    case Identifier0(x0, params, span) =>
      job(ast); Identifier0(transform(x0), params, span)
  }
  def transform(ast: PrimaryExpression): PrimaryExpression = ast match {
    case PrimaryExpression0(params, span) => { job(ast); ast }
    case PrimaryExpression1(x0, params, span) =>
      job(ast); PrimaryExpression1(transform(x0), params, span)
    case PrimaryExpression2(x0, params, span) =>
      job(ast); PrimaryExpression2(transform(x0), params, span)
    case PrimaryExpression3(x0, params, span) =>
      job(ast); PrimaryExpression3(transform(x0), params, span)
    case PrimaryExpression4(x0, params, span) =>
      job(ast); PrimaryExpression4(transform(x0), params, span)
    case PrimaryExpression5(x0, params, span) =>
      job(ast); PrimaryExpression5(transform(x0), params, span)
    case PrimaryExpression6(x0, params, span) =>
      job(ast); PrimaryExpression6(transform(x0), params, span)
    case PrimaryExpression7(x0, params, span) =>
      job(ast); PrimaryExpression7(transform(x0), params, span)
    case PrimaryExpression8(x0, params, span) =>
      job(ast); PrimaryExpression8(transform(x0), params, span)
    case PrimaryExpression9(x0, params, span) =>
      job(ast); PrimaryExpression9(transform(x0), params, span)
    case PrimaryExpression10(x0, params, span) =>
      job(ast); PrimaryExpression10(transform(x0), params, span)
    case PrimaryExpression11(x0, params, span) =>
      job(ast); PrimaryExpression11(transform(x0), params, span)
    case PrimaryExpression12(x0, params, span) =>
      job(ast); PrimaryExpression12(transform(x0), params, span)
  }
  def transform(ast: CoverParenthesizedExpressionAndArrowParameterList): CoverParenthesizedExpressionAndArrowParameterList = ast match {
    case CoverParenthesizedExpressionAndArrowParameterList0(x1, params, span) =>
      job(ast); CoverParenthesizedExpressionAndArrowParameterList0(transform(x1), params, span)
    case CoverParenthesizedExpressionAndArrowParameterList1(x1, params, span) =>
      job(ast); CoverParenthesizedExpressionAndArrowParameterList1(transform(x1), params, span)
    case CoverParenthesizedExpressionAndArrowParameterList2(params, span) => { job(ast); ast }
    case CoverParenthesizedExpressionAndArrowParameterList3(x2, params, span) =>
      job(ast); CoverParenthesizedExpressionAndArrowParameterList3(transform(x2), params, span)
    case CoverParenthesizedExpressionAndArrowParameterList4(x2, params, span) =>
      job(ast); CoverParenthesizedExpressionAndArrowParameterList4(transform(x2), params, span)
    case CoverParenthesizedExpressionAndArrowParameterList5(x1, x4, params, span) =>
      job(ast); CoverParenthesizedExpressionAndArrowParameterList5(transform(x1), transform(x4), params, span)
    case CoverParenthesizedExpressionAndArrowParameterList6(x1, x4, params, span) =>
      job(ast); CoverParenthesizedExpressionAndArrowParameterList6(transform(x1), transform(x4), params, span)
  }
  def transform(ast: ParenthesizedExpression): ParenthesizedExpression = ast match {
    case ParenthesizedExpression0(x1, params, span) =>
      job(ast); ParenthesizedExpression0(transform(x1), params, span)
  }
  def transform(ast: Literal): Literal = ast match {
    case Literal0(x0, params, span) =>
      job(ast); Literal0(transform(x0), params, span)
    case Literal1(x0, params, span) =>
      job(ast); Literal1(transform(x0), params, span)
    case Literal2(x0, params, span) =>
      job(ast); Literal2(transform(x0), params, span)
    case Literal3(x0, params, span) =>
      job(ast); Literal3(transform(x0), params, span)
  }
  def transform(ast: ArrayLiteral): ArrayLiteral = ast match {
    case ArrayLiteral0(x1, params, span) =>
      job(ast); ArrayLiteral0(transform[Elision](x1, transform), params, span)
    case ArrayLiteral1(x1, params, span) =>
      job(ast); ArrayLiteral1(transform(x1), params, span)
    case ArrayLiteral2(x1, x3, params, span) =>
      job(ast); ArrayLiteral2(transform(x1), transform[Elision](x3, transform), params, span)
  }
  def transform(ast: ElementList): ElementList = ast match {
    case ElementList0(x0, x1, params, span) =>
      job(ast); ElementList0(transform[Elision](x0, transform), transform(x1), params, span)
    case ElementList1(x0, x1, params, span) =>
      job(ast); ElementList1(transform[Elision](x0, transform), transform(x1), params, span)
    case ElementList2(x0, x2, x3, params, span) =>
      job(ast); ElementList2(transform(x0), transform[Elision](x2, transform), transform(x3), params, span)
    case ElementList3(x0, x2, x3, params, span) =>
      job(ast); ElementList3(transform(x0), transform[Elision](x2, transform), transform(x3), params, span)
  }
  def transform(ast: Elision): Elision = ast match {
    case Elision0(params, span) => { job(ast); ast }
    case Elision1(x0, params, span) =>
      job(ast); Elision1(transform(x0), params, span)
  }
  def transform(ast: SpreadElement): SpreadElement = ast match {
    case SpreadElement0(x1, params, span) =>
      job(ast); SpreadElement0(transform(x1), params, span)
  }
  def transform(ast: ObjectLiteral): ObjectLiteral = ast match {
    case ObjectLiteral0(params, span) => { job(ast); ast }
    case ObjectLiteral1(x1, params, span) =>
      job(ast); ObjectLiteral1(transform(x1), params, span)
    case ObjectLiteral2(x1, params, span) =>
      job(ast); ObjectLiteral2(transform(x1), params, span)
  }
  def transform(ast: PropertyDefinitionList): PropertyDefinitionList = ast match {
    case PropertyDefinitionList0(x0, params, span) =>
      job(ast); PropertyDefinitionList0(transform(x0), params, span)
    case PropertyDefinitionList1(x0, x2, params, span) =>
      job(ast); PropertyDefinitionList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: PropertyDefinition): PropertyDefinition = ast match {
    case PropertyDefinition0(x0, params, span) =>
      job(ast); PropertyDefinition0(transform(x0), params, span)
    case PropertyDefinition1(x0, params, span) =>
      job(ast); PropertyDefinition1(transform(x0), params, span)
    case PropertyDefinition2(x0, x2, params, span) =>
      job(ast); PropertyDefinition2(transform(x0), transform(x2), params, span)
    case PropertyDefinition3(x0, params, span) =>
      job(ast); PropertyDefinition3(transform(x0), params, span)
    case PropertyDefinition4(x1, params, span) =>
      job(ast); PropertyDefinition4(transform(x1), params, span)
  }
  def transform(ast: PropertyName): PropertyName = ast match {
    case PropertyName0(x0, params, span) =>
      job(ast); PropertyName0(transform(x0), params, span)
    case PropertyName1(x0, params, span) =>
      job(ast); PropertyName1(transform(x0), params, span)
  }
  def transform(ast: LiteralPropertyName): LiteralPropertyName = ast match {
    case LiteralPropertyName0(x0, params, span) =>
      job(ast); LiteralPropertyName0(transform(x0), params, span)
    case LiteralPropertyName1(x0, params, span) =>
      job(ast); LiteralPropertyName1(transform(x0), params, span)
    case LiteralPropertyName2(x0, params, span) =>
      job(ast); LiteralPropertyName2(transform(x0), params, span)
  }
  def transform(ast: ComputedPropertyName): ComputedPropertyName = ast match {
    case ComputedPropertyName0(x1, params, span) =>
      job(ast); ComputedPropertyName0(transform(x1), params, span)
  }
  def transform(ast: CoverInitializedName): CoverInitializedName = ast match {
    case CoverInitializedName0(x0, x1, params, span) =>
      job(ast); CoverInitializedName0(transform(x0), transform(x1), params, span)
  }
  def transform(ast: Initializer): Initializer = ast match {
    case Initializer0(x1, params, span) =>
      job(ast); Initializer0(transform(x1), params, span)
  }
  def transform(ast: TemplateLiteral): TemplateLiteral = ast match {
    case TemplateLiteral0(x0, params, span) =>
      job(ast); TemplateLiteral0(transform(x0), params, span)
    case TemplateLiteral1(x0, params, span) =>
      job(ast); TemplateLiteral1(transform(x0), params, span)
  }
  def transform(ast: SubstitutionTemplate): SubstitutionTemplate = ast match {
    case SubstitutionTemplate0(x0, x1, x2, params, span) =>
      job(ast); SubstitutionTemplate0(transform(x0), transform(x1), transform(x2), params, span)
  }
  def transform(ast: TemplateSpans): TemplateSpans = ast match {
    case TemplateSpans0(x0, params, span) =>
      job(ast); TemplateSpans0(transform(x0), params, span)
    case TemplateSpans1(x0, x1, params, span) =>
      job(ast); TemplateSpans1(transform(x0), transform(x1), params, span)
  }
  def transform(ast: TemplateMiddleList): TemplateMiddleList = ast match {
    case TemplateMiddleList0(x0, x1, params, span) =>
      job(ast); TemplateMiddleList0(transform(x0), transform(x1), params, span)
    case TemplateMiddleList1(x0, x1, x2, params, span) =>
      job(ast); TemplateMiddleList1(transform(x0), transform(x1), transform(x2), params, span)
  }
  def transform(ast: MemberExpression): MemberExpression = ast match {
    case MemberExpression0(x0, params, span) =>
      job(ast); MemberExpression0(transform(x0), params, span)
    case MemberExpression1(x0, x2, params, span) =>
      job(ast); MemberExpression1(transform(x0), transform(x2), params, span)
    case MemberExpression2(x0, x2, params, span) =>
      job(ast); MemberExpression2(transform(x0), transform(x2), params, span)
    case MemberExpression3(x0, x1, params, span) =>
      job(ast); MemberExpression3(transform(x0), transform(x1), params, span)
    case MemberExpression4(x0, params, span) =>
      job(ast); MemberExpression4(transform(x0), params, span)
    case MemberExpression5(x0, params, span) =>
      job(ast); MemberExpression5(transform(x0), params, span)
    case MemberExpression6(x1, x2, params, span) =>
      job(ast); MemberExpression6(transform(x1), transform(x2), params, span)
  }
  def transform(ast: SuperProperty): SuperProperty = ast match {
    case SuperProperty0(x2, params, span) =>
      job(ast); SuperProperty0(transform(x2), params, span)
    case SuperProperty1(x2, params, span) =>
      job(ast); SuperProperty1(transform(x2), params, span)
  }
  def transform(ast: MetaProperty): MetaProperty = ast match {
    case MetaProperty0(x0, params, span) =>
      job(ast); MetaProperty0(transform(x0), params, span)
    case MetaProperty1(x0, params, span) =>
      job(ast); MetaProperty1(transform(x0), params, span)
  }
  def transform(ast: NewTarget): NewTarget = ast match {
    case NewTarget0(params, span) => { job(ast); ast }
  }
  def transform(ast: ImportMeta): ImportMeta = ast match {
    case ImportMeta0(params, span) => { job(ast); ast }
  }
  def transform(ast: NewExpression): NewExpression = ast match {
    case NewExpression0(x0, params, span) =>
      job(ast); NewExpression0(transform(x0), params, span)
    case NewExpression1(x1, params, span) =>
      job(ast); NewExpression1(transform(x1), params, span)
  }
  def transform(ast: CallExpression): CallExpression = ast match {
    case CallExpression0(x0, params, span) =>
      job(ast); CallExpression0(transform(x0), params, span)
    case CallExpression1(x0, params, span) =>
      job(ast); CallExpression1(transform(x0), params, span)
    case CallExpression2(x0, params, span) =>
      job(ast); CallExpression2(transform(x0), params, span)
    case CallExpression3(x0, x1, params, span) =>
      job(ast); CallExpression3(transform(x0), transform(x1), params, span)
    case CallExpression4(x0, x2, params, span) =>
      job(ast); CallExpression4(transform(x0), transform(x2), params, span)
    case CallExpression5(x0, x2, params, span) =>
      job(ast); CallExpression5(transform(x0), transform(x2), params, span)
    case CallExpression6(x0, x1, params, span) =>
      job(ast); CallExpression6(transform(x0), transform(x1), params, span)
  }
  def transform(ast: SuperCall): SuperCall = ast match {
    case SuperCall0(x1, params, span) =>
      job(ast); SuperCall0(transform(x1), params, span)
  }
  def transform(ast: ImportCall): ImportCall = ast match {
    case ImportCall0(x2, params, span) =>
      job(ast); ImportCall0(transform(x2), params, span)
  }
  def transform(ast: Arguments): Arguments = ast match {
    case Arguments0(params, span) => { job(ast); ast }
    case Arguments1(x1, params, span) =>
      job(ast); Arguments1(transform(x1), params, span)
    case Arguments2(x1, params, span) =>
      job(ast); Arguments2(transform(x1), params, span)
  }
  def transform(ast: ArgumentList): ArgumentList = ast match {
    case ArgumentList0(x0, params, span) =>
      job(ast); ArgumentList0(transform(x0), params, span)
    case ArgumentList1(x1, params, span) =>
      job(ast); ArgumentList1(transform(x1), params, span)
    case ArgumentList2(x0, x2, params, span) =>
      job(ast); ArgumentList2(transform(x0), transform(x2), params, span)
    case ArgumentList3(x0, x3, params, span) =>
      job(ast); ArgumentList3(transform(x0), transform(x3), params, span)
  }
  def transform(ast: OptionalExpression): OptionalExpression = ast match {
    case OptionalExpression0(x0, x1, params, span) =>
      job(ast); OptionalExpression0(transform(x0), transform(x1), params, span)
    case OptionalExpression1(x0, x1, params, span) =>
      job(ast); OptionalExpression1(transform(x0), transform(x1), params, span)
    case OptionalExpression2(x0, x1, params, span) =>
      job(ast); OptionalExpression2(transform(x0), transform(x1), params, span)
  }
  def transform(ast: OptionalChain): OptionalChain = ast match {
    case OptionalChain0(x1, params, span) =>
      job(ast); OptionalChain0(transform(x1), params, span)
    case OptionalChain1(x2, params, span) =>
      job(ast); OptionalChain1(transform(x2), params, span)
    case OptionalChain2(x1, params, span) =>
      job(ast); OptionalChain2(transform(x1), params, span)
    case OptionalChain3(x1, params, span) =>
      job(ast); OptionalChain3(transform(x1), params, span)
    case OptionalChain4(x0, x1, params, span) =>
      job(ast); OptionalChain4(transform(x0), transform(x1), params, span)
    case OptionalChain5(x0, x2, params, span) =>
      job(ast); OptionalChain5(transform(x0), transform(x2), params, span)
    case OptionalChain6(x0, x2, params, span) =>
      job(ast); OptionalChain6(transform(x0), transform(x2), params, span)
    case OptionalChain7(x0, x1, params, span) =>
      job(ast); OptionalChain7(transform(x0), transform(x1), params, span)
  }
  def transform(ast: LeftHandSideExpression): LeftHandSideExpression = ast match {
    case LeftHandSideExpression0(x0, params, span) =>
      job(ast); LeftHandSideExpression0(transform(x0), params, span)
    case LeftHandSideExpression1(x0, params, span) =>
      job(ast); LeftHandSideExpression1(transform(x0), params, span)
    case LeftHandSideExpression2(x0, params, span) =>
      job(ast); LeftHandSideExpression2(transform(x0), params, span)
  }
  def transform(ast: CallMemberExpression): CallMemberExpression = ast match {
    case CallMemberExpression0(x0, x1, params, span) =>
      job(ast); CallMemberExpression0(transform(x0), transform(x1), params, span)
  }
  def transform(ast: UpdateExpression): UpdateExpression = ast match {
    case UpdateExpression0(x0, params, span) =>
      job(ast); UpdateExpression0(transform(x0), params, span)
    case UpdateExpression1(x0, params, span) =>
      job(ast); UpdateExpression1(transform(x0), params, span)
    case UpdateExpression2(x0, params, span) =>
      job(ast); UpdateExpression2(transform(x0), params, span)
    case UpdateExpression3(x1, params, span) =>
      job(ast); UpdateExpression3(transform(x1), params, span)
    case UpdateExpression4(x1, params, span) =>
      job(ast); UpdateExpression4(transform(x1), params, span)
  }
  def transform(ast: UnaryExpression): UnaryExpression = ast match {
    case UnaryExpression0(x0, params, span) =>
      job(ast); UnaryExpression0(transform(x0), params, span)
    case UnaryExpression1(x1, params, span) =>
      job(ast); UnaryExpression1(transform(x1), params, span)
    case UnaryExpression2(x1, params, span) =>
      job(ast); UnaryExpression2(transform(x1), params, span)
    case UnaryExpression3(x1, params, span) =>
      job(ast); UnaryExpression3(transform(x1), params, span)
    case UnaryExpression4(x1, params, span) =>
      job(ast); UnaryExpression4(transform(x1), params, span)
    case UnaryExpression5(x1, params, span) =>
      job(ast); UnaryExpression5(transform(x1), params, span)
    case UnaryExpression6(x1, params, span) =>
      job(ast); UnaryExpression6(transform(x1), params, span)
    case UnaryExpression7(x1, params, span) =>
      job(ast); UnaryExpression7(transform(x1), params, span)
    case UnaryExpression8(x0, params, span) =>
      job(ast); UnaryExpression8(transform(x0), params, span)
  }
  def transform(ast: ExponentiationExpression): ExponentiationExpression = ast match {
    case ExponentiationExpression0(x0, params, span) =>
      job(ast); ExponentiationExpression0(transform(x0), params, span)
    case ExponentiationExpression1(x0, x2, params, span) =>
      job(ast); ExponentiationExpression1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: MultiplicativeExpression): MultiplicativeExpression = ast match {
    case MultiplicativeExpression0(x0, params, span) =>
      job(ast); MultiplicativeExpression0(transform(x0), params, span)
    case MultiplicativeExpression1(x0, x1, x2, params, span) =>
      job(ast); MultiplicativeExpression1(transform(x0), transform(x1), transform(x2), params, span)
  }
  def transform(ast: MultiplicativeOperator): MultiplicativeOperator = ast match {
    case MultiplicativeOperator0(params, span) => { job(ast); ast }
    case MultiplicativeOperator1(params, span) => { job(ast); ast }
    case MultiplicativeOperator2(params, span) => { job(ast); ast }
  }
  def transform(ast: AdditiveExpression): AdditiveExpression = ast match {
    case AdditiveExpression0(x0, params, span) =>
      job(ast); AdditiveExpression0(transform(x0), params, span)
    case AdditiveExpression1(x0, x2, params, span) =>
      job(ast); AdditiveExpression1(transform(x0), transform(x2), params, span)
    case AdditiveExpression2(x0, x2, params, span) =>
      job(ast); AdditiveExpression2(transform(x0), transform(x2), params, span)
  }
  def transform(ast: ShiftExpression): ShiftExpression = ast match {
    case ShiftExpression0(x0, params, span) =>
      job(ast); ShiftExpression0(transform(x0), params, span)
    case ShiftExpression1(x0, x2, params, span) =>
      job(ast); ShiftExpression1(transform(x0), transform(x2), params, span)
    case ShiftExpression2(x0, x2, params, span) =>
      job(ast); ShiftExpression2(transform(x0), transform(x2), params, span)
    case ShiftExpression3(x0, x2, params, span) =>
      job(ast); ShiftExpression3(transform(x0), transform(x2), params, span)
  }
  def transform(ast: RelationalExpression): RelationalExpression = ast match {
    case RelationalExpression0(x0, params, span) =>
      job(ast); RelationalExpression0(transform(x0), params, span)
    case RelationalExpression1(x0, x2, params, span) =>
      job(ast); RelationalExpression1(transform(x0), transform(x2), params, span)
    case RelationalExpression2(x0, x2, params, span) =>
      job(ast); RelationalExpression2(transform(x0), transform(x2), params, span)
    case RelationalExpression3(x0, x2, params, span) =>
      job(ast); RelationalExpression3(transform(x0), transform(x2), params, span)
    case RelationalExpression4(x0, x2, params, span) =>
      job(ast); RelationalExpression4(transform(x0), transform(x2), params, span)
    case RelationalExpression5(x0, x2, params, span) =>
      job(ast); RelationalExpression5(transform(x0), transform(x2), params, span)
    case RelationalExpression6(x0, x2, params, span) =>
      job(ast); RelationalExpression6(transform(x0), transform(x2), params, span)
  }
  def transform(ast: EqualityExpression): EqualityExpression = ast match {
    case EqualityExpression0(x0, params, span) =>
      job(ast); EqualityExpression0(transform(x0), params, span)
    case EqualityExpression1(x0, x2, params, span) =>
      job(ast); EqualityExpression1(transform(x0), transform(x2), params, span)
    case EqualityExpression2(x0, x2, params, span) =>
      job(ast); EqualityExpression2(transform(x0), transform(x2), params, span)
    case EqualityExpression3(x0, x2, params, span) =>
      job(ast); EqualityExpression3(transform(x0), transform(x2), params, span)
    case EqualityExpression4(x0, x2, params, span) =>
      job(ast); EqualityExpression4(transform(x0), transform(x2), params, span)
  }
  def transform(ast: BitwiseANDExpression): BitwiseANDExpression = ast match {
    case BitwiseANDExpression0(x0, params, span) =>
      job(ast); BitwiseANDExpression0(transform(x0), params, span)
    case BitwiseANDExpression1(x0, x2, params, span) =>
      job(ast); BitwiseANDExpression1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: BitwiseXORExpression): BitwiseXORExpression = ast match {
    case BitwiseXORExpression0(x0, params, span) =>
      job(ast); BitwiseXORExpression0(transform(x0), params, span)
    case BitwiseXORExpression1(x0, x2, params, span) =>
      job(ast); BitwiseXORExpression1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: BitwiseORExpression): BitwiseORExpression = ast match {
    case BitwiseORExpression0(x0, params, span) =>
      job(ast); BitwiseORExpression0(transform(x0), params, span)
    case BitwiseORExpression1(x0, x2, params, span) =>
      job(ast); BitwiseORExpression1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: LogicalANDExpression): LogicalANDExpression = ast match {
    case LogicalANDExpression0(x0, params, span) =>
      job(ast); LogicalANDExpression0(transform(x0), params, span)
    case LogicalANDExpression1(x0, x2, params, span) =>
      job(ast); LogicalANDExpression1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: LogicalORExpression): LogicalORExpression = ast match {
    case LogicalORExpression0(x0, params, span) =>
      job(ast); LogicalORExpression0(transform(x0), params, span)
    case LogicalORExpression1(x0, x2, params, span) =>
      job(ast); LogicalORExpression1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: CoalesceExpression): CoalesceExpression = ast match {
    case CoalesceExpression0(x0, x2, params, span) =>
      job(ast); CoalesceExpression0(transform(x0), transform(x2), params, span)
  }
  def transform(ast: CoalesceExpressionHead): CoalesceExpressionHead = ast match {
    case CoalesceExpressionHead0(x0, params, span) =>
      job(ast); CoalesceExpressionHead0(transform(x0), params, span)
    case CoalesceExpressionHead1(x0, params, span) =>
      job(ast); CoalesceExpressionHead1(transform(x0), params, span)
  }
  def transform(ast: ShortCircuitExpression): ShortCircuitExpression = ast match {
    case ShortCircuitExpression0(x0, params, span) =>
      job(ast); ShortCircuitExpression0(transform(x0), params, span)
    case ShortCircuitExpression1(x0, params, span) =>
      job(ast); ShortCircuitExpression1(transform(x0), params, span)
  }
  def transform(ast: ConditionalExpression): ConditionalExpression = ast match {
    case ConditionalExpression0(x0, params, span) =>
      job(ast); ConditionalExpression0(transform(x0), params, span)
    case ConditionalExpression1(x0, x2, x4, params, span) =>
      job(ast); ConditionalExpression1(transform(x0), transform(x2), transform(x4), params, span)
  }
  def transform(ast: AssignmentExpression): AssignmentExpression = ast match {
    case AssignmentExpression0(x0, params, span) =>
      job(ast); AssignmentExpression0(transform(x0), params, span)
    case AssignmentExpression1(x0, params, span) =>
      job(ast); AssignmentExpression1(transform(x0), params, span)
    case AssignmentExpression2(x0, params, span) =>
      job(ast); AssignmentExpression2(transform(x0), params, span)
    case AssignmentExpression3(x0, params, span) =>
      job(ast); AssignmentExpression3(transform(x0), params, span)
    case AssignmentExpression4(x0, x2, params, span) =>
      job(ast); AssignmentExpression4(transform(x0), transform(x2), params, span)
    case AssignmentExpression5(x0, x1, x2, params, span) =>
      job(ast); AssignmentExpression5(transform(x0), transform(x1), transform(x2), params, span)
    case AssignmentExpression6(x0, x2, params, span) =>
      job(ast); AssignmentExpression6(transform(x0), transform(x2), params, span)
    case AssignmentExpression7(x0, x2, params, span) =>
      job(ast); AssignmentExpression7(transform(x0), transform(x2), params, span)
    case AssignmentExpression8(x0, x2, params, span) =>
      job(ast); AssignmentExpression8(transform(x0), transform(x2), params, span)
  }
  def transform(ast: AssignmentOperator): AssignmentOperator = ast match {
    case AssignmentOperator0(params, span) => { job(ast); ast }
    case AssignmentOperator1(params, span) => { job(ast); ast }
    case AssignmentOperator2(params, span) => { job(ast); ast }
    case AssignmentOperator3(params, span) => { job(ast); ast }
    case AssignmentOperator4(params, span) => { job(ast); ast }
    case AssignmentOperator5(params, span) => { job(ast); ast }
    case AssignmentOperator6(params, span) => { job(ast); ast }
    case AssignmentOperator7(params, span) => { job(ast); ast }
    case AssignmentOperator8(params, span) => { job(ast); ast }
    case AssignmentOperator9(params, span) => { job(ast); ast }
    case AssignmentOperator10(params, span) => { job(ast); ast }
    case AssignmentOperator11(params, span) => { job(ast); ast }
  }
  def transform(ast: AssignmentPattern): AssignmentPattern = ast match {
    case AssignmentPattern0(x0, params, span) =>
      job(ast); AssignmentPattern0(transform(x0), params, span)
    case AssignmentPattern1(x0, params, span) =>
      job(ast); AssignmentPattern1(transform(x0), params, span)
  }
  def transform(ast: ObjectAssignmentPattern): ObjectAssignmentPattern = ast match {
    case ObjectAssignmentPattern0(params, span) => { job(ast); ast }
    case ObjectAssignmentPattern1(x1, params, span) =>
      job(ast); ObjectAssignmentPattern1(transform(x1), params, span)
    case ObjectAssignmentPattern2(x1, params, span) =>
      job(ast); ObjectAssignmentPattern2(transform(x1), params, span)
    case ObjectAssignmentPattern3(x1, x3, params, span) =>
      job(ast); ObjectAssignmentPattern3(transform(x1), transform[AssignmentRestProperty](x3, transform), params, span)
  }
  def transform(ast: ArrayAssignmentPattern): ArrayAssignmentPattern = ast match {
    case ArrayAssignmentPattern0(x1, x2, params, span) =>
      job(ast); ArrayAssignmentPattern0(transform[Elision](x1, transform), transform[AssignmentRestElement](x2, transform), params, span)
    case ArrayAssignmentPattern1(x1, params, span) =>
      job(ast); ArrayAssignmentPattern1(transform(x1), params, span)
    case ArrayAssignmentPattern2(x1, x3, x4, params, span) =>
      job(ast); ArrayAssignmentPattern2(transform(x1), transform[Elision](x3, transform), transform[AssignmentRestElement](x4, transform), params, span)
  }
  def transform(ast: AssignmentRestProperty): AssignmentRestProperty = ast match {
    case AssignmentRestProperty0(x1, params, span) =>
      job(ast); AssignmentRestProperty0(transform(x1), params, span)
  }
  def transform(ast: AssignmentPropertyList): AssignmentPropertyList = ast match {
    case AssignmentPropertyList0(x0, params, span) =>
      job(ast); AssignmentPropertyList0(transform(x0), params, span)
    case AssignmentPropertyList1(x0, x2, params, span) =>
      job(ast); AssignmentPropertyList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: AssignmentElementList): AssignmentElementList = ast match {
    case AssignmentElementList0(x0, params, span) =>
      job(ast); AssignmentElementList0(transform(x0), params, span)
    case AssignmentElementList1(x0, x2, params, span) =>
      job(ast); AssignmentElementList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: AssignmentElisionElement): AssignmentElisionElement = ast match {
    case AssignmentElisionElement0(x0, x1, params, span) =>
      job(ast); AssignmentElisionElement0(transform[Elision](x0, transform), transform(x1), params, span)
  }
  def transform(ast: AssignmentProperty): AssignmentProperty = ast match {
    case AssignmentProperty0(x0, x1, params, span) =>
      job(ast); AssignmentProperty0(transform(x0), transform[Initializer](x1, transform), params, span)
    case AssignmentProperty1(x0, x2, params, span) =>
      job(ast); AssignmentProperty1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: AssignmentElement): AssignmentElement = ast match {
    case AssignmentElement0(x0, x1, params, span) =>
      job(ast); AssignmentElement0(transform(x0), transform[Initializer](x1, transform), params, span)
  }
  def transform(ast: AssignmentRestElement): AssignmentRestElement = ast match {
    case AssignmentRestElement0(x1, params, span) =>
      job(ast); AssignmentRestElement0(transform(x1), params, span)
  }
  def transform(ast: DestructuringAssignmentTarget): DestructuringAssignmentTarget = ast match {
    case DestructuringAssignmentTarget0(x0, params, span) =>
      job(ast); DestructuringAssignmentTarget0(transform(x0), params, span)
  }
  def transform(ast: Expression): Expression = ast match {
    case Expression0(x0, params, span) =>
      job(ast); Expression0(transform(x0), params, span)
    case Expression1(x0, x2, params, span) =>
      job(ast); Expression1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: Statement): Statement = ast match {
    case Statement0(x0, params, span) =>
      job(ast); Statement0(transform(x0), params, span)
    case Statement1(x0, params, span) =>
      job(ast); Statement1(transform(x0), params, span)
    case Statement2(x0, params, span) =>
      job(ast); Statement2(transform(x0), params, span)
    case Statement3(x0, params, span) =>
      job(ast); Statement3(transform(x0), params, span)
    case Statement4(x0, params, span) =>
      job(ast); Statement4(transform(x0), params, span)
    case Statement5(x0, params, span) =>
      job(ast); Statement5(transform(x0), params, span)
    case Statement6(x0, params, span) =>
      job(ast); Statement6(transform(x0), params, span)
    case Statement7(x0, params, span) =>
      job(ast); Statement7(transform(x0), params, span)
    case Statement8(x0, params, span) =>
      job(ast); Statement8(transform(x0), params, span)
    case Statement9(x0, params, span) =>
      job(ast); Statement9(transform(x0), params, span)
    case Statement10(x0, params, span) =>
      job(ast); Statement10(transform(x0), params, span)
    case Statement11(x0, params, span) =>
      job(ast); Statement11(transform(x0), params, span)
    case Statement12(x0, params, span) =>
      job(ast); Statement12(transform(x0), params, span)
    case Statement13(x0, params, span) =>
      job(ast); Statement13(transform(x0), params, span)
  }
  def transform(ast: Declaration): Declaration = ast match {
    case Declaration0(x0, params, span) =>
      job(ast); Declaration0(transform(x0), params, span)
    case Declaration1(x0, params, span) =>
      job(ast); Declaration1(transform(x0), params, span)
    case Declaration2(x0, params, span) =>
      job(ast); Declaration2(transform(x0), params, span)
  }
  def transform(ast: HoistableDeclaration): HoistableDeclaration = ast match {
    case HoistableDeclaration0(x0, params, span) =>
      job(ast); HoistableDeclaration0(transform(x0), params, span)
    case HoistableDeclaration1(x0, params, span) =>
      job(ast); HoistableDeclaration1(transform(x0), params, span)
    case HoistableDeclaration2(x0, params, span) =>
      job(ast); HoistableDeclaration2(transform(x0), params, span)
    case HoistableDeclaration3(x0, params, span) =>
      job(ast); HoistableDeclaration3(transform(x0), params, span)
  }
  def transform(ast: BreakableStatement): BreakableStatement = ast match {
    case BreakableStatement0(x0, params, span) =>
      job(ast); BreakableStatement0(transform(x0), params, span)
    case BreakableStatement1(x0, params, span) =>
      job(ast); BreakableStatement1(transform(x0), params, span)
  }
  def transform(ast: BlockStatement): BlockStatement = ast match {
    case BlockStatement0(x0, params, span) =>
      job(ast); BlockStatement0(transform(x0), params, span)
  }
  def transform(ast: Block): Block = ast match {
    case Block0(x1, params, span) =>
      job(ast); Block0(transform[StatementList](x1, transform), params, span)
  }
  def transform(ast: StatementList): StatementList = ast match {
    case StatementList0(x0, params, span) =>
      job(ast); StatementList0(transform(x0), params, span)
    case StatementList1(x0, x1, params, span) =>
      job(ast); StatementList1(transform(x0), transform(x1), params, span)
  }
  def transform(ast: StatementListItem): StatementListItem = ast match {
    case StatementListItem0(x0, params, span) =>
      job(ast); StatementListItem0(transform(x0), params, span)
    case StatementListItem1(x0, params, span) =>
      job(ast); StatementListItem1(transform(x0), params, span)
  }
  def transform(ast: LexicalDeclaration): LexicalDeclaration = ast match {
    case LexicalDeclaration0(x0, x1, params, span) =>
      job(ast); LexicalDeclaration0(transform(x0), transform(x1), params, span)
  }
  def transform(ast: LetOrConst): LetOrConst = ast match {
    case LetOrConst0(params, span) => { job(ast); ast }
    case LetOrConst1(params, span) => { job(ast); ast }
  }
  def transform(ast: BindingList): BindingList = ast match {
    case BindingList0(x0, params, span) =>
      job(ast); BindingList0(transform(x0), params, span)
    case BindingList1(x0, x2, params, span) =>
      job(ast); BindingList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: LexicalBinding): LexicalBinding = ast match {
    case LexicalBinding0(x0, x1, params, span) =>
      job(ast); LexicalBinding0(transform(x0), transform[Initializer](x1, transform), params, span)
    case LexicalBinding1(x0, x1, params, span) =>
      job(ast); LexicalBinding1(transform(x0), transform(x1), params, span)
  }
  def transform(ast: VariableStatement): VariableStatement = ast match {
    case VariableStatement0(x1, params, span) =>
      job(ast); VariableStatement0(transform(x1), params, span)
  }
  def transform(ast: VariableDeclarationList): VariableDeclarationList = ast match {
    case VariableDeclarationList0(x0, params, span) =>
      job(ast); VariableDeclarationList0(transform(x0), params, span)
    case VariableDeclarationList1(x0, x2, params, span) =>
      job(ast); VariableDeclarationList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: VariableDeclaration): VariableDeclaration = ast match {
    case VariableDeclaration0(x0, x1, params, span) =>
      job(ast); VariableDeclaration0(transform(x0), transform[Initializer](x1, transform), params, span)
    case VariableDeclaration1(x0, x1, params, span) =>
      job(ast); VariableDeclaration1(transform(x0), transform(x1), params, span)
  }
  def transform(ast: BindingPattern): BindingPattern = ast match {
    case BindingPattern0(x0, params, span) =>
      job(ast); BindingPattern0(transform(x0), params, span)
    case BindingPattern1(x0, params, span) =>
      job(ast); BindingPattern1(transform(x0), params, span)
  }
  def transform(ast: ObjectBindingPattern): ObjectBindingPattern = ast match {
    case ObjectBindingPattern0(params, span) => { job(ast); ast }
    case ObjectBindingPattern1(x1, params, span) =>
      job(ast); ObjectBindingPattern1(transform(x1), params, span)
    case ObjectBindingPattern2(x1, params, span) =>
      job(ast); ObjectBindingPattern2(transform(x1), params, span)
    case ObjectBindingPattern3(x1, x3, params, span) =>
      job(ast); ObjectBindingPattern3(transform(x1), transform[BindingRestProperty](x3, transform), params, span)
  }
  def transform(ast: ArrayBindingPattern): ArrayBindingPattern = ast match {
    case ArrayBindingPattern0(x1, x2, params, span) =>
      job(ast); ArrayBindingPattern0(transform[Elision](x1, transform), transform[BindingRestElement](x2, transform), params, span)
    case ArrayBindingPattern1(x1, params, span) =>
      job(ast); ArrayBindingPattern1(transform(x1), params, span)
    case ArrayBindingPattern2(x1, x3, x4, params, span) =>
      job(ast); ArrayBindingPattern2(transform(x1), transform[Elision](x3, transform), transform[BindingRestElement](x4, transform), params, span)
  }
  def transform(ast: BindingRestProperty): BindingRestProperty = ast match {
    case BindingRestProperty0(x1, params, span) =>
      job(ast); BindingRestProperty0(transform(x1), params, span)
  }
  def transform(ast: BindingPropertyList): BindingPropertyList = ast match {
    case BindingPropertyList0(x0, params, span) =>
      job(ast); BindingPropertyList0(transform(x0), params, span)
    case BindingPropertyList1(x0, x2, params, span) =>
      job(ast); BindingPropertyList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: BindingElementList): BindingElementList = ast match {
    case BindingElementList0(x0, params, span) =>
      job(ast); BindingElementList0(transform(x0), params, span)
    case BindingElementList1(x0, x2, params, span) =>
      job(ast); BindingElementList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: BindingElisionElement): BindingElisionElement = ast match {
    case BindingElisionElement0(x0, x1, params, span) =>
      job(ast); BindingElisionElement0(transform[Elision](x0, transform), transform(x1), params, span)
  }
  def transform(ast: BindingProperty): BindingProperty = ast match {
    case BindingProperty0(x0, params, span) =>
      job(ast); BindingProperty0(transform(x0), params, span)
    case BindingProperty1(x0, x2, params, span) =>
      job(ast); BindingProperty1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: BindingElement): BindingElement = ast match {
    case BindingElement0(x0, params, span) =>
      job(ast); BindingElement0(transform(x0), params, span)
    case BindingElement1(x0, x1, params, span) =>
      job(ast); BindingElement1(transform(x0), transform[Initializer](x1, transform), params, span)
  }
  def transform(ast: SingleNameBinding): SingleNameBinding = ast match {
    case SingleNameBinding0(x0, x1, params, span) =>
      job(ast); SingleNameBinding0(transform(x0), transform[Initializer](x1, transform), params, span)
  }
  def transform(ast: BindingRestElement): BindingRestElement = ast match {
    case BindingRestElement0(x1, params, span) =>
      job(ast); BindingRestElement0(transform(x1), params, span)
    case BindingRestElement1(x1, params, span) =>
      job(ast); BindingRestElement1(transform(x1), params, span)
  }
  def transform(ast: EmptyStatement): EmptyStatement = ast match {
    case EmptyStatement0(params, span) => { job(ast); ast }
  }
  def transform(ast: ExpressionStatement): ExpressionStatement = ast match {
    case ExpressionStatement0(x1, params, span) =>
      job(ast); ExpressionStatement0(transform(x1), params, span)
  }
  def transform(ast: IfStatement): IfStatement = ast match {
    case IfStatement0(x2, x4, x6, params, span) =>
      job(ast); IfStatement0(transform(x2), transform(x4), transform(x6), params, span)
    case IfStatement1(x2, x4, params, span) =>
      job(ast); IfStatement1(transform(x2), transform(x4), params, span)
  }
  def transform(ast: IterationStatement): IterationStatement = ast match {
    case IterationStatement0(x0, params, span) =>
      job(ast); IterationStatement0(transform(x0), params, span)
    case IterationStatement1(x0, params, span) =>
      job(ast); IterationStatement1(transform(x0), params, span)
    case IterationStatement2(x0, params, span) =>
      job(ast); IterationStatement2(transform(x0), params, span)
    case IterationStatement3(x0, params, span) =>
      job(ast); IterationStatement3(transform(x0), params, span)
  }
  def transform(ast: DoWhileStatement): DoWhileStatement = ast match {
    case DoWhileStatement0(x1, x4, params, span) =>
      job(ast); DoWhileStatement0(transform(x1), transform(x4), params, span)
  }
  def transform(ast: WhileStatement): WhileStatement = ast match {
    case WhileStatement0(x2, x4, params, span) =>
      job(ast); WhileStatement0(transform(x2), transform(x4), params, span)
  }
  def transform(ast: ForStatement): ForStatement = ast match {
    case ForStatement0(x3, x5, x7, x9, params, span) =>
      job(ast); ForStatement0(transform[Expression](x3, transform), transform[Expression](x5, transform), transform[Expression](x7, transform), transform(x9), params, span)
    case ForStatement1(x3, x5, x7, x9, params, span) =>
      job(ast); ForStatement1(transform(x3), transform[Expression](x5, transform), transform[Expression](x7, transform), transform(x9), params, span)
    case ForStatement2(x2, x3, x5, x7, params, span) =>
      job(ast); ForStatement2(transform(x2), transform[Expression](x3, transform), transform[Expression](x5, transform), transform(x7), params, span)
  }
  def transform(ast: ForInOfStatement): ForInOfStatement = ast match {
    case ForInOfStatement0(x3, x5, x7, params, span) =>
      job(ast); ForInOfStatement0(transform(x3), transform(x5), transform(x7), params, span)
    case ForInOfStatement1(x3, x5, x7, params, span) =>
      job(ast); ForInOfStatement1(transform(x3), transform(x5), transform(x7), params, span)
    case ForInOfStatement2(x2, x4, x6, params, span) =>
      job(ast); ForInOfStatement2(transform(x2), transform(x4), transform(x6), params, span)
    case ForInOfStatement3(x3, x5, x7, params, span) =>
      job(ast); ForInOfStatement3(transform(x3), transform(x5), transform(x7), params, span)
    case ForInOfStatement4(x3, x5, x7, params, span) =>
      job(ast); ForInOfStatement4(transform(x3), transform(x5), transform(x7), params, span)
    case ForInOfStatement5(x2, x4, x6, params, span) =>
      job(ast); ForInOfStatement5(transform(x2), transform(x4), transform(x6), params, span)
    case ForInOfStatement6(x4, x6, x8, params, span) =>
      job(ast); ForInOfStatement6(transform(x4), transform(x6), transform(x8), params, span)
    case ForInOfStatement7(x4, x6, x8, params, span) =>
      job(ast); ForInOfStatement7(transform(x4), transform(x6), transform(x8), params, span)
    case ForInOfStatement8(x3, x5, x7, params, span) =>
      job(ast); ForInOfStatement8(transform(x3), transform(x5), transform(x7), params, span)
  }
  def transform(ast: ForDeclaration): ForDeclaration = ast match {
    case ForDeclaration0(x0, x1, params, span) =>
      job(ast); ForDeclaration0(transform(x0), transform(x1), params, span)
  }
  def transform(ast: ForBinding): ForBinding = ast match {
    case ForBinding0(x0, params, span) =>
      job(ast); ForBinding0(transform(x0), params, span)
    case ForBinding1(x0, params, span) =>
      job(ast); ForBinding1(transform(x0), params, span)
  }
  def transform(ast: ContinueStatement): ContinueStatement = ast match {
    case ContinueStatement0(params, span) => { job(ast); ast }
    case ContinueStatement1(x2, params, span) =>
      job(ast); ContinueStatement1(transform(x2), params, span)
  }
  def transform(ast: BreakStatement): BreakStatement = ast match {
    case BreakStatement0(params, span) => { job(ast); ast }
    case BreakStatement1(x2, params, span) =>
      job(ast); BreakStatement1(transform(x2), params, span)
  }
  def transform(ast: ReturnStatement): ReturnStatement = ast match {
    case ReturnStatement0(params, span) => { job(ast); ast }
    case ReturnStatement1(x2, params, span) =>
      job(ast); ReturnStatement1(transform(x2), params, span)
  }
  def transform(ast: WithStatement): WithStatement = ast match {
    case WithStatement0(x2, x4, params, span) =>
      job(ast); WithStatement0(transform(x2), transform(x4), params, span)
  }
  def transform(ast: SwitchStatement): SwitchStatement = ast match {
    case SwitchStatement0(x2, x4, params, span) =>
      job(ast); SwitchStatement0(transform(x2), transform(x4), params, span)
  }
  def transform(ast: CaseBlock): CaseBlock = ast match {
    case CaseBlock0(x1, params, span) =>
      job(ast); CaseBlock0(transform[CaseClauses](x1, transform), params, span)
    case CaseBlock1(x1, x2, x3, params, span) =>
      job(ast); CaseBlock1(transform[CaseClauses](x1, transform), transform(x2), transform[CaseClauses](x3, transform), params, span)
  }
  def transform(ast: CaseClauses): CaseClauses = ast match {
    case CaseClauses0(x0, params, span) =>
      job(ast); CaseClauses0(transform(x0), params, span)
    case CaseClauses1(x0, x1, params, span) =>
      job(ast); CaseClauses1(transform(x0), transform(x1), params, span)
  }
  def transform(ast: CaseClause): CaseClause = ast match {
    case CaseClause0(x1, x3, params, span) =>
      job(ast); CaseClause0(transform(x1), transform[StatementList](x3, transform), params, span)
  }
  def transform(ast: DefaultClause): DefaultClause = ast match {
    case DefaultClause0(x2, params, span) =>
      job(ast); DefaultClause0(transform[StatementList](x2, transform), params, span)
  }
  def transform(ast: LabelledStatement): LabelledStatement = ast match {
    case LabelledStatement0(x0, x2, params, span) =>
      job(ast); LabelledStatement0(transform(x0), transform(x2), params, span)
  }
  def transform(ast: LabelledItem): LabelledItem = ast match {
    case LabelledItem0(x0, params, span) =>
      job(ast); LabelledItem0(transform(x0), params, span)
    case LabelledItem1(x0, params, span) =>
      job(ast); LabelledItem1(transform(x0), params, span)
  }
  def transform(ast: ThrowStatement): ThrowStatement = ast match {
    case ThrowStatement0(x2, params, span) =>
      job(ast); ThrowStatement0(transform(x2), params, span)
  }
  def transform(ast: TryStatement): TryStatement = ast match {
    case TryStatement0(x1, x2, params, span) =>
      job(ast); TryStatement0(transform(x1), transform(x2), params, span)
    case TryStatement1(x1, x2, params, span) =>
      job(ast); TryStatement1(transform(x1), transform(x2), params, span)
    case TryStatement2(x1, x2, x3, params, span) =>
      job(ast); TryStatement2(transform(x1), transform(x2), transform(x3), params, span)
  }
  def transform(ast: Catch): Catch = ast match {
    case Catch0(x2, x4, params, span) =>
      job(ast); Catch0(transform(x2), transform(x4), params, span)
    case Catch1(x1, params, span) =>
      job(ast); Catch1(transform(x1), params, span)
  }
  def transform(ast: Finally): Finally = ast match {
    case Finally0(x1, params, span) =>
      job(ast); Finally0(transform(x1), params, span)
  }
  def transform(ast: CatchParameter): CatchParameter = ast match {
    case CatchParameter0(x0, params, span) =>
      job(ast); CatchParameter0(transform(x0), params, span)
    case CatchParameter1(x0, params, span) =>
      job(ast); CatchParameter1(transform(x0), params, span)
  }
  def transform(ast: DebuggerStatement): DebuggerStatement = ast match {
    case DebuggerStatement0(params, span) => { job(ast); ast }
  }
  def transform(ast: UniqueFormalParameters): UniqueFormalParameters = ast match {
    case UniqueFormalParameters0(x0, params, span) =>
      job(ast); UniqueFormalParameters0(transform(x0), params, span)
  }
  def transform(ast: FormalParameters): FormalParameters = ast match {
    case FormalParameters0(params, span) => { job(ast); ast }
    case FormalParameters1(x0, params, span) =>
      job(ast); FormalParameters1(transform(x0), params, span)
    case FormalParameters2(x0, params, span) =>
      job(ast); FormalParameters2(transform(x0), params, span)
    case FormalParameters3(x0, params, span) =>
      job(ast); FormalParameters3(transform(x0), params, span)
    case FormalParameters4(x0, x2, params, span) =>
      job(ast); FormalParameters4(transform(x0), transform(x2), params, span)
  }
  def transform(ast: FormalParameterList): FormalParameterList = ast match {
    case FormalParameterList0(x0, params, span) =>
      job(ast); FormalParameterList0(transform(x0), params, span)
    case FormalParameterList1(x0, x2, params, span) =>
      job(ast); FormalParameterList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: FunctionRestParameter): FunctionRestParameter = ast match {
    case FunctionRestParameter0(x0, params, span) =>
      job(ast); FunctionRestParameter0(transform(x0), params, span)
  }
  def transform(ast: FormalParameter): FormalParameter = ast match {
    case FormalParameter0(x0, params, span) =>
      job(ast); FormalParameter0(transform(x0), params, span)
  }
  def transform(ast: FunctionDeclaration): FunctionDeclaration = ast match {
    case FunctionDeclaration0(x1, x3, x6, params, span) =>
      job(ast); FunctionDeclaration0(transform(x1), transform(x3), transform(x6), params, span)
    case FunctionDeclaration1(x2, x5, params, span) =>
      job(ast); FunctionDeclaration1(transform(x2), transform(x5), params, span)
  }
  def transform(ast: FunctionExpression): FunctionExpression = ast match {
    case FunctionExpression0(x1, x3, x6, params, span) =>
      job(ast); FunctionExpression0(transform[BindingIdentifier](x1, transform), transform(x3), transform(x6), params, span)
  }
  def transform(ast: FunctionBody): FunctionBody = ast match {
    case FunctionBody0(x0, params, span) =>
      job(ast); FunctionBody0(transform(x0), params, span)
  }
  def transform(ast: FunctionStatementList): FunctionStatementList = ast match {
    case FunctionStatementList0(x0, params, span) =>
      job(ast); FunctionStatementList0(transform[StatementList](x0, transform), params, span)
  }
  def transform(ast: ArrowFunction): ArrowFunction = ast match {
    case ArrowFunction0(x0, x3, params, span) =>
      job(ast); ArrowFunction0(transform(x0), transform(x3), params, span)
  }
  def transform(ast: ArrowParameters): ArrowParameters = ast match {
    case ArrowParameters0(x0, params, span) =>
      job(ast); ArrowParameters0(transform(x0), params, span)
    case ArrowParameters1(x0, params, span) =>
      job(ast); ArrowParameters1(transform(x0), params, span)
  }
  def transform(ast: ConciseBody): ConciseBody = ast match {
    case ConciseBody0(x1, params, span) =>
      job(ast); ConciseBody0(transform(x1), params, span)
    case ConciseBody1(x1, params, span) =>
      job(ast); ConciseBody1(transform(x1), params, span)
  }
  def transform(ast: ExpressionBody): ExpressionBody = ast match {
    case ExpressionBody0(x0, params, span) =>
      job(ast); ExpressionBody0(transform(x0), params, span)
  }
  def transform(ast: ArrowFormalParameters): ArrowFormalParameters = ast match {
    case ArrowFormalParameters0(x1, params, span) =>
      job(ast); ArrowFormalParameters0(transform(x1), params, span)
  }
  def transform(ast: MethodDefinition): MethodDefinition = ast match {
    case MethodDefinition0(x0, x2, x5, params, span) =>
      job(ast); MethodDefinition0(transform(x0), transform(x2), transform(x5), params, span)
    case MethodDefinition1(x0, params, span) =>
      job(ast); MethodDefinition1(transform(x0), params, span)
    case MethodDefinition2(x0, params, span) =>
      job(ast); MethodDefinition2(transform(x0), params, span)
    case MethodDefinition3(x0, params, span) =>
      job(ast); MethodDefinition3(transform(x0), params, span)
    case MethodDefinition4(x1, x5, params, span) =>
      job(ast); MethodDefinition4(transform(x1), transform(x5), params, span)
    case MethodDefinition5(x1, x3, x6, params, span) =>
      job(ast); MethodDefinition5(transform(x1), transform(x3), transform(x6), params, span)
  }
  def transform(ast: PropertySetParameterList): PropertySetParameterList = ast match {
    case PropertySetParameterList0(x0, params, span) =>
      job(ast); PropertySetParameterList0(transform(x0), params, span)
  }
  def transform(ast: GeneratorMethod): GeneratorMethod = ast match {
    case GeneratorMethod0(x1, x3, x6, params, span) =>
      job(ast); GeneratorMethod0(transform(x1), transform(x3), transform(x6), params, span)
  }
  def transform(ast: GeneratorDeclaration): GeneratorDeclaration = ast match {
    case GeneratorDeclaration0(x2, x4, x7, params, span) =>
      job(ast); GeneratorDeclaration0(transform(x2), transform(x4), transform(x7), params, span)
    case GeneratorDeclaration1(x3, x6, params, span) =>
      job(ast); GeneratorDeclaration1(transform(x3), transform(x6), params, span)
  }
  def transform(ast: GeneratorExpression): GeneratorExpression = ast match {
    case GeneratorExpression0(x2, x4, x7, params, span) =>
      job(ast); GeneratorExpression0(transform[BindingIdentifier](x2, transform), transform(x4), transform(x7), params, span)
  }
  def transform(ast: GeneratorBody): GeneratorBody = ast match {
    case GeneratorBody0(x0, params, span) =>
      job(ast); GeneratorBody0(transform(x0), params, span)
  }
  def transform(ast: YieldExpression): YieldExpression = ast match {
    case YieldExpression0(params, span) => { job(ast); ast }
    case YieldExpression1(x2, params, span) =>
      job(ast); YieldExpression1(transform(x2), params, span)
    case YieldExpression2(x3, params, span) =>
      job(ast); YieldExpression2(transform(x3), params, span)
  }
  def transform(ast: AsyncGeneratorMethod): AsyncGeneratorMethod = ast match {
    case AsyncGeneratorMethod0(x3, x5, x8, params, span) =>
      job(ast); AsyncGeneratorMethod0(transform(x3), transform(x5), transform(x8), params, span)
  }
  def transform(ast: AsyncGeneratorDeclaration): AsyncGeneratorDeclaration = ast match {
    case AsyncGeneratorDeclaration0(x4, x6, x9, params, span) =>
      job(ast); AsyncGeneratorDeclaration0(transform(x4), transform(x6), transform(x9), params, span)
    case AsyncGeneratorDeclaration1(x5, x8, params, span) =>
      job(ast); AsyncGeneratorDeclaration1(transform(x5), transform(x8), params, span)
  }
  def transform(ast: AsyncGeneratorExpression): AsyncGeneratorExpression = ast match {
    case AsyncGeneratorExpression0(x4, x6, x9, params, span) =>
      job(ast); AsyncGeneratorExpression0(transform[BindingIdentifier](x4, transform), transform(x6), transform(x9), params, span)
  }
  def transform(ast: AsyncGeneratorBody): AsyncGeneratorBody = ast match {
    case AsyncGeneratorBody0(x0, params, span) =>
      job(ast); AsyncGeneratorBody0(transform(x0), params, span)
  }
  def transform(ast: ClassDeclaration): ClassDeclaration = ast match {
    case ClassDeclaration0(x1, x2, params, span) =>
      job(ast); ClassDeclaration0(transform(x1), transform(x2), params, span)
    case ClassDeclaration1(x1, params, span) =>
      job(ast); ClassDeclaration1(transform(x1), params, span)
  }
  def transform(ast: ClassExpression): ClassExpression = ast match {
    case ClassExpression0(x1, x2, params, span) =>
      job(ast); ClassExpression0(transform[BindingIdentifier](x1, transform), transform(x2), params, span)
  }
  def transform(ast: ClassTail): ClassTail = ast match {
    case ClassTail0(x0, x2, params, span) =>
      job(ast); ClassTail0(transform[ClassHeritage](x0, transform), transform[ClassBody](x2, transform), params, span)
  }
  def transform(ast: ClassHeritage): ClassHeritage = ast match {
    case ClassHeritage0(x1, params, span) =>
      job(ast); ClassHeritage0(transform(x1), params, span)
  }
  def transform(ast: ClassBody): ClassBody = ast match {
    case ClassBody0(x0, params, span) =>
      job(ast); ClassBody0(transform(x0), params, span)
  }
  def transform(ast: ClassElementList): ClassElementList = ast match {
    case ClassElementList0(x0, params, span) =>
      job(ast); ClassElementList0(transform(x0), params, span)
    case ClassElementList1(x0, x1, params, span) =>
      job(ast); ClassElementList1(transform(x0), transform(x1), params, span)
  }
  def transform(ast: ClassElement): ClassElement = ast match {
    case ClassElement0(x0, params, span) =>
      job(ast); ClassElement0(transform(x0), params, span)
    case ClassElement1(x1, params, span) =>
      job(ast); ClassElement1(transform(x1), params, span)
    case ClassElement2(params, span) => { job(ast); ast }
  }
  def transform(ast: AsyncFunctionDeclaration): AsyncFunctionDeclaration = ast match {
    case AsyncFunctionDeclaration0(x3, x5, x8, params, span) =>
      job(ast); AsyncFunctionDeclaration0(transform(x3), transform(x5), transform(x8), params, span)
    case AsyncFunctionDeclaration1(x4, x7, params, span) =>
      job(ast); AsyncFunctionDeclaration1(transform(x4), transform(x7), params, span)
  }
  def transform(ast: AsyncFunctionExpression): AsyncFunctionExpression = ast match {
    case AsyncFunctionExpression0(x3, x5, x8, params, span) =>
      job(ast); AsyncFunctionExpression0(transform[BindingIdentifier](x3, transform), transform(x5), transform(x8), params, span)
  }
  def transform(ast: AsyncMethod): AsyncMethod = ast match {
    case AsyncMethod0(x2, x4, x7, params, span) =>
      job(ast); AsyncMethod0(transform(x2), transform(x4), transform(x7), params, span)
  }
  def transform(ast: AsyncFunctionBody): AsyncFunctionBody = ast match {
    case AsyncFunctionBody0(x0, params, span) =>
      job(ast); AsyncFunctionBody0(transform(x0), params, span)
  }
  def transform(ast: AwaitExpression): AwaitExpression = ast match {
    case AwaitExpression0(x1, params, span) =>
      job(ast); AwaitExpression0(transform(x1), params, span)
  }
  def transform(ast: AsyncArrowFunction): AsyncArrowFunction = ast match {
    case AsyncArrowFunction0(x2, x5, params, span) =>
      job(ast); AsyncArrowFunction0(transform(x2), transform(x5), params, span)
    case AsyncArrowFunction1(x0, x3, params, span) =>
      job(ast); AsyncArrowFunction1(transform(x0), transform(x3), params, span)
  }
  def transform(ast: AsyncConciseBody): AsyncConciseBody = ast match {
    case AsyncConciseBody0(x1, params, span) =>
      job(ast); AsyncConciseBody0(transform(x1), params, span)
    case AsyncConciseBody1(x1, params, span) =>
      job(ast); AsyncConciseBody1(transform(x1), params, span)
  }
  def transform(ast: AsyncArrowBindingIdentifier): AsyncArrowBindingIdentifier = ast match {
    case AsyncArrowBindingIdentifier0(x0, params, span) =>
      job(ast); AsyncArrowBindingIdentifier0(transform(x0), params, span)
  }
  def transform(ast: CoverCallExpressionAndAsyncArrowHead): CoverCallExpressionAndAsyncArrowHead = ast match {
    case CoverCallExpressionAndAsyncArrowHead0(x0, x1, params, span) =>
      job(ast); CoverCallExpressionAndAsyncArrowHead0(transform(x0), transform(x1), params, span)
  }
  def transform(ast: AsyncArrowHead): AsyncArrowHead = ast match {
    case AsyncArrowHead0(x2, params, span) =>
      job(ast); AsyncArrowHead0(transform(x2), params, span)
  }
  def transform(ast: Script): Script = ast match {
    case Script0(x0, params, span) =>
      job(ast); Script0(transform[ScriptBody](x0, transform), params, span)
  }
  def transform(ast: ScriptBody): ScriptBody = ast match {
    case ScriptBody0(x0, params, span) =>
      job(ast); ScriptBody0(transform(x0), params, span)
  }
  def transform(ast: Module): Module = ast match {
    case Module0(x0, params, span) =>
      job(ast); Module0(transform[ModuleBody](x0, transform), params, span)
  }
  def transform(ast: ModuleBody): ModuleBody = ast match {
    case ModuleBody0(x0, params, span) =>
      job(ast); ModuleBody0(transform(x0), params, span)
  }
  def transform(ast: ModuleItemList): ModuleItemList = ast match {
    case ModuleItemList0(x0, params, span) =>
      job(ast); ModuleItemList0(transform(x0), params, span)
    case ModuleItemList1(x0, x1, params, span) =>
      job(ast); ModuleItemList1(transform(x0), transform(x1), params, span)
  }
  def transform(ast: ModuleItem): ModuleItem = ast match {
    case ModuleItem0(x0, params, span) =>
      job(ast); ModuleItem0(transform(x0), params, span)
    case ModuleItem1(x0, params, span) =>
      job(ast); ModuleItem1(transform(x0), params, span)
    case ModuleItem2(x0, params, span) =>
      job(ast); ModuleItem2(transform(x0), params, span)
  }
  def transform(ast: ImportDeclaration): ImportDeclaration = ast match {
    case ImportDeclaration0(x1, x2, params, span) =>
      job(ast); ImportDeclaration0(transform(x1), transform(x2), params, span)
    case ImportDeclaration1(x1, params, span) =>
      job(ast); ImportDeclaration1(transform(x1), params, span)
  }
  def transform(ast: ImportClause): ImportClause = ast match {
    case ImportClause0(x0, params, span) =>
      job(ast); ImportClause0(transform(x0), params, span)
    case ImportClause1(x0, params, span) =>
      job(ast); ImportClause1(transform(x0), params, span)
    case ImportClause2(x0, params, span) =>
      job(ast); ImportClause2(transform(x0), params, span)
    case ImportClause3(x0, x2, params, span) =>
      job(ast); ImportClause3(transform(x0), transform(x2), params, span)
    case ImportClause4(x0, x2, params, span) =>
      job(ast); ImportClause4(transform(x0), transform(x2), params, span)
  }
  def transform(ast: ImportedDefaultBinding): ImportedDefaultBinding = ast match {
    case ImportedDefaultBinding0(x0, params, span) =>
      job(ast); ImportedDefaultBinding0(transform(x0), params, span)
  }
  def transform(ast: NameSpaceImport): NameSpaceImport = ast match {
    case NameSpaceImport0(x2, params, span) =>
      job(ast); NameSpaceImport0(transform(x2), params, span)
  }
  def transform(ast: NamedImports): NamedImports = ast match {
    case NamedImports0(params, span) => { job(ast); ast }
    case NamedImports1(x1, params, span) =>
      job(ast); NamedImports1(transform(x1), params, span)
    case NamedImports2(x1, params, span) =>
      job(ast); NamedImports2(transform(x1), params, span)
  }
  def transform(ast: FromClause): FromClause = ast match {
    case FromClause0(x1, params, span) =>
      job(ast); FromClause0(transform(x1), params, span)
  }
  def transform(ast: ImportsList): ImportsList = ast match {
    case ImportsList0(x0, params, span) =>
      job(ast); ImportsList0(transform(x0), params, span)
    case ImportsList1(x0, x2, params, span) =>
      job(ast); ImportsList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: ImportSpecifier): ImportSpecifier = ast match {
    case ImportSpecifier0(x0, params, span) =>
      job(ast); ImportSpecifier0(transform(x0), params, span)
    case ImportSpecifier1(x0, x2, params, span) =>
      job(ast); ImportSpecifier1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: ModuleSpecifier): ModuleSpecifier = ast match {
    case ModuleSpecifier0(x0, params, span) =>
      job(ast); ModuleSpecifier0(transform(x0), params, span)
  }
  def transform(ast: ImportedBinding): ImportedBinding = ast match {
    case ImportedBinding0(x0, params, span) =>
      job(ast); ImportedBinding0(transform(x0), params, span)
  }
  def transform(ast: ExportDeclaration): ExportDeclaration = ast match {
    case ExportDeclaration0(x1, x2, params, span) =>
      job(ast); ExportDeclaration0(transform(x1), transform(x2), params, span)
    case ExportDeclaration1(x1, params, span) =>
      job(ast); ExportDeclaration1(transform(x1), params, span)
    case ExportDeclaration2(x1, params, span) =>
      job(ast); ExportDeclaration2(transform(x1), params, span)
    case ExportDeclaration3(x1, params, span) =>
      job(ast); ExportDeclaration3(transform(x1), params, span)
    case ExportDeclaration4(x2, params, span) =>
      job(ast); ExportDeclaration4(transform(x2), params, span)
    case ExportDeclaration5(x2, params, span) =>
      job(ast); ExportDeclaration5(transform(x2), params, span)
    case ExportDeclaration6(x3, params, span) =>
      job(ast); ExportDeclaration6(transform(x3), params, span)
  }
  def transform(ast: ExportFromClause): ExportFromClause = ast match {
    case ExportFromClause0(params, span) => { job(ast); ast }
    case ExportFromClause1(x2, params, span) =>
      job(ast); ExportFromClause1(transform(x2), params, span)
    case ExportFromClause2(x0, params, span) =>
      job(ast); ExportFromClause2(transform(x0), params, span)
  }
  def transform(ast: NamedExports): NamedExports = ast match {
    case NamedExports0(params, span) => { job(ast); ast }
    case NamedExports1(x1, params, span) =>
      job(ast); NamedExports1(transform(x1), params, span)
    case NamedExports2(x1, params, span) =>
      job(ast); NamedExports2(transform(x1), params, span)
  }
  def transform(ast: ExportsList): ExportsList = ast match {
    case ExportsList0(x0, params, span) =>
      job(ast); ExportsList0(transform(x0), params, span)
    case ExportsList1(x0, x2, params, span) =>
      job(ast); ExportsList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: ExportSpecifier): ExportSpecifier = ast match {
    case ExportSpecifier0(x0, params, span) =>
      job(ast); ExportSpecifier0(transform(x0), params, span)
    case ExportSpecifier1(x0, x2, params, span) =>
      job(ast); ExportSpecifier1(transform(x0), transform(x2), params, span)
  }

  def transform(ast: AST): AST = ast match {
    case ast: IdentifierReference => transform(ast)
    case ast: BindingIdentifier => transform(ast)
    case ast: LabelIdentifier => transform(ast)
    case ast: Identifier => transform(ast)
    case ast: PrimaryExpression => transform(ast)
    case ast: CoverParenthesizedExpressionAndArrowParameterList => transform(ast)
    case ast: ParenthesizedExpression => transform(ast)
    case ast: Literal => transform(ast)
    case ast: ArrayLiteral => transform(ast)
    case ast: ElementList => transform(ast)
    case ast: Elision => transform(ast)
    case ast: SpreadElement => transform(ast)
    case ast: ObjectLiteral => transform(ast)
    case ast: PropertyDefinitionList => transform(ast)
    case ast: PropertyDefinition => transform(ast)
    case ast: PropertyName => transform(ast)
    case ast: LiteralPropertyName => transform(ast)
    case ast: ComputedPropertyName => transform(ast)
    case ast: CoverInitializedName => transform(ast)
    case ast: Initializer => transform(ast)
    case ast: TemplateLiteral => transform(ast)
    case ast: SubstitutionTemplate => transform(ast)
    case ast: TemplateSpans => transform(ast)
    case ast: TemplateMiddleList => transform(ast)
    case ast: MemberExpression => transform(ast)
    case ast: SuperProperty => transform(ast)
    case ast: MetaProperty => transform(ast)
    case ast: NewTarget => transform(ast)
    case ast: ImportMeta => transform(ast)
    case ast: NewExpression => transform(ast)
    case ast: CallExpression => transform(ast)
    case ast: SuperCall => transform(ast)
    case ast: ImportCall => transform(ast)
    case ast: Arguments => transform(ast)
    case ast: ArgumentList => transform(ast)
    case ast: OptionalExpression => transform(ast)
    case ast: OptionalChain => transform(ast)
    case ast: LeftHandSideExpression => transform(ast)
    case ast: CallMemberExpression => transform(ast)
    case ast: UpdateExpression => transform(ast)
    case ast: UnaryExpression => transform(ast)
    case ast: ExponentiationExpression => transform(ast)
    case ast: MultiplicativeExpression => transform(ast)
    case ast: MultiplicativeOperator => transform(ast)
    case ast: AdditiveExpression => transform(ast)
    case ast: ShiftExpression => transform(ast)
    case ast: RelationalExpression => transform(ast)
    case ast: EqualityExpression => transform(ast)
    case ast: BitwiseANDExpression => transform(ast)
    case ast: BitwiseXORExpression => transform(ast)
    case ast: BitwiseORExpression => transform(ast)
    case ast: LogicalANDExpression => transform(ast)
    case ast: LogicalORExpression => transform(ast)
    case ast: CoalesceExpression => transform(ast)
    case ast: CoalesceExpressionHead => transform(ast)
    case ast: ShortCircuitExpression => transform(ast)
    case ast: ConditionalExpression => transform(ast)
    case ast: AssignmentExpression => transform(ast)
    case ast: AssignmentOperator => transform(ast)
    case ast: AssignmentPattern => transform(ast)
    case ast: ObjectAssignmentPattern => transform(ast)
    case ast: ArrayAssignmentPattern => transform(ast)
    case ast: AssignmentRestProperty => transform(ast)
    case ast: AssignmentPropertyList => transform(ast)
    case ast: AssignmentElementList => transform(ast)
    case ast: AssignmentElisionElement => transform(ast)
    case ast: AssignmentProperty => transform(ast)
    case ast: AssignmentElement => transform(ast)
    case ast: AssignmentRestElement => transform(ast)
    case ast: DestructuringAssignmentTarget => transform(ast)
    case ast: Expression => transform(ast)
    case ast: Statement => transform(ast)
    case ast: Declaration => transform(ast)
    case ast: HoistableDeclaration => transform(ast)
    case ast: BreakableStatement => transform(ast)
    case ast: BlockStatement => transform(ast)
    case ast: Block => transform(ast)
    case ast: StatementList => transform(ast)
    case ast: StatementListItem => transform(ast)
    case ast: LexicalDeclaration => transform(ast)
    case ast: LetOrConst => transform(ast)
    case ast: BindingList => transform(ast)
    case ast: LexicalBinding => transform(ast)
    case ast: VariableStatement => transform(ast)
    case ast: VariableDeclarationList => transform(ast)
    case ast: VariableDeclaration => transform(ast)
    case ast: BindingPattern => transform(ast)
    case ast: ObjectBindingPattern => transform(ast)
    case ast: ArrayBindingPattern => transform(ast)
    case ast: BindingRestProperty => transform(ast)
    case ast: BindingPropertyList => transform(ast)
    case ast: BindingElementList => transform(ast)
    case ast: BindingElisionElement => transform(ast)
    case ast: BindingProperty => transform(ast)
    case ast: BindingElement => transform(ast)
    case ast: SingleNameBinding => transform(ast)
    case ast: BindingRestElement => transform(ast)
    case ast: EmptyStatement => transform(ast)
    case ast: ExpressionStatement => transform(ast)
    case ast: IfStatement => transform(ast)
    case ast: IterationStatement => transform(ast)
    case ast: DoWhileStatement => transform(ast)
    case ast: WhileStatement => transform(ast)
    case ast: ForStatement => transform(ast)
    case ast: ForInOfStatement => transform(ast)
    case ast: ForDeclaration => transform(ast)
    case ast: ForBinding => transform(ast)
    case ast: ContinueStatement => transform(ast)
    case ast: BreakStatement => transform(ast)
    case ast: ReturnStatement => transform(ast)
    case ast: WithStatement => transform(ast)
    case ast: SwitchStatement => transform(ast)
    case ast: CaseBlock => transform(ast)
    case ast: CaseClauses => transform(ast)
    case ast: CaseClause => transform(ast)
    case ast: DefaultClause => transform(ast)
    case ast: LabelledStatement => transform(ast)
    case ast: LabelledItem => transform(ast)
    case ast: ThrowStatement => transform(ast)
    case ast: TryStatement => transform(ast)
    case ast: Catch => transform(ast)
    case ast: Finally => transform(ast)
    case ast: CatchParameter => transform(ast)
    case ast: DebuggerStatement => transform(ast)
    case ast: UniqueFormalParameters => transform(ast)
    case ast: FormalParameters => transform(ast)
    case ast: FormalParameterList => transform(ast)
    case ast: FunctionRestParameter => transform(ast)
    case ast: FormalParameter => transform(ast)
    case ast: FunctionDeclaration => transform(ast)
    case ast: FunctionExpression => transform(ast)
    case ast: FunctionBody => transform(ast)
    case ast: FunctionStatementList => transform(ast)
    case ast: ArrowFunction => transform(ast)
    case ast: ArrowParameters => transform(ast)
    case ast: ConciseBody => transform(ast)
    case ast: ExpressionBody => transform(ast)
    case ast: ArrowFormalParameters => transform(ast)
    case ast: MethodDefinition => transform(ast)
    case ast: PropertySetParameterList => transform(ast)
    case ast: GeneratorMethod => transform(ast)
    case ast: GeneratorDeclaration => transform(ast)
    case ast: GeneratorExpression => transform(ast)
    case ast: GeneratorBody => transform(ast)
    case ast: YieldExpression => transform(ast)
    case ast: AsyncGeneratorMethod => transform(ast)
    case ast: AsyncGeneratorDeclaration => transform(ast)
    case ast: AsyncGeneratorExpression => transform(ast)
    case ast: AsyncGeneratorBody => transform(ast)
    case ast: ClassDeclaration => transform(ast)
    case ast: ClassExpression => transform(ast)
    case ast: ClassTail => transform(ast)
    case ast: ClassHeritage => transform(ast)
    case ast: ClassBody => transform(ast)
    case ast: ClassElementList => transform(ast)
    case ast: ClassElement => transform(ast)
    case ast: AsyncFunctionDeclaration => transform(ast)
    case ast: AsyncFunctionExpression => transform(ast)
    case ast: AsyncMethod => transform(ast)
    case ast: AsyncFunctionBody => transform(ast)
    case ast: AwaitExpression => transform(ast)
    case ast: AsyncArrowFunction => transform(ast)
    case ast: AsyncConciseBody => transform(ast)
    case ast: AsyncArrowBindingIdentifier => transform(ast)
    case ast: CoverCallExpressionAndAsyncArrowHead => transform(ast)
    case ast: AsyncArrowHead => transform(ast)
    case ast: Script => transform(ast)
    case ast: ScriptBody => transform(ast)
    case ast: Module => transform(ast)
    case ast: ModuleBody => transform(ast)
    case ast: ModuleItemList => transform(ast)
    case ast: ModuleItem => transform(ast)
    case ast: ImportDeclaration => transform(ast)
    case ast: ImportClause => transform(ast)
    case ast: ImportedDefaultBinding => transform(ast)
    case ast: NameSpaceImport => transform(ast)
    case ast: NamedImports => transform(ast)
    case ast: FromClause => transform(ast)
    case ast: ImportsList => transform(ast)
    case ast: ImportSpecifier => transform(ast)
    case ast: ModuleSpecifier => transform(ast)
    case ast: ImportedBinding => transform(ast)
    case ast: ExportDeclaration => transform(ast)
    case ast: ExportFromClause => transform(ast)
    case ast: NamedExports => transform(ast)
    case ast: ExportsList => transform(ast)
    case ast: ExportSpecifier => transform(ast)
  }
}

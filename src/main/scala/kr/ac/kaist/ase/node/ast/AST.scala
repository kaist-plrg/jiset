package kr.ac.kaist.ase.node.ast

trait AST

trait IdentifierReference extends AST
case class IdentifierReference0(x0: Identifier) extends IdentifierReference {
  override def toString: String = {
    s"${x0}"
  }
}
case object IdentifierReference1 extends IdentifierReference {
  override def toString: String = {
    s"yield"
  }
}
case object IdentifierReference2 extends IdentifierReference {
  override def toString: String = {
    s"await"
  }
}
trait BindingIdentifier extends AST
case class BindingIdentifier0(x0: Identifier) extends BindingIdentifier {
  override def toString: String = {
    s"${x0}"
  }
}
case object BindingIdentifier1 extends BindingIdentifier {
  override def toString: String = {
    s"yield"
  }
}
case object BindingIdentifier2 extends BindingIdentifier {
  override def toString: String = {
    s"await"
  }
}
trait Identifier extends AST
case class Identifier0(x0: String) extends Identifier {
  override def toString: String = {
    s"${x0}"
  }
}
trait AsyncArrowBindingIdentifier extends AST
case class AsyncArrowBindingIdentifier0(x0: BindingIdentifier) extends AsyncArrowBindingIdentifier {
  override def toString: String = {
    s"${x0}"
  }
}
trait LabelIdentifier extends AST
case class LabelIdentifier0(x0: Identifier) extends LabelIdentifier {
  override def toString: String = {
    s"${x0}"
  }
}
case object LabelIdentifier1 extends LabelIdentifier {
  override def toString: String = {
    s"yield"
  }
}
case object LabelIdentifier2 extends LabelIdentifier {
  override def toString: String = {
    s"await"
  }
}
trait PrimaryExpression extends AST
case object PrimaryExpression0 extends PrimaryExpression {
  override def toString: String = {
    s"this"
  }
}
case class PrimaryExpression1(x0: IdentifierReference) extends PrimaryExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class PrimaryExpression2(x0: Literal) extends PrimaryExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class PrimaryExpression3(x0: ArrayLiteral) extends PrimaryExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class PrimaryExpression4(x0: ObjectLiteral) extends PrimaryExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class PrimaryExpression5(x0: FunctionExpression) extends PrimaryExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class PrimaryExpression6(x0: ClassExpression) extends PrimaryExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class PrimaryExpression7(x0: GeneratorExpression) extends PrimaryExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class PrimaryExpression8(x0: AsyncFunctionExpression) extends PrimaryExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class PrimaryExpression9(x0: AsyncGeneratorExpression) extends PrimaryExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class PrimaryExpression10(x0: String) extends PrimaryExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class PrimaryExpression11(x0: TemplateLiteral) extends PrimaryExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class PrimaryExpression12(x0: CoverParenthesizedExpressionAndArrowParameterList) extends PrimaryExpression {
  override def toString: String = {
    s"${x0}"
  }
}
trait CoverParenthesizedExpressionAndArrowParameterList extends AST
case class CoverParenthesizedExpressionAndArrowParameterList0(x1: Expression) extends CoverParenthesizedExpressionAndArrowParameterList {
  override def toString: String = {
    s"( ${x1} )"
  }
}
case class CoverParenthesizedExpressionAndArrowParameterList1(x1: Expression) extends CoverParenthesizedExpressionAndArrowParameterList {
  override def toString: String = {
    s"( ${x1} , )"
  }
}
case object CoverParenthesizedExpressionAndArrowParameterList2 extends CoverParenthesizedExpressionAndArrowParameterList {
  override def toString: String = {
    s"( )"
  }
}
case class CoverParenthesizedExpressionAndArrowParameterList3(x2: BindingIdentifier) extends CoverParenthesizedExpressionAndArrowParameterList {
  override def toString: String = {
    s"( ... ${x2} )"
  }
}
case class CoverParenthesizedExpressionAndArrowParameterList4(x2: BindingPattern) extends CoverParenthesizedExpressionAndArrowParameterList {
  override def toString: String = {
    s"( ... ${x2} )"
  }
}
case class CoverParenthesizedExpressionAndArrowParameterList5(x1: Expression, x4: BindingIdentifier) extends CoverParenthesizedExpressionAndArrowParameterList {
  override def toString: String = {
    s"( ${x1} , ... ${x4} )"
  }
}
case class CoverParenthesizedExpressionAndArrowParameterList6(x1: Expression, x4: BindingPattern) extends CoverParenthesizedExpressionAndArrowParameterList {
  override def toString: String = {
    s"( ${x1} , ... ${x4} )"
  }
}
trait ParenthesizedExpression extends AST
case class ParenthesizedExpression0(x1: Expression) extends ParenthesizedExpression {
  override def toString: String = {
    s"( ${x1} )"
  }
}
trait Literal extends AST
case class Literal0(x0: String) extends Literal {
  override def toString: String = {
    s"${x0}"
  }
}
case class Literal1(x0: String) extends Literal {
  override def toString: String = {
    s"${x0}"
  }
}
case class Literal2(x0: String) extends Literal {
  override def toString: String = {
    s"${x0}"
  }
}
case class Literal3(x0: String) extends Literal {
  override def toString: String = {
    s"${x0}"
  }
}
trait ArrayLiteral extends AST
case class ArrayLiteral0(x1: Option[Elision]) extends ArrayLiteral {
  override def toString: String = {
    s"[ ${x1.getOrElse("")} ]"
  }
}
case class ArrayLiteral1(x1: ElementList) extends ArrayLiteral {
  override def toString: String = {
    s"[ ${x1} ]"
  }
}
case class ArrayLiteral2(x1: ElementList, x3: Option[Elision]) extends ArrayLiteral {
  override def toString: String = {
    s"[ ${x1} , ${x3.getOrElse("")} ]"
  }
}
trait ElementList extends AST
case class ElementList0(x0: Option[Elision], x1: AssignmentExpression) extends ElementList {
  override def toString: String = {
    s"${x0.getOrElse("")} ${x1}"
  }
}
case class ElementList1(x0: Option[Elision], x1: SpreadElement) extends ElementList {
  override def toString: String = {
    s"${x0.getOrElse("")} ${x1}"
  }
}
case class ElementList2(x0: ElementList, x2: Option[Elision], x3: AssignmentExpression) extends ElementList {
  override def toString: String = {
    s"${x0} , ${x2.getOrElse("")} ${x3}"
  }
}
case class ElementList3(x0: ElementList, x2: Option[Elision], x3: SpreadElement) extends ElementList {
  override def toString: String = {
    s"${x0} , ${x2.getOrElse("")} ${x3}"
  }
}
trait Elision extends AST
case object Elision0 extends Elision {
  override def toString: String = {
    s","
  }
}
case class Elision1(x0: Elision) extends Elision {
  override def toString: String = {
    s"${x0} ,"
  }
}
trait SpreadElement extends AST
case class SpreadElement0(x1: AssignmentExpression) extends SpreadElement {
  override def toString: String = {
    s"... ${x1}"
  }
}
trait ObjectLiteral extends AST
case object ObjectLiteral0 extends ObjectLiteral {
  override def toString: String = {
    s"{ }"
  }
}
case class ObjectLiteral1(x1: PropertyDefinitionList) extends ObjectLiteral {
  override def toString: String = {
    s"{ ${x1} }"
  }
}
case class ObjectLiteral2(x1: PropertyDefinitionList) extends ObjectLiteral {
  override def toString: String = {
    s"{ ${x1} , }"
  }
}
trait PropertyDefinitionList extends AST
case class PropertyDefinitionList0(x0: PropertyDefinition) extends PropertyDefinitionList {
  override def toString: String = {
    s"${x0}"
  }
}
case class PropertyDefinitionList1(x0: PropertyDefinitionList, x2: PropertyDefinition) extends PropertyDefinitionList {
  override def toString: String = {
    s"${x0} , ${x2}"
  }
}
trait PropertyDefinition extends AST
case class PropertyDefinition0(x0: IdentifierReference) extends PropertyDefinition {
  override def toString: String = {
    s"${x0}"
  }
}
case class PropertyDefinition1(x0: CoverInitializedName) extends PropertyDefinition {
  override def toString: String = {
    s"${x0}"
  }
}
case class PropertyDefinition2(x0: PropertyName, x2: AssignmentExpression) extends PropertyDefinition {
  override def toString: String = {
    s"${x0} : ${x2}"
  }
}
case class PropertyDefinition3(x0: MethodDefinition) extends PropertyDefinition {
  override def toString: String = {
    s"${x0}"
  }
}
case class PropertyDefinition4(x1: AssignmentExpression) extends PropertyDefinition {
  override def toString: String = {
    s"... ${x1}"
  }
}
trait PropertyName extends AST
case class PropertyName0(x0: LiteralPropertyName) extends PropertyName {
  override def toString: String = {
    s"${x0}"
  }
}
case class PropertyName1(x0: ComputedPropertyName) extends PropertyName {
  override def toString: String = {
    s"${x0}"
  }
}
trait LiteralPropertyName extends AST
case class LiteralPropertyName0(x0: String) extends LiteralPropertyName {
  override def toString: String = {
    s"${x0}"
  }
}
case class LiteralPropertyName1(x0: String) extends LiteralPropertyName {
  override def toString: String = {
    s"${x0}"
  }
}
case class LiteralPropertyName2(x0: String) extends LiteralPropertyName {
  override def toString: String = {
    s"${x0}"
  }
}
trait ComputedPropertyName extends AST
case class ComputedPropertyName0(x1: AssignmentExpression) extends ComputedPropertyName {
  override def toString: String = {
    s"[ ${x1} ]"
  }
}
trait CoverInitializedName extends AST
case class CoverInitializedName0(x0: IdentifierReference, x1: Initializer) extends CoverInitializedName {
  override def toString: String = {
    s"${x0} ${x1}"
  }
}
trait Initializer extends AST
case class Initializer0(x1: AssignmentExpression) extends Initializer {
  override def toString: String = {
    s"= ${x1}"
  }
}
trait TemplateLiteral extends AST
case class TemplateLiteral0(x0: String) extends TemplateLiteral {
  override def toString: String = {
    s"${x0}"
  }
}
case class TemplateLiteral1(x0: SubstitutionTemplate) extends TemplateLiteral {
  override def toString: String = {
    s"${x0}"
  }
}
trait TemplateSpans extends AST
case class TemplateSpans0(x0: String) extends TemplateSpans {
  override def toString: String = {
    s"${x0}"
  }
}
case class TemplateSpans1(x0: TemplateMiddleList, x1: String) extends TemplateSpans {
  override def toString: String = {
    s"${x0} ${x1}"
  }
}
trait TemplateMiddleList extends AST
case class TemplateMiddleList0(x0: String, x1: Expression) extends TemplateMiddleList {
  override def toString: String = {
    s"${x0} ${x1}"
  }
}
case class TemplateMiddleList1(x0: TemplateMiddleList, x1: String, x2: Expression) extends TemplateMiddleList {
  override def toString: String = {
    s"${x0} ${x1} ${x2}"
  }
}
trait MemberExpression extends AST
case class MemberExpression0(x0: PrimaryExpression) extends MemberExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class MemberExpression1(x0: MemberExpression, x2: Expression) extends MemberExpression {
  override def toString: String = {
    s"${x0} [ ${x2} ]"
  }
}
case class MemberExpression2(x0: MemberExpression, x2: String) extends MemberExpression {
  override def toString: String = {
    s"${x0} . ${x2}"
  }
}
case class MemberExpression3(x0: MemberExpression, x1: TemplateLiteral) extends MemberExpression {
  override def toString: String = {
    s"${x0} ${x1}"
  }
}
case class MemberExpression4(x0: SuperProperty) extends MemberExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class MemberExpression5(x0: MetaProperty) extends MemberExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class MemberExpression6(x1: MemberExpression, x2: Arguments) extends MemberExpression {
  override def toString: String = {
    s"new ${x1} ${x2}"
  }
}
trait SuperProperty extends AST
case class SuperProperty0(x2: Expression) extends SuperProperty {
  override def toString: String = {
    s"super [ ${x2} ]"
  }
}
case class SuperProperty1(x2: String) extends SuperProperty {
  override def toString: String = {
    s"super . ${x2}"
  }
}
trait MetaProperty extends AST
case class MetaProperty0(x0: NewTarget) extends MetaProperty {
  override def toString: String = {
    s"${x0}"
  }
}
trait NewTarget extends AST
case object NewTarget0 extends NewTarget {
  override def toString: String = {
    s"new . target"
  }
}
trait NewExpression extends AST
case class NewExpression0(x0: MemberExpression) extends NewExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class NewExpression1(x1: NewExpression) extends NewExpression {
  override def toString: String = {
    s"new ${x1}"
  }
}
trait CallExpression extends AST
case class CallExpression0(x0: CoverCallExpressionAndAsyncArrowHead) extends CallExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class CallExpression1(x0: SuperCall) extends CallExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class CallExpression2(x0: CallExpression, x1: Arguments) extends CallExpression {
  override def toString: String = {
    s"${x0} ${x1}"
  }
}
case class CallExpression3(x0: CallExpression, x2: Expression) extends CallExpression {
  override def toString: String = {
    s"${x0} [ ${x2} ]"
  }
}
case class CallExpression4(x0: CallExpression, x2: String) extends CallExpression {
  override def toString: String = {
    s"${x0} . ${x2}"
  }
}
case class CallExpression5(x0: CallExpression, x1: TemplateLiteral) extends CallExpression {
  override def toString: String = {
    s"${x0} ${x1}"
  }
}
trait CoverCallExpressionAndAsyncArrowHead extends AST
case class CoverCallExpressionAndAsyncArrowHead0(x0: MemberExpression, x1: Arguments) extends CoverCallExpressionAndAsyncArrowHead {
  override def toString: String = {
    s"${x0} ${x1}"
  }
}
trait CallMemberExpression extends AST
case class CallMemberExpression0(x0: MemberExpression, x1: Arguments) extends CallMemberExpression {
  override def toString: String = {
    s"${x0} ${x1}"
  }
}
trait SuperCall extends AST
case class SuperCall0(x1: Arguments) extends SuperCall {
  override def toString: String = {
    s"super ${x1}"
  }
}
trait Arguments extends AST
case object Arguments0 extends Arguments {
  override def toString: String = {
    s"( )"
  }
}
case class Arguments1(x1: ArgumentList) extends Arguments {
  override def toString: String = {
    s"( ${x1} )"
  }
}
case class Arguments2(x1: ArgumentList) extends Arguments {
  override def toString: String = {
    s"( ${x1} , )"
  }
}
trait ArgumentList extends AST
case class ArgumentList0(x0: AssignmentExpression) extends ArgumentList {
  override def toString: String = {
    s"${x0}"
  }
}
case class ArgumentList1(x1: AssignmentExpression) extends ArgumentList {
  override def toString: String = {
    s"... ${x1}"
  }
}
case class ArgumentList2(x0: ArgumentList, x2: AssignmentExpression) extends ArgumentList {
  override def toString: String = {
    s"${x0} , ${x2}"
  }
}
case class ArgumentList3(x0: ArgumentList, x3: AssignmentExpression) extends ArgumentList {
  override def toString: String = {
    s"${x0} , ... ${x3}"
  }
}
trait LeftHandSideExpression extends AST
case class LeftHandSideExpression0(x0: NewExpression) extends LeftHandSideExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class LeftHandSideExpression1(x0: CallExpression) extends LeftHandSideExpression {
  override def toString: String = {
    s"${x0}"
  }
}
trait UpdateExpression extends AST
case class UpdateExpression0(x0: LeftHandSideExpression) extends UpdateExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class UpdateExpression1(x0: LeftHandSideExpression) extends UpdateExpression {
  override def toString: String = {
    s"${x0} ++"
  }
}
case class UpdateExpression2(x0: LeftHandSideExpression) extends UpdateExpression {
  override def toString: String = {
    s"${x0} --"
  }
}
case class UpdateExpression3(x1: UnaryExpression) extends UpdateExpression {
  override def toString: String = {
    s"++ ${x1}"
  }
}
case class UpdateExpression4(x1: UnaryExpression) extends UpdateExpression {
  override def toString: String = {
    s"-- ${x1}"
  }
}
trait UnaryExpression extends AST
case class UnaryExpression0(x0: UpdateExpression) extends UnaryExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class UnaryExpression1(x1: UnaryExpression) extends UnaryExpression {
  override def toString: String = {
    s"delete ${x1}"
  }
}
case class UnaryExpression2(x1: UnaryExpression) extends UnaryExpression {
  override def toString: String = {
    s"void ${x1}"
  }
}
case class UnaryExpression3(x1: UnaryExpression) extends UnaryExpression {
  override def toString: String = {
    s"typeof ${x1}"
  }
}
case class UnaryExpression4(x1: UnaryExpression) extends UnaryExpression {
  override def toString: String = {
    s"+ ${x1}"
  }
}
case class UnaryExpression5(x1: UnaryExpression) extends UnaryExpression {
  override def toString: String = {
    s"- ${x1}"
  }
}
case class UnaryExpression6(x1: UnaryExpression) extends UnaryExpression {
  override def toString: String = {
    s"~ ${x1}"
  }
}
case class UnaryExpression7(x1: UnaryExpression) extends UnaryExpression {
  override def toString: String = {
    s"! ${x1}"
  }
}
case class UnaryExpression8(x0: AwaitExpression) extends UnaryExpression {
  override def toString: String = {
    s"${x0}"
  }
}
trait ExponentiationExpression extends AST
case class ExponentiationExpression0(x0: UnaryExpression) extends ExponentiationExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class ExponentiationExpression1(x0: UpdateExpression, x2: ExponentiationExpression) extends ExponentiationExpression {
  override def toString: String = {
    s"${x0} ** ${x2}"
  }
}
trait MultiplicativeExpression extends AST
case class MultiplicativeExpression0(x0: ExponentiationExpression) extends MultiplicativeExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class MultiplicativeExpression1(x0: MultiplicativeExpression, x1: MultiplicativeOperator, x2: ExponentiationExpression) extends MultiplicativeExpression {
  override def toString: String = {
    s"${x0} ${x1} ${x2}"
  }
}
trait MultiplicativeOperator extends AST
case object MultiplicativeOperator0 extends MultiplicativeOperator {
  override def toString: String = {
    s"*"
  }
}
case object MultiplicativeOperator1 extends MultiplicativeOperator {
  override def toString: String = {
    s"/"
  }
}
case object MultiplicativeOperator2 extends MultiplicativeOperator {
  override def toString: String = {
    s"%"
  }
}
trait AdditiveExpression extends AST
case class AdditiveExpression0(x0: MultiplicativeExpression) extends AdditiveExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class AdditiveExpression1(x0: AdditiveExpression, x2: MultiplicativeExpression) extends AdditiveExpression {
  override def toString: String = {
    s"${x0} + ${x2}"
  }
}
case class AdditiveExpression2(x0: AdditiveExpression, x2: MultiplicativeExpression) extends AdditiveExpression {
  override def toString: String = {
    s"${x0} - ${x2}"
  }
}
trait ShiftExpression extends AST
case class ShiftExpression0(x0: AdditiveExpression) extends ShiftExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class ShiftExpression1(x0: ShiftExpression, x2: AdditiveExpression) extends ShiftExpression {
  override def toString: String = {
    s"${x0} << ${x2}"
  }
}
case class ShiftExpression2(x0: ShiftExpression, x2: AdditiveExpression) extends ShiftExpression {
  override def toString: String = {
    s"${x0} >> ${x2}"
  }
}
case class ShiftExpression3(x0: ShiftExpression, x2: AdditiveExpression) extends ShiftExpression {
  override def toString: String = {
    s"${x0} >>> ${x2}"
  }
}
trait RelationalExpression extends AST
case class RelationalExpression0(x0: ShiftExpression) extends RelationalExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class RelationalExpression1(x0: RelationalExpression, x2: ShiftExpression) extends RelationalExpression {
  override def toString: String = {
    s"${x0} < ${x2}"
  }
}
case class RelationalExpression2(x0: RelationalExpression, x2: ShiftExpression) extends RelationalExpression {
  override def toString: String = {
    s"${x0} > ${x2}"
  }
}
case class RelationalExpression3(x0: RelationalExpression, x2: ShiftExpression) extends RelationalExpression {
  override def toString: String = {
    s"${x0} <= ${x2}"
  }
}
case class RelationalExpression4(x0: RelationalExpression, x2: ShiftExpression) extends RelationalExpression {
  override def toString: String = {
    s"${x0} >= ${x2}"
  }
}
case class RelationalExpression5(x0: RelationalExpression, x2: ShiftExpression) extends RelationalExpression {
  override def toString: String = {
    s"${x0} instanceof ${x2}"
  }
}
case class RelationalExpression6(x0: RelationalExpression, x2: ShiftExpression) extends RelationalExpression {
  override def toString: String = {
    s"${x0} in ${x2}"
  }
}
trait EqualityExpression extends AST
case class EqualityExpression0(x0: RelationalExpression) extends EqualityExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class EqualityExpression1(x0: EqualityExpression, x2: RelationalExpression) extends EqualityExpression {
  override def toString: String = {
    s"${x0} == ${x2}"
  }
}
case class EqualityExpression2(x0: EqualityExpression, x2: RelationalExpression) extends EqualityExpression {
  override def toString: String = {
    s"${x0} != ${x2}"
  }
}
case class EqualityExpression3(x0: EqualityExpression, x2: RelationalExpression) extends EqualityExpression {
  override def toString: String = {
    s"${x0} === ${x2}"
  }
}
case class EqualityExpression4(x0: EqualityExpression, x2: RelationalExpression) extends EqualityExpression {
  override def toString: String = {
    s"${x0} !== ${x2}"
  }
}
trait BitwiseANDExpression extends AST
case class BitwiseANDExpression0(x0: EqualityExpression) extends BitwiseANDExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class BitwiseANDExpression1(x0: BitwiseANDExpression, x2: EqualityExpression) extends BitwiseANDExpression {
  override def toString: String = {
    s"${x0} & ${x2}"
  }
}
trait BitwiseXORExpression extends AST
case class BitwiseXORExpression0(x0: BitwiseANDExpression) extends BitwiseXORExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class BitwiseXORExpression1(x0: BitwiseXORExpression, x2: BitwiseANDExpression) extends BitwiseXORExpression {
  override def toString: String = {
    s"${x0} ^ ${x2}"
  }
}
trait BitwiseORExpression extends AST
case class BitwiseORExpression0(x0: BitwiseXORExpression) extends BitwiseORExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class BitwiseORExpression1(x0: BitwiseORExpression, x2: BitwiseXORExpression) extends BitwiseORExpression {
  override def toString: String = {
    s"${x0} | ${x2}"
  }
}
trait LogicalANDExpression extends AST
case class LogicalANDExpression0(x0: BitwiseORExpression) extends LogicalANDExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class LogicalANDExpression1(x0: LogicalANDExpression, x2: BitwiseORExpression) extends LogicalANDExpression {
  override def toString: String = {
    s"${x0} && ${x2}"
  }
}
trait LogicalORExpression extends AST
case class LogicalORExpression0(x0: LogicalANDExpression) extends LogicalORExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class LogicalORExpression1(x0: LogicalORExpression, x2: LogicalANDExpression) extends LogicalORExpression {
  override def toString: String = {
    s"${x0} || ${x2}"
  }
}
trait ConditionalExpression extends AST
case class ConditionalExpression0(x0: LogicalORExpression) extends ConditionalExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class ConditionalExpression1(x0: LogicalORExpression, x2: AssignmentExpression, x4: AssignmentExpression) extends ConditionalExpression {
  override def toString: String = {
    s"${x0} ? ${x2} : ${x4}"
  }
}
trait AssignmentExpression extends AST
case class AssignmentExpression0(x0: ConditionalExpression) extends AssignmentExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class AssignmentExpression1(x0: YieldExpression) extends AssignmentExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class AssignmentExpression2(x0: ArrowFunction) extends AssignmentExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class AssignmentExpression3(x0: AsyncArrowFunction) extends AssignmentExpression {
  override def toString: String = {
    s"${x0}"
  }
}
case class AssignmentExpression4(x0: LeftHandSideExpression, x2: AssignmentExpression) extends AssignmentExpression {
  override def toString: String = {
    s"${x0} = ${x2}"
  }
}
case class AssignmentExpression5(x0: LeftHandSideExpression, x1: AssignmentOperator, x2: AssignmentExpression) extends AssignmentExpression {
  override def toString: String = {
    s"${x0} ${x1} ${x2}"
  }
}
trait AssignmentPattern extends AST
case class AssignmentPattern0(x0: ObjectAssignmentPattern) extends AssignmentPattern {
  override def toString: String = {
    s"${x0}"
  }
}
case class AssignmentPattern1(x0: ArrayAssignmentPattern) extends AssignmentPattern {
  override def toString: String = {
    s"${x0}"
  }
}
trait ObjectAssignmentPattern extends AST
case object ObjectAssignmentPattern0 extends ObjectAssignmentPattern {
  override def toString: String = {
    s"{ }"
  }
}
case class ObjectAssignmentPattern1(x1: AssignmentRestProperty) extends ObjectAssignmentPattern {
  override def toString: String = {
    s"{ ${x1} }"
  }
}
case class ObjectAssignmentPattern2(x1: AssignmentPropertyList) extends ObjectAssignmentPattern {
  override def toString: String = {
    s"{ ${x1} }"
  }
}
case class ObjectAssignmentPattern3(x1: AssignmentPropertyList, x3: Option[AssignmentRestProperty]) extends ObjectAssignmentPattern {
  override def toString: String = {
    s"{ ${x1} , ${x3.getOrElse("")} }"
  }
}
trait ArrayAssignmentPattern extends AST
case class ArrayAssignmentPattern0(x1: Option[Elision], x2: Option[AssignmentRestElement]) extends ArrayAssignmentPattern {
  override def toString: String = {
    s"[ ${x1.getOrElse("")} ${x2.getOrElse("")} ]"
  }
}
case class ArrayAssignmentPattern1(x1: AssignmentElementList) extends ArrayAssignmentPattern {
  override def toString: String = {
    s"[ ${x1} ]"
  }
}
case class ArrayAssignmentPattern2(x1: AssignmentElementList, x3: Option[Elision], x4: Option[AssignmentRestElement]) extends ArrayAssignmentPattern {
  override def toString: String = {
    s"[ ${x1} , ${x3.getOrElse("")} ${x4.getOrElse("")} ]"
  }
}
trait AssignmentPropertyList extends AST
case class AssignmentPropertyList0(x0: AssignmentProperty) extends AssignmentPropertyList {
  override def toString: String = {
    s"${x0}"
  }
}
case class AssignmentPropertyList1(x0: AssignmentPropertyList, x2: AssignmentProperty) extends AssignmentPropertyList {
  override def toString: String = {
    s"${x0} , ${x2}"
  }
}
trait AssignmentElementList extends AST
case class AssignmentElementList0(x0: AssignmentElisionElement) extends AssignmentElementList {
  override def toString: String = {
    s"${x0}"
  }
}
case class AssignmentElementList1(x0: AssignmentElementList, x2: AssignmentElisionElement) extends AssignmentElementList {
  override def toString: String = {
    s"${x0} , ${x2}"
  }
}
trait AssignmentElisionElement extends AST
case class AssignmentElisionElement0(x0: Option[Elision], x1: AssignmentElement) extends AssignmentElisionElement {
  override def toString: String = {
    s"${x0.getOrElse("")} ${x1}"
  }
}
trait AssignmentProperty extends AST
case class AssignmentProperty0(x0: IdentifierReference, x1: Option[Initializer]) extends AssignmentProperty {
  override def toString: String = {
    s"${x0} ${x1.getOrElse("")}"
  }
}
case class AssignmentProperty1(x0: PropertyName, x2: AssignmentElement) extends AssignmentProperty {
  override def toString: String = {
    s"${x0} : ${x2}"
  }
}
trait AssignmentElement extends AST
case class AssignmentElement0(x0: DestructuringAssignmentTarget, x1: Option[Initializer]) extends AssignmentElement {
  override def toString: String = {
    s"${x0} ${x1.getOrElse("")}"
  }
}
trait AssignmentRestElement extends AST
case class AssignmentRestElement0(x1: DestructuringAssignmentTarget) extends AssignmentRestElement {
  override def toString: String = {
    s"... ${x1}"
  }
}
trait DestructuringAssignmentTarget extends AST
case class DestructuringAssignmentTarget0(x0: LeftHandSideExpression) extends DestructuringAssignmentTarget {
  override def toString: String = {
    s"${x0}"
  }
}
trait AssignmentOperator extends AST
case object AssignmentOperator0 extends AssignmentOperator {
  override def toString: String = {
    s"*="
  }
}
case object AssignmentOperator1 extends AssignmentOperator {
  override def toString: String = {
    s"/="
  }
}
case object AssignmentOperator2 extends AssignmentOperator {
  override def toString: String = {
    s"%="
  }
}
case object AssignmentOperator3 extends AssignmentOperator {
  override def toString: String = {
    s"+="
  }
}
case object AssignmentOperator4 extends AssignmentOperator {
  override def toString: String = {
    s"-="
  }
}
case object AssignmentOperator5 extends AssignmentOperator {
  override def toString: String = {
    s"<<="
  }
}
case object AssignmentOperator6 extends AssignmentOperator {
  override def toString: String = {
    s">>="
  }
}
case object AssignmentOperator7 extends AssignmentOperator {
  override def toString: String = {
    s">>>="
  }
}
case object AssignmentOperator8 extends AssignmentOperator {
  override def toString: String = {
    s"&="
  }
}
case object AssignmentOperator9 extends AssignmentOperator {
  override def toString: String = {
    s"^="
  }
}
case object AssignmentOperator10 extends AssignmentOperator {
  override def toString: String = {
    s"|="
  }
}
case object AssignmentOperator11 extends AssignmentOperator {
  override def toString: String = {
    s"**="
  }
}
trait Expression extends AST
case class Expression0(x0: AssignmentExpression) extends Expression {
  override def toString: String = {
    s"${x0}"
  }
}
case class Expression1(x0: Expression, x2: AssignmentExpression) extends Expression {
  override def toString: String = {
    s"${x0} , ${x2}"
  }
}
trait Statement extends AST
case class Statement0(x0: BlockStatement) extends Statement {
  override def toString: String = {
    s"${x0}"
  }
}
case class Statement1(x0: VariableStatement) extends Statement {
  override def toString: String = {
    s"${x0}"
  }
}
case class Statement2(x0: EmptyStatement) extends Statement {
  override def toString: String = {
    s"${x0}"
  }
}
case class Statement3(x0: ExpressionStatement) extends Statement {
  override def toString: String = {
    s"${x0}"
  }
}
case class Statement4(x0: IfStatement) extends Statement {
  override def toString: String = {
    s"${x0}"
  }
}
case class Statement5(x0: BreakableStatement) extends Statement {
  override def toString: String = {
    s"${x0}"
  }
}
case class Statement6(x0: ContinueStatement) extends Statement {
  override def toString: String = {
    s"${x0}"
  }
}
case class Statement7(x0: BreakStatement) extends Statement {
  override def toString: String = {
    s"${x0}"
  }
}
case class Statement8(x0: ReturnStatement) extends Statement {
  override def toString: String = {
    s"${x0}"
  }
}
case class Statement9(x0: WithStatement) extends Statement {
  override def toString: String = {
    s"${x0}"
  }
}
case class Statement10(x0: LabelledStatement) extends Statement {
  override def toString: String = {
    s"${x0}"
  }
}
case class Statement11(x0: ThrowStatement) extends Statement {
  override def toString: String = {
    s"${x0}"
  }
}
case class Statement12(x0: TryStatement) extends Statement {
  override def toString: String = {
    s"${x0}"
  }
}
case class Statement13(x0: DebuggerStatement) extends Statement {
  override def toString: String = {
    s"${x0}"
  }
}
trait Declaration extends AST
case class Declaration0(x0: HoistableDeclaration) extends Declaration {
  override def toString: String = {
    s"${x0}"
  }
}
case class Declaration1(x0: ClassDeclaration) extends Declaration {
  override def toString: String = {
    s"${x0}"
  }
}
case class Declaration2(x0: LexicalDeclaration) extends Declaration {
  override def toString: String = {
    s"${x0}"
  }
}
trait HoistableDeclaration extends AST
case class HoistableDeclaration0(x0: FunctionDeclaration) extends HoistableDeclaration {
  override def toString: String = {
    s"${x0}"
  }
}
case class HoistableDeclaration1(x0: GeneratorDeclaration) extends HoistableDeclaration {
  override def toString: String = {
    s"${x0}"
  }
}
case class HoistableDeclaration2(x0: AsyncFunctionDeclaration) extends HoistableDeclaration {
  override def toString: String = {
    s"${x0}"
  }
}
case class HoistableDeclaration3(x0: AsyncGeneratorDeclaration) extends HoistableDeclaration {
  override def toString: String = {
    s"${x0}"
  }
}
trait BreakableStatement extends AST
case class BreakableStatement0(x0: IterationStatement) extends BreakableStatement {
  override def toString: String = {
    s"${x0}"
  }
}
case class BreakableStatement1(x0: SwitchStatement) extends BreakableStatement {
  override def toString: String = {
    s"${x0}"
  }
}
trait BlockStatement extends AST
case class BlockStatement0(x0: Block) extends BlockStatement {
  override def toString: String = {
    s"${x0}"
  }
}
trait Block extends AST
case class Block0(x1: Option[StatementList]) extends Block {
  override def toString: String = {
    s"{ ${x1.getOrElse("")} }"
  }
}
trait StatementList extends AST
case class StatementList0(x0: StatementListItem) extends StatementList {
  override def toString: String = {
    s"${x0}"
  }
}
case class StatementList1(x0: StatementList, x1: StatementListItem) extends StatementList {
  override def toString: String = {
    s"${x0} ${x1}"
  }
}
trait StatementListItem extends AST
case class StatementListItem0(x0: Statement) extends StatementListItem {
  override def toString: String = {
    s"${x0}"
  }
}
case class StatementListItem1(x0: Declaration) extends StatementListItem {
  override def toString: String = {
    s"${x0}"
  }
}
trait LexicalDeclaration extends AST
case class LexicalDeclaration0(x0: LetOrConst, x1: BindingList) extends LexicalDeclaration {
  override def toString: String = {
    s"${x0} ${x1} ;"
  }
}
trait LetOrConst extends AST
case object LetOrConst0 extends LetOrConst {
  override def toString: String = {
    s"let"
  }
}
case object LetOrConst1 extends LetOrConst {
  override def toString: String = {
    s"const"
  }
}
trait BindingList extends AST
case class BindingList0(x0: LexicalBinding) extends BindingList {
  override def toString: String = {
    s"${x0}"
  }
}
case class BindingList1(x0: BindingList, x2: LexicalBinding) extends BindingList {
  override def toString: String = {
    s"${x0} , ${x2}"
  }
}
trait LexicalBinding extends AST
case class LexicalBinding0(x0: BindingIdentifier, x1: Option[Initializer]) extends LexicalBinding {
  override def toString: String = {
    s"${x0} ${x1.getOrElse("")}"
  }
}
case class LexicalBinding1(x0: BindingPattern, x1: Initializer) extends LexicalBinding {
  override def toString: String = {
    s"${x0} ${x1}"
  }
}
trait VariableStatement extends AST
case class VariableStatement0(x1: VariableDeclarationList) extends VariableStatement {
  override def toString: String = {
    s"var ${x1} ;"
  }
}
trait VariableDeclarationList extends AST
case class VariableDeclarationList0(x0: VariableDeclaration) extends VariableDeclarationList {
  override def toString: String = {
    s"${x0}"
  }
}
case class VariableDeclarationList1(x0: VariableDeclarationList, x2: VariableDeclaration) extends VariableDeclarationList {
  override def toString: String = {
    s"${x0} , ${x2}"
  }
}
trait VariableDeclaration extends AST
case class VariableDeclaration0(x0: BindingIdentifier, x1: Option[Initializer]) extends VariableDeclaration {
  override def toString: String = {
    s"${x0} ${x1.getOrElse("")}"
  }
}
case class VariableDeclaration1(x0: BindingPattern, x1: Initializer) extends VariableDeclaration {
  override def toString: String = {
    s"${x0} ${x1}"
  }
}
trait BindingPattern extends AST
case class BindingPattern0(x0: ObjectBindingPattern) extends BindingPattern {
  override def toString: String = {
    s"${x0}"
  }
}
case class BindingPattern1(x0: ArrayBindingPattern) extends BindingPattern {
  override def toString: String = {
    s"${x0}"
  }
}
trait ObjectBindingPattern extends AST
case object ObjectBindingPattern0 extends ObjectBindingPattern {
  override def toString: String = {
    s"{ }"
  }
}
case class ObjectBindingPattern1(x1: BindingRestProperty) extends ObjectBindingPattern {
  override def toString: String = {
    s"{ ${x1} }"
  }
}
case class ObjectBindingPattern2(x1: BindingPropertyList) extends ObjectBindingPattern {
  override def toString: String = {
    s"{ ${x1} }"
  }
}
case class ObjectBindingPattern3(x1: BindingPropertyList, x3: Option[BindingRestProperty]) extends ObjectBindingPattern {
  override def toString: String = {
    s"{ ${x1} , ${x3.getOrElse("")} }"
  }
}
trait ArrayBindingPattern extends AST
case class ArrayBindingPattern0(x1: Option[Elision], x2: Option[BindingRestElement]) extends ArrayBindingPattern {
  override def toString: String = {
    s"[ ${x1.getOrElse("")} ${x2.getOrElse("")} ]"
  }
}
case class ArrayBindingPattern1(x1: BindingElementList) extends ArrayBindingPattern {
  override def toString: String = {
    s"[ ${x1} ]"
  }
}
case class ArrayBindingPattern2(x1: BindingElementList, x3: Option[Elision], x4: Option[BindingRestElement]) extends ArrayBindingPattern {
  override def toString: String = {
    s"[ ${x1} , ${x3.getOrElse("")} ${x4.getOrElse("")} ]"
  }
}
trait BindingPropertyList extends AST
case class BindingPropertyList0(x0: BindingProperty) extends BindingPropertyList {
  override def toString: String = {
    s"${x0}"
  }
}
case class BindingPropertyList1(x0: BindingPropertyList, x2: BindingProperty) extends BindingPropertyList {
  override def toString: String = {
    s"${x0} , ${x2}"
  }
}
trait BindingElementList extends AST
case class BindingElementList0(x0: BindingElisionElement) extends BindingElementList {
  override def toString: String = {
    s"${x0}"
  }
}
case class BindingElementList1(x0: BindingElementList, x2: BindingElisionElement) extends BindingElementList {
  override def toString: String = {
    s"${x0} , ${x2}"
  }
}
trait BindingElisionElement extends AST
case class BindingElisionElement0(x0: Option[Elision], x1: BindingElement) extends BindingElisionElement {
  override def toString: String = {
    s"${x0.getOrElse("")} ${x1}"
  }
}
trait BindingProperty extends AST
case class BindingProperty0(x0: SingleNameBinding) extends BindingProperty {
  override def toString: String = {
    s"${x0}"
  }
}
case class BindingProperty1(x0: PropertyName, x2: BindingElement) extends BindingProperty {
  override def toString: String = {
    s"${x0} : ${x2}"
  }
}
trait BindingElement extends AST
case class BindingElement0(x0: SingleNameBinding) extends BindingElement {
  override def toString: String = {
    s"${x0}"
  }
}
case class BindingElement1(x0: BindingPattern, x1: Option[Initializer]) extends BindingElement {
  override def toString: String = {
    s"${x0} ${x1.getOrElse("")}"
  }
}
trait SingleNameBinding extends AST
case class SingleNameBinding0(x0: BindingIdentifier, x1: Option[Initializer]) extends SingleNameBinding {
  override def toString: String = {
    s"${x0} ${x1.getOrElse("")}"
  }
}
trait BindingRestElement extends AST
case class BindingRestElement0(x1: BindingIdentifier) extends BindingRestElement {
  override def toString: String = {
    s"... ${x1}"
  }
}
case class BindingRestElement1(x1: BindingPattern) extends BindingRestElement {
  override def toString: String = {
    s"... ${x1}"
  }
}
trait EmptyStatement extends AST
case object EmptyStatement0 extends EmptyStatement {
  override def toString: String = {
    s";"
  }
}
trait ExpressionStatement extends AST
case class ExpressionStatement0(x1: Expression) extends ExpressionStatement {
  override def toString: String = {
    s"${x1} ;"
  }
}
trait IfStatement extends AST
case class IfStatement0(x2: Expression, x4: Statement, x6: Statement) extends IfStatement {
  override def toString: String = {
    s"if ( ${x2} ) ${x4} else ${x6}"
  }
}
case class IfStatement1(x2: Expression, x4: Statement) extends IfStatement {
  override def toString: String = {
    s"if ( ${x2} ) ${x4}"
  }
}
trait IterationStatement extends AST
case class IterationStatement0(x1: Statement, x4: Expression) extends IterationStatement {
  override def toString: String = {
    s"do ${x1} while ( ${x4} ) ;"
  }
}
case class IterationStatement1(x2: Expression, x4: Statement) extends IterationStatement {
  override def toString: String = {
    s"while ( ${x2} ) ${x4}"
  }
}
case class IterationStatement2(x3: Option[Expression], x5: Option[Expression], x7: Option[Expression], x9: Statement) extends IterationStatement {
  override def toString: String = {
    s"for ( ${x3.getOrElse("")} ; ${x5.getOrElse("")} ; ${x7.getOrElse("")} ) ${x9}"
  }
}
case class IterationStatement3(x3: VariableDeclarationList, x5: Option[Expression], x7: Option[Expression], x9: Statement) extends IterationStatement {
  override def toString: String = {
    s"for ( var ${x3} ; ${x5.getOrElse("")} ; ${x7.getOrElse("")} ) ${x9}"
  }
}
case class IterationStatement4(x2: LexicalDeclaration, x3: Option[Expression], x5: Option[Expression], x7: Statement) extends IterationStatement {
  override def toString: String = {
    s"for ( ${x2} ${x3.getOrElse("")} ; ${x5.getOrElse("")} ) ${x7}"
  }
}
case class IterationStatement5(x3: LeftHandSideExpression, x5: Expression, x7: Statement) extends IterationStatement {
  override def toString: String = {
    s"for ( ${x3} in ${x5} ) ${x7}"
  }
}
case class IterationStatement6(x3: ForBinding, x5: Expression, x7: Statement) extends IterationStatement {
  override def toString: String = {
    s"for ( var ${x3} in ${x5} ) ${x7}"
  }
}
case class IterationStatement7(x2: ForDeclaration, x4: Expression, x6: Statement) extends IterationStatement {
  override def toString: String = {
    s"for ( ${x2} in ${x4} ) ${x6}"
  }
}
case class IterationStatement8(x3: LeftHandSideExpression, x5: AssignmentExpression, x7: Statement) extends IterationStatement {
  override def toString: String = {
    s"for ( ${x3} of ${x5} ) ${x7}"
  }
}
case class IterationStatement9(x3: ForBinding, x5: AssignmentExpression, x7: Statement) extends IterationStatement {
  override def toString: String = {
    s"for ( var ${x3} of ${x5} ) ${x7}"
  }
}
case class IterationStatement10(x2: ForDeclaration, x4: AssignmentExpression, x6: Statement) extends IterationStatement {
  override def toString: String = {
    s"for ( ${x2} of ${x4} ) ${x6}"
  }
}
case class IterationStatement11(x4: LeftHandSideExpression, x6: AssignmentExpression, x8: Statement) extends IterationStatement {
  override def toString: String = {
    s"for await ( ${x4} of ${x6} ) ${x8}"
  }
}
case class IterationStatement12(x4: ForBinding, x6: AssignmentExpression, x8: Statement) extends IterationStatement {
  override def toString: String = {
    s"for await ( var ${x4} of ${x6} ) ${x8}"
  }
}
case class IterationStatement13(x3: ForDeclaration, x5: AssignmentExpression, x7: Statement) extends IterationStatement {
  override def toString: String = {
    s"for await ( ${x3} of ${x5} ) ${x7}"
  }
}
trait ForDeclaration extends AST
case class ForDeclaration0(x0: LetOrConst, x1: ForBinding) extends ForDeclaration {
  override def toString: String = {
    s"${x0} ${x1}"
  }
}
trait ForBinding extends AST
case class ForBinding0(x0: BindingIdentifier) extends ForBinding {
  override def toString: String = {
    s"${x0}"
  }
}
case class ForBinding1(x0: BindingPattern) extends ForBinding {
  override def toString: String = {
    s"${x0}"
  }
}
trait ContinueStatement extends AST
case object ContinueStatement0 extends ContinueStatement {
  override def toString: String = {
    s"continue ;"
  }
}
case class ContinueStatement1(x2: LabelIdentifier) extends ContinueStatement {
  override def toString: String = {
    s"continue ${x2} ;"
  }
}
trait BreakStatement extends AST
case object BreakStatement0 extends BreakStatement {
  override def toString: String = {
    s"break ;"
  }
}
case class BreakStatement1(x2: LabelIdentifier) extends BreakStatement {
  override def toString: String = {
    s"break ${x2} ;"
  }
}
trait ReturnStatement extends AST
case object ReturnStatement0 extends ReturnStatement {
  override def toString: String = {
    s"return ;"
  }
}
case class ReturnStatement1(x2: Expression) extends ReturnStatement {
  override def toString: String = {
    s"return ${x2} ;"
  }
}
trait WithStatement extends AST
case class WithStatement0(x2: Expression, x4: Statement) extends WithStatement {
  override def toString: String = {
    s"with ( ${x2} ) ${x4}"
  }
}
trait SwitchStatement extends AST
case class SwitchStatement0(x2: Expression, x4: CaseBlock) extends SwitchStatement {
  override def toString: String = {
    s"switch ( ${x2} ) ${x4}"
  }
}
trait CaseBlock extends AST
case class CaseBlock0(x1: Option[CaseClauses]) extends CaseBlock {
  override def toString: String = {
    s"{ ${x1.getOrElse("")} }"
  }
}
case class CaseBlock1(x1: Option[CaseClauses], x2: DefaultClause, x3: Option[CaseClauses]) extends CaseBlock {
  override def toString: String = {
    s"{ ${x1.getOrElse("")} ${x2} ${x3.getOrElse("")} }"
  }
}
trait CaseClauses extends AST
case class CaseClauses0(x0: CaseClause) extends CaseClauses {
  override def toString: String = {
    s"${x0}"
  }
}
case class CaseClauses1(x0: CaseClauses, x1: CaseClause) extends CaseClauses {
  override def toString: String = {
    s"${x0} ${x1}"
  }
}
trait CaseClause extends AST
case class CaseClause0(x1: Expression, x3: Option[StatementList]) extends CaseClause {
  override def toString: String = {
    s"case ${x1} : ${x3.getOrElse("")}"
  }
}
trait DefaultClause extends AST
case class DefaultClause0(x2: Option[StatementList]) extends DefaultClause {
  override def toString: String = {
    s"default : ${x2.getOrElse("")}"
  }
}
trait LabelledStatement extends AST
case class LabelledStatement0(x0: LabelIdentifier, x2: LabelledItem) extends LabelledStatement {
  override def toString: String = {
    s"${x0} : ${x2}"
  }
}
trait LabelledItem extends AST
case class LabelledItem0(x0: Statement) extends LabelledItem {
  override def toString: String = {
    s"${x0}"
  }
}
case class LabelledItem1(x0: FunctionDeclaration) extends LabelledItem {
  override def toString: String = {
    s"${x0}"
  }
}
trait ThrowStatement extends AST
case class ThrowStatement0(x2: Expression) extends ThrowStatement {
  override def toString: String = {
    s"throw ${x2} ;"
  }
}
trait TryStatement extends AST
case class TryStatement0(x1: Block, x2: Catch) extends TryStatement {
  override def toString: String = {
    s"try ${x1} ${x2}"
  }
}
case class TryStatement1(x1: Block, x2: Finally) extends TryStatement {
  override def toString: String = {
    s"try ${x1} ${x2}"
  }
}
case class TryStatement2(x1: Block, x2: Catch, x3: Finally) extends TryStatement {
  override def toString: String = {
    s"try ${x1} ${x2} ${x3}"
  }
}
trait Catch extends AST
case class Catch0(x2: CatchParameter, x4: Block) extends Catch {
  override def toString: String = {
    s"catch ( ${x2} ) ${x4}"
  }
}
trait Finally extends AST
case class Finally0(x1: Block) extends Finally {
  override def toString: String = {
    s"finally ${x1}"
  }
}
trait CatchParameter extends AST
case class CatchParameter0(x0: BindingIdentifier) extends CatchParameter {
  override def toString: String = {
    s"${x0}"
  }
}
case class CatchParameter1(x0: BindingPattern) extends CatchParameter {
  override def toString: String = {
    s"${x0}"
  }
}
trait DebuggerStatement extends AST
case object DebuggerStatement0 extends DebuggerStatement {
  override def toString: String = {
    s"debugger ;"
  }
}
trait FunctionDeclaration extends AST
case class FunctionDeclaration0(x1: BindingIdentifier, x3: FormalParameters, x6: FunctionBody) extends FunctionDeclaration {
  override def toString: String = {
    s"function ${x1} ( ${x3} ) { ${x6} }"
  }
}
case class FunctionDeclaration1(x2: FormalParameters, x5: FunctionBody) extends FunctionDeclaration {
  override def toString: String = {
    s"function ( ${x2} ) { ${x5} }"
  }
}
trait FunctionExpression extends AST
case class FunctionExpression0(x1: Option[BindingIdentifier], x3: FormalParameters, x6: FunctionBody) extends FunctionExpression {
  override def toString: String = {
    s"function ${x1.getOrElse("")} ( ${x3} ) { ${x6} }"
  }
}
trait UniqueFormalParameters extends AST
case class UniqueFormalParameters0(x0: FormalParameters) extends UniqueFormalParameters {
  override def toString: String = {
    s"${x0}"
  }
}
trait FormalParameters extends AST
case object FormalParameters0 extends FormalParameters {
  override def toString: String = {
    s""
  }
}
case class FormalParameters1(x0: FunctionRestParameter) extends FormalParameters {
  override def toString: String = {
    s"${x0}"
  }
}
case class FormalParameters2(x0: FormalParameterList) extends FormalParameters {
  override def toString: String = {
    s"${x0}"
  }
}
case class FormalParameters3(x0: FormalParameterList) extends FormalParameters {
  override def toString: String = {
    s"${x0} ,"
  }
}
case class FormalParameters4(x0: FormalParameterList, x2: FunctionRestParameter) extends FormalParameters {
  override def toString: String = {
    s"${x0} , ${x2}"
  }
}
trait FormalParameterList extends AST
case class FormalParameterList0(x0: FormalParameter) extends FormalParameterList {
  override def toString: String = {
    s"${x0}"
  }
}
case class FormalParameterList1(x0: FormalParameterList, x2: FormalParameter) extends FormalParameterList {
  override def toString: String = {
    s"${x0} , ${x2}"
  }
}
trait FunctionRestParameter extends AST
case class FunctionRestParameter0(x0: BindingRestElement) extends FunctionRestParameter {
  override def toString: String = {
    s"${x0}"
  }
}
trait FormalParameter extends AST
case class FormalParameter0(x0: BindingElement) extends FormalParameter {
  override def toString: String = {
    s"${x0}"
  }
}
trait FunctionBody extends AST
case class FunctionBody0(x0: FunctionStatementList) extends FunctionBody {
  override def toString: String = {
    s"${x0}"
  }
}
trait FunctionStatementList extends AST
case class FunctionStatementList0(x0: Option[StatementList]) extends FunctionStatementList {
  override def toString: String = {
    s"${x0.getOrElse("")}"
  }
}
trait ArrowFunction extends AST
case class ArrowFunction0(x0: ArrowParameters, x3: ConciseBody) extends ArrowFunction {
  override def toString: String = {
    s"${x0} => ${x3}"
  }
}
trait ArrowParameters extends AST
case class ArrowParameters0(x0: BindingIdentifier) extends ArrowParameters {
  override def toString: String = {
    s"${x0}"
  }
}
case class ArrowParameters1(x0: CoverParenthesizedExpressionAndArrowParameterList) extends ArrowParameters {
  override def toString: String = {
    s"${x0}"
  }
}
trait ConciseBody extends AST
case class ConciseBody0(x1: AssignmentExpression) extends ConciseBody {
  override def toString: String = {
    s"${x1}"
  }
}
case class ConciseBody1(x1: FunctionBody) extends ConciseBody {
  override def toString: String = {
    s"{ ${x1} }"
  }
}
trait ArrowFormalParameters extends AST
case class ArrowFormalParameters0(x1: UniqueFormalParameters) extends ArrowFormalParameters {
  override def toString: String = {
    s"( ${x1} )"
  }
}
trait AsyncArrowFunction extends AST
case class AsyncArrowFunction0(x2: AsyncArrowBindingIdentifier, x5: AsyncConciseBody) extends AsyncArrowFunction {
  override def toString: String = {
    s"async ${x2} => ${x5}"
  }
}
case class AsyncArrowFunction1(x0: CoverCallExpressionAndAsyncArrowHead, x3: AsyncConciseBody) extends AsyncArrowFunction {
  override def toString: String = {
    s"${x0} => ${x3}"
  }
}
trait AsyncConciseBody extends AST
case class AsyncConciseBody0(x1: AssignmentExpression) extends AsyncConciseBody {
  override def toString: String = {
    s"${x1}"
  }
}
case class AsyncConciseBody1(x1: AsyncFunctionBody) extends AsyncConciseBody {
  override def toString: String = {
    s"{ ${x1} }"
  }
}
trait AsyncArrowHead extends AST
case class AsyncArrowHead0(x2: ArrowFormalParameters) extends AsyncArrowHead {
  override def toString: String = {
    s"async ${x2}"
  }
}
trait MethodDefinition extends AST
case class MethodDefinition0(x0: PropertyName, x2: UniqueFormalParameters, x5: FunctionBody) extends MethodDefinition {
  override def toString: String = {
    s"${x0} ( ${x2} ) { ${x5} }"
  }
}
case class MethodDefinition1(x0: GeneratorMethod) extends MethodDefinition {
  override def toString: String = {
    s"${x0}"
  }
}
case class MethodDefinition2(x0: AsyncMethod) extends MethodDefinition {
  override def toString: String = {
    s"${x0}"
  }
}
case class MethodDefinition3(x0: AsyncGeneratorMethod) extends MethodDefinition {
  override def toString: String = {
    s"${x0}"
  }
}
case class MethodDefinition4(x1: PropertyName, x5: FunctionBody) extends MethodDefinition {
  override def toString: String = {
    s"get ${x1} ( ) { ${x5} }"
  }
}
case class MethodDefinition5(x1: PropertyName, x3: PropertySetParameterList, x6: FunctionBody) extends MethodDefinition {
  override def toString: String = {
    s"set ${x1} ( ${x3} ) { ${x6} }"
  }
}
trait PropertySetParameterList extends AST
case class PropertySetParameterList0(x0: FormalParameter) extends PropertySetParameterList {
  override def toString: String = {
    s"${x0}"
  }
}
trait GeneratorMethod extends AST
case class GeneratorMethod0(x1: PropertyName, x3: UniqueFormalParameters, x6: GeneratorBody) extends GeneratorMethod {
  override def toString: String = {
    s"* ${x1} ( ${x3} ) { ${x6} }"
  }
}
trait GeneratorDeclaration extends AST
case class GeneratorDeclaration0(x2: BindingIdentifier, x4: FormalParameters, x7: GeneratorBody) extends GeneratorDeclaration {
  override def toString: String = {
    s"function * ${x2} ( ${x4} ) { ${x7} }"
  }
}
case class GeneratorDeclaration1(x3: FormalParameters, x6: GeneratorBody) extends GeneratorDeclaration {
  override def toString: String = {
    s"function * ( ${x3} ) { ${x6} }"
  }
}
trait GeneratorExpression extends AST
case class GeneratorExpression0(x2: Option[BindingIdentifier], x4: FormalParameters, x7: GeneratorBody) extends GeneratorExpression {
  override def toString: String = {
    s"function * ${x2.getOrElse("")} ( ${x4} ) { ${x7} }"
  }
}
trait GeneratorBody extends AST
case class GeneratorBody0(x0: FunctionBody) extends GeneratorBody {
  override def toString: String = {
    s"${x0}"
  }
}
trait YieldExpression extends AST
case object YieldExpression0 extends YieldExpression {
  override def toString: String = {
    s"yield"
  }
}
case class YieldExpression1(x2: AssignmentExpression) extends YieldExpression {
  override def toString: String = {
    s"yield ${x2}"
  }
}
case class YieldExpression2(x3: AssignmentExpression) extends YieldExpression {
  override def toString: String = {
    s"yield * ${x3}"
  }
}
trait AsyncMethod extends AST
case class AsyncMethod0(x2: PropertyName, x4: UniqueFormalParameters, x7: AsyncFunctionBody) extends AsyncMethod {
  override def toString: String = {
    s"async ${x2} ( ${x4} ) { ${x7} }"
  }
}
trait AsyncFunctionDeclaration extends AST
case class AsyncFunctionDeclaration0(x3: BindingIdentifier, x5: FormalParameters, x8: AsyncFunctionBody) extends AsyncFunctionDeclaration {
  override def toString: String = {
    s"async function ${x3} ( ${x5} ) { ${x8} }"
  }
}
case class AsyncFunctionDeclaration1(x4: FormalParameters, x7: AsyncFunctionBody) extends AsyncFunctionDeclaration {
  override def toString: String = {
    s"async function ( ${x4} ) { ${x7} }"
  }
}
trait AsyncFunctionExpression extends AST
case class AsyncFunctionExpression0(x4: FormalParameters, x7: AsyncFunctionBody) extends AsyncFunctionExpression {
  override def toString: String = {
    s"async function ( ${x4} ) { ${x7} }"
  }
}
case class AsyncFunctionExpression1(x3: BindingIdentifier, x5: FormalParameters, x8: AsyncFunctionBody) extends AsyncFunctionExpression {
  override def toString: String = {
    s"async function ${x3} ( ${x5} ) { ${x8} }"
  }
}
trait AsyncFunctionBody extends AST
case class AsyncFunctionBody0(x0: FunctionBody) extends AsyncFunctionBody {
  override def toString: String = {
    s"${x0}"
  }
}
trait AwaitExpression extends AST
case class AwaitExpression0(x1: UnaryExpression) extends AwaitExpression {
  override def toString: String = {
    s"await ${x1}"
  }
}
trait ClassDeclaration extends AST
case class ClassDeclaration0(x1: BindingIdentifier, x2: ClassTail) extends ClassDeclaration {
  override def toString: String = {
    s"class ${x1} ${x2}"
  }
}
case class ClassDeclaration1(x1: ClassTail) extends ClassDeclaration {
  override def toString: String = {
    s"class ${x1}"
  }
}
trait ClassExpression extends AST
case class ClassExpression0(x1: Option[BindingIdentifier], x2: ClassTail) extends ClassExpression {
  override def toString: String = {
    s"class ${x1.getOrElse("")} ${x2}"
  }
}
trait ClassTail extends AST
case class ClassTail0(x0: Option[ClassHeritage], x2: Option[ClassBody]) extends ClassTail {
  override def toString: String = {
    s"${x0.getOrElse("")} { ${x2.getOrElse("")} }"
  }
}
trait ClassHeritage extends AST
case class ClassHeritage0(x1: LeftHandSideExpression) extends ClassHeritage {
  override def toString: String = {
    s"extends ${x1}"
  }
}
trait ClassBody extends AST
case class ClassBody0(x0: ClassElementList) extends ClassBody {
  override def toString: String = {
    s"${x0}"
  }
}
trait ClassElementList extends AST
case class ClassElementList0(x0: ClassElement) extends ClassElementList {
  override def toString: String = {
    s"${x0}"
  }
}
case class ClassElementList1(x0: ClassElementList, x1: ClassElement) extends ClassElementList {
  override def toString: String = {
    s"${x0} ${x1}"
  }
}
trait ClassElement extends AST
case class ClassElement0(x0: MethodDefinition) extends ClassElement {
  override def toString: String = {
    s"${x0}"
  }
}
case class ClassElement1(x1: MethodDefinition) extends ClassElement {
  override def toString: String = {
    s"static ${x1}"
  }
}
case object ClassElement2 extends ClassElement {
  override def toString: String = {
    s";"
  }
}
trait Script extends AST
case class Script0(x0: Option[ScriptBody]) extends Script {
  override def toString: String = {
    s"${x0.getOrElse("")}"
  }
}
trait ScriptBody extends AST
case class ScriptBody0(x0: StatementList) extends ScriptBody {
  override def toString: String = {
    s"${x0}"
  }
}
trait Module extends AST
case class Module0(x0: Option[ModuleBody]) extends Module {
  override def toString: String = {
    s"${x0.getOrElse("")}"
  }
}
trait ModuleBody extends AST
case class ModuleBody0(x0: ModuleItemList) extends ModuleBody {
  override def toString: String = {
    s"${x0}"
  }
}
trait ModuleItemList extends AST
case class ModuleItemList0(x0: ModuleItem) extends ModuleItemList {
  override def toString: String = {
    s"${x0}"
  }
}
case class ModuleItemList1(x0: ModuleItemList, x1: ModuleItem) extends ModuleItemList {
  override def toString: String = {
    s"${x0} ${x1}"
  }
}
trait ModuleItem extends AST
case class ModuleItem0(x0: ImportDeclaration) extends ModuleItem {
  override def toString: String = {
    s"${x0}"
  }
}
case class ModuleItem1(x0: ExportDeclaration) extends ModuleItem {
  override def toString: String = {
    s"${x0}"
  }
}
case class ModuleItem2(x0: StatementListItem) extends ModuleItem {
  override def toString: String = {
    s"${x0}"
  }
}
trait ImportDeclaration extends AST
case class ImportDeclaration0(x1: ImportClause, x2: FromClause) extends ImportDeclaration {
  override def toString: String = {
    s"import ${x1} ${x2} ;"
  }
}
case class ImportDeclaration1(x1: ModuleSpecifier) extends ImportDeclaration {
  override def toString: String = {
    s"import ${x1} ;"
  }
}
trait ImportClause extends AST
case class ImportClause0(x0: ImportedDefaultBinding) extends ImportClause {
  override def toString: String = {
    s"${x0}"
  }
}
case class ImportClause1(x0: NameSpaceImport) extends ImportClause {
  override def toString: String = {
    s"${x0}"
  }
}
case class ImportClause2(x0: NamedImports) extends ImportClause {
  override def toString: String = {
    s"${x0}"
  }
}
case class ImportClause3(x0: ImportedDefaultBinding, x2: NameSpaceImport) extends ImportClause {
  override def toString: String = {
    s"${x0} , ${x2}"
  }
}
case class ImportClause4(x0: ImportedDefaultBinding, x2: NamedImports) extends ImportClause {
  override def toString: String = {
    s"${x0} , ${x2}"
  }
}
trait ImportedDefaultBinding extends AST
case class ImportedDefaultBinding0(x0: ImportedBinding) extends ImportedDefaultBinding {
  override def toString: String = {
    s"${x0}"
  }
}
trait NameSpaceImport extends AST
case class NameSpaceImport0(x2: ImportedBinding) extends NameSpaceImport {
  override def toString: String = {
    s"* as ${x2}"
  }
}
trait NamedImports extends AST
case object NamedImports0 extends NamedImports {
  override def toString: String = {
    s"{ }"
  }
}
case class NamedImports1(x1: ImportsList) extends NamedImports {
  override def toString: String = {
    s"{ ${x1} }"
  }
}
case class NamedImports2(x1: ImportsList) extends NamedImports {
  override def toString: String = {
    s"{ ${x1} , }"
  }
}
trait FromClause extends AST
case class FromClause0(x1: ModuleSpecifier) extends FromClause {
  override def toString: String = {
    s"from ${x1}"
  }
}
trait ImportsList extends AST
case class ImportsList0(x0: ImportSpecifier) extends ImportsList {
  override def toString: String = {
    s"${x0}"
  }
}
case class ImportsList1(x0: ImportsList, x2: ImportSpecifier) extends ImportsList {
  override def toString: String = {
    s"${x0} , ${x2}"
  }
}
trait ImportSpecifier extends AST
case class ImportSpecifier0(x0: ImportedBinding) extends ImportSpecifier {
  override def toString: String = {
    s"${x0}"
  }
}
case class ImportSpecifier1(x0: String, x2: ImportedBinding) extends ImportSpecifier {
  override def toString: String = {
    s"${x0} as ${x2}"
  }
}
trait ModuleSpecifier extends AST
case class ModuleSpecifier0(x0: String) extends ModuleSpecifier {
  override def toString: String = {
    s"${x0}"
  }
}
trait ImportedBinding extends AST
case class ImportedBinding0(x0: BindingIdentifier) extends ImportedBinding {
  override def toString: String = {
    s"${x0}"
  }
}
trait ExportDeclaration extends AST
case class ExportDeclaration0(x2: FromClause) extends ExportDeclaration {
  override def toString: String = {
    s"export * ${x2} ;"
  }
}
case class ExportDeclaration1(x1: ExportClause, x2: FromClause) extends ExportDeclaration {
  override def toString: String = {
    s"export ${x1} ${x2} ;"
  }
}
case class ExportDeclaration2(x1: ExportClause) extends ExportDeclaration {
  override def toString: String = {
    s"export ${x1} ;"
  }
}
case class ExportDeclaration3(x1: VariableStatement) extends ExportDeclaration {
  override def toString: String = {
    s"export ${x1}"
  }
}
case class ExportDeclaration4(x1: Declaration) extends ExportDeclaration {
  override def toString: String = {
    s"export ${x1}"
  }
}
case class ExportDeclaration5(x2: HoistableDeclaration) extends ExportDeclaration {
  override def toString: String = {
    s"export default ${x2}"
  }
}
case class ExportDeclaration6(x2: ClassDeclaration) extends ExportDeclaration {
  override def toString: String = {
    s"export default ${x2}"
  }
}
case class ExportDeclaration7(x3: AssignmentExpression) extends ExportDeclaration {
  override def toString: String = {
    s"export default ${x3} ;"
  }
}
trait ExportClause extends AST
case object ExportClause0 extends ExportClause {
  override def toString: String = {
    s"{ }"
  }
}
case class ExportClause1(x1: ExportsList) extends ExportClause {
  override def toString: String = {
    s"{ ${x1} }"
  }
}
case class ExportClause2(x1: ExportsList) extends ExportClause {
  override def toString: String = {
    s"{ ${x1} , }"
  }
}
trait ExportsList extends AST
case class ExportsList0(x0: ExportSpecifier) extends ExportsList {
  override def toString: String = {
    s"${x0}"
  }
}
case class ExportsList1(x0: ExportsList, x2: ExportSpecifier) extends ExportsList {
  override def toString: String = {
    s"${x0} , ${x2}"
  }
}
trait ExportSpecifier extends AST
case class ExportSpecifier0(x0: String) extends ExportSpecifier {
  override def toString: String = {
    s"${x0}"
  }
}
case class ExportSpecifier1(x0: String, x2: String) extends ExportSpecifier {
  override def toString: String = {
    s"${x0} as ${x2}"
  }
}
trait AsyncGeneratorMethod extends AST
case class AsyncGeneratorMethod0(x3: PropertyName, x5: UniqueFormalParameters, x8: AsyncGeneratorBody) extends AsyncGeneratorMethod {
  override def toString: String = {
    s"async * ${x3} ( ${x5} ) { ${x8} }"
  }
}
trait AsyncGeneratorDeclaration extends AST
case class AsyncGeneratorDeclaration0(x4: BindingIdentifier, x6: FormalParameters, x9: AsyncGeneratorBody) extends AsyncGeneratorDeclaration {
  override def toString: String = {
    s"async function * ${x4} ( ${x6} ) { ${x9} }"
  }
}
case class AsyncGeneratorDeclaration1(x5: FormalParameters, x8: AsyncGeneratorBody) extends AsyncGeneratorDeclaration {
  override def toString: String = {
    s"async function * ( ${x5} ) { ${x8} }"
  }
}
trait AsyncGeneratorExpression extends AST
case class AsyncGeneratorExpression0(x4: Option[BindingIdentifier], x6: FormalParameters, x9: AsyncGeneratorBody) extends AsyncGeneratorExpression {
  override def toString: String = {
    s"async function * ${x4.getOrElse("")} ( ${x6} ) { ${x9} }"
  }
}
trait AsyncGeneratorBody extends AST
case class AsyncGeneratorBody0(x0: FunctionBody) extends AsyncGeneratorBody {
  override def toString: String = {
    s"${x0}"
  }
}
trait AssignmentRestProperty extends AST
case class AssignmentRestProperty0(x1: DestructuringAssignmentTarget) extends AssignmentRestProperty {
  override def toString: String = {
    s"... ${x1}"
  }
}
trait SubstitutionTemplate extends AST
case class SubstitutionTemplate0(x0: String, x1: Expression, x2: TemplateSpans) extends SubstitutionTemplate {
  override def toString: String = {
    s"${x0} ${x1} ${x2}"
  }
}
trait BindingRestProperty extends AST
case class BindingRestProperty0(x1: BindingIdentifier) extends BindingRestProperty {
  override def toString: String = {
    s"... ${x1}"
  }
}

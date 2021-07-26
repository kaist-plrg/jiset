const Node = require('../Node');

// CallExpression[Yield, Await] :
//    CoverCallExpressionAndAsyncArrowHead[?Yield, ?Await]
//    SuperCall[?Yield, ?Await]
//    ImportCall[?Yield, ?Await]
//    CallExpression[?Yield, ?Await] Arguments[?Yield, ?Await]
//    CallExpression[?Yield, ?Await] [ Expression[+In, ?Yield, ?Await] ]
//    CallExpression[?Yield, ?Await] . IdentifierName
//    CallExpression[?Yield, ?Await] TemplateLiteral[?Yield, ?Await, +Tagged]
let CallExpression = (Yield, Await) => (given) => {
  const CoverCallExpressionAndAsyncArrowHead = require('./CoverCallExpressionAndAsyncArrowHead');
  const Expression = require('./Expression');
  const Arguments = require('./Arguments');
  const SuperCall = require('./SuperCall');

  let params = [Yield, Await];
  let { type, callee } = given;

  if (type === 'MemberExpression') {
    const { object, property, computed } = given;
    const callExpr = CallExpression(Yield, Await)(object);
    if (!computed) return new Node('CallExpression', given, 5, [callExpr, property.name], params);
    else {
      const expr = Expression(true, Yield, Await)(property);
      return new Node('CallExpression', given, 4, [callExpr, expr], params);
    }
  } else if (type != 'CallExpression') {
    Node.TODO(`${type} @ CallExpression`);
  } else if (callee.type == 'Super') {
    const call = SuperCall(Yield, Await)(given);
    return new Node('CallExpression', given, 1, [call], params);
  } else if ((callee.type === 'MemberExpression' && callee.object.type === 'CallExpression') || callee.type === 'CallExpression') {
    // handles expressions like a().b()
    const callExpr = CallExpression(Yield, Await)(callee);
    const args = Arguments(Yield, Await)(given.arguments);
    const node = new Node('CallExpression', given, 3, [callExpr, args], params);
    return node;
  } else {
    let cover = CoverCallExpressionAndAsyncArrowHead(Yield, Await)(given)
    return new Node('CallExpression', given, 0, [cover], params);
  }
}

module.exports = CallExpression;

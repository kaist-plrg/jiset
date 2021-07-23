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
  const Arguments = require('./Arguments');

  let params = [Yield, Await];
  let { type, callee } = given;

  if (type === 'MemberExpression') {
    const { object, property } = given;
    const callExpr = CallExpression(Yield, Await)(object);
    return new Node('CallExpression', given, 5, [callExpr, property.name], params);
  } else if (type != 'CallExpression') {
    Node.TODO(`${type} @ CallExpression`);
  } else if (callee.type == 'Super') {
    Node.TODO('Super @ CallExpression');
  } else if (callee.type == 'CallExpression') {
    Node.TODO(`${callee.type} @ CallExpression`);
  } else if (callee.type === 'MemberExpression' && callee.object.type === 'CallExpression') {
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

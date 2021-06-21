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
  let params = [Yield, Await];
  let { type, callee } = given;
  if (type != 'CallExpression') {
    Node.TODO(`${type} @ CallExpression`);
  } else if (callee.type == 'Super') {
    Node.TODO('Super @ CallExpression');
  } else if (callee.type == 'CallExpression') {
    Node.TODO(`${callee.type} @ CallExpression`);
  } else {
    let cover = CoverCallExpressionAndAsyncArrowHead(Yield, Await)(given)
    return new Node('CallExpression', given, 0, [cover], params);
  }
}

module.exports = CallExpression;

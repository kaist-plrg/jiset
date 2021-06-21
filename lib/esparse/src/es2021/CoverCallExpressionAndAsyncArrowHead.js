const Node = require('../Node');

// CoverCallExpressionAndAsyncArrowHead[Yield, Await] :
//    MemberExpression[?Yield, ?Await] Arguments[?Yield, ?Await]
let CoverCallExpressionAndAsyncArrowHead = (Yield, Await) => (given) => {
  const MemberExpression = require('./MemberExpression');
  const Arguments = require('./Arguments');
  let params = [Yield, Await];
  let { callee } = given;
  let member = MemberExpression(Yield, Await)(callee);
  let args = Arguments(Yield, Await)(given.arguments);
  return new Node('CoverCallExpressionAndAsyncArrowHead', given, 0, [member, args], params);
}

module.exports = CoverCallExpressionAndAsyncArrowHead;

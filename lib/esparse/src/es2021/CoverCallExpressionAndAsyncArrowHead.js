const Node = require('../Node');
const { checkComma } = require('../Global');

// CoverCallExpressionAndAsyncArrowHead[Yield, Await] :
//    MemberExpression[?Yield, ?Await] Arguments[?Yield, ?Await]
let CoverCallExpressionAndAsyncArrowHead = (Yield, Await) => (given) => {
  const MemberExpression = require('./MemberExpression');
  const Arguments = require('./Arguments');
  let params = [Yield, Await];
  let { callee } = given;
  let member = MemberExpression(Yield, Await)(callee);
  let hasComma = checkComma(callee.end, given.end, ')');
  let args = Arguments(Yield, Await)(given.arguments, hasComma);
  return new Node('CoverCallExpressionAndAsyncArrowHead', given, 0, [member, args], params);
}

module.exports = CoverCallExpressionAndAsyncArrowHead;

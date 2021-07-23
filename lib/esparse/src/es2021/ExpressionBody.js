const Node = require('../Node');

// ExpressionBody[In, Await]: {
//   AssignmentExpression[?In, ~Yield, ?Await]
// }

let ExpressionBody = (In, Await) => (given) => {
  const AssignmentExpression = require('./AssignmentExpression');
  let params = [In, Await];
  let expr = AssignmentExpression(In, false, Await)(given);
  return new Node('ExpressionBody', given, 0, [expr], params);
}

module.exports = ExpressionBody;

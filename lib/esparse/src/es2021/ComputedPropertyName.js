const Node = require('../Node');

// ComputedPropertyName[Yield, Await]: {
//   `[` AssignmentExpression[+In, ?Yield, ?Await] `]`
// }

let ComputedPropertyName = (Yield, Await) => (given) => {
  let AssignmentExpression = require('./AssignmentExpression');
  let params = [Yield, Await];
  let name = AssignmentExpression(true, Yield, Await)(given);
  return new Node('ComputedPropertyName', given, 0, [name], params);
}

module.exports = ComputedPropertyName;

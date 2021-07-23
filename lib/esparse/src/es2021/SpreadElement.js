const Node = require('../Node');

// SpreadElement[Yield, Await] :
//    ... AssignmentExpression[+In, ?Yield, ?Await]
let SpreadElement = (Yield, Await) => (given) => {
  let AssignmentExpression = require('./AssignmentExpression');
  let params = [Yield, Await];
  let expr = AssignmentExpression(true, Yield, Await)(given.argument);
  return new Node('SpreadElement', given, 0, [expr], params);
}

module.exports = SpreadElement;

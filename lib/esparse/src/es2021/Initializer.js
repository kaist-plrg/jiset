const Node = require('../Node');

// Initializer[In, Yield, Await] :
//    = AssignmentExpression[?In, ?Yield, ?Await]
let Initializer = (In, Yield, Await) => (given) => {
  let AssignmentExpression = require('./AssignmentExpression');
  let expr = AssignmentExpression(In, Yield, Await)(given);
  let params = [In, Yield, Await];
  return new Node('Initializer', given, 0, [expr], params);
}

module.exports = Initializer;

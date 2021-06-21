const Node = require('../Node');

// Expression[In, Yield, Await] :
//    AssignmentExpression[?In, ?Yield, ?Await]
//    Expression[?In, ?Yield, ?Await] , AssignmentExpression[?In, ?Yield, ?Await]
let Expression = (In, Yield, Await) => (given) => {
  const AssignmentExpression = require('./AssignmentExpression');
  let params = [In, Yield, Await];
  if (!Array.isArray(given)) given = [given];
  let size = given.length;
  if (size == 1) {
    let expr = AssignmentExpression(In, Yield, Await)(given[0]);
    return new Node('Expression', given[0], 0, [expr], params);
  } else {
    let genChild = AssignmentExpression(In, Yield, Await);
    return Node.fromList('Expression', given, genChild, params);
  }
}

module.exports = Expression;

const Node = require('../Node');

// Expression[In, Yield, Await] :
//    AssignmentExpression[?In, ?Yield, ?Await]
//    Expression[?In, ?Yield, ?Await] , AssignmentExpression[?In, ?Yield, ?Await]
let Expression = (In, Yield, Await) => (given) => {
  const AssignmentExpression = require('./AssignmentExpression');
  let params = [In, Yield, Await];
  let loc = given.loc;
  
  if (given.type === 'SequenceExpression') given = given.expressions;
  else if (!Array.isArray(given)) given = [given];

  let size = given.length;
  if (size == 1) {
    let expr = AssignmentExpression(In, Yield, Await)(given[0]);
    let node = new Node('Expression', given[0], 0, [expr], params);
    node.loc = loc;
    return node;
  } else {
    let genChild = AssignmentExpression(In, Yield, Await);
    let node = Node.fromList('Expression', given, genChild, params);
    node.loc = loc;
    return node;
  }
}

module.exports = Expression;

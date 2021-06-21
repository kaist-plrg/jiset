const Node = require('../Node');

// ArgumentList[Yield, Await] :
//    AssignmentExpression[+In, ?Yield, ?Await]
//    ... AssignmentExpression[+In, ?Yield, ?Await]
//    ArgumentList[?Yield, ?Await] , AssignmentExpression[+In, ?Yield, ?Await]
//    ArgumentList[?Yield, ?Await] , ... AssignmentExpression[+In, ?Yield, ?Await]
let ArgumentList = (Yield, Await) => (given) => {
  const AssignmentExpression = require('./AssignmentExpression');
  let params = [Yield, Await];
  let size = given.length;
  let last = given.pop();
  let expr, index;
  if (last.type != 'SpreadElement') {
    expr = AssignmentExpression(true, Yield, Await)(last);
    index = 0;
  } else {
    expr = AssignmentExpression(true, Yield, Await)(last.argument);
    index = 1;
  }

  if (size == 1) {
    return new Node('ArgumentList', last, index, [expr], params);
  } else {
    index += 2;
    let list = ArgumentList(Yield, Await)(given);
    return new Node('ArgumentList', last, index, [list, expr], params);
  }
}

module.exports = ArgumentList;

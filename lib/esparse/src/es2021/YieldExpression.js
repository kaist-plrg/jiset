const Node = require('../Node');

// YieldExpression[In, Await] :
//    yield
//    yield [no LineTerminator here] AssignmentExpression[?In, +Yield, ?Await]
//    yield [no LineTerminator here] * AssignmentExpression[?In, +Yield, ?Await]
let YieldExpression = (In, Await) => (given) => {
  const AssignmentExpression = require('./AssignmentExpression');
  let params = [In, Await];
  let { argument, delegate } = given;
  if (argument == null) {
    return new Node('YieldExpression', given, 0, [], params);
  } else if (!delegate) {
    let expr = AssignmentExpression(In, true, Await)(argument);
    return new Node('YieldExpression', given, 1, [expr], params);
  } else {
    let expr = AssignmentExpression(In, true, Await)(argument);
    return new Node('YieldExpression', given, 2, [expr], params);
  }
}

module.exports = YieldExpression;

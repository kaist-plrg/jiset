const Node = require('../Node');

// IfStatement[Yield, Await, Return] :
//    if ( Expression[+In, ?Yield, ?Await] ) Statement[?Yield, ?Await, ?Return] else Statement[?Yield, ?Await, ?Return]
//    if ( Expression[+In, ?Yield, ?Await] ) Statement[?Yield, ?Await, ?Return] [lookahead ≠ else]
let IfStatement = (Yield, Await, Return) => (given) => {
  const Expression = require('./Expression');
  const Statement = require('./Statement');
  let params = [Yield, Await, Return];
  let { test, consequent, alternate } = given;
  let expr = Expression(true, Yield, Await)(test);
  let cons = Statement(Yield, Await, Return)(consequent);
  if (alternate != null) {
    let alter = Statement(Yield, Await, Return)(alternate);
    return new Node('IfStatement', given, 0, [expr, cons, alter], params);
  } else {
    return new Node('IfStatement', given, 1, [expr, cons], params);
  }
}

module.exports = IfStatement;

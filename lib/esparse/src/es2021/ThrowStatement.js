const Node = require('../Node');

// ThrowStatement[Yield, Await] :
//    throw [no LineTerminator here] Expression[+In, ?Yield, ?Await] ;
let ThrowStatement = (Yield, Await) => (given) => {
  const Expression = require('./Expression');
  let params = [Yield, Await];
  let expr = Expression(true, Yield, Await)(given.argument);
  return new Node('ThrowStatement', given, 0, [expr], params);
}

module.exports = ThrowStatement;

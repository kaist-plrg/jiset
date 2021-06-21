const Node = require('../Node');

// ReturnStatement[Yield, Await] :
//    return ;
//    return [no LineTerminator here] Expression[+In, ?Yield, ?Await] ;
let ReturnStatement = (Yield, Await) => (given) => {
  const Expression = require('./Expression');
  let params = [Yield, Await];
  let { argument } = given;
  if (argument == null) {
    return new Node('ReturnStatement', given, 0, [], params);
  } else {
    let expr = Expression(true, Yield, Await)(argument);
    return new Node('ReturnStatement', given, 1, [expr], params);
  }
}

module.exports = ReturnStatement;

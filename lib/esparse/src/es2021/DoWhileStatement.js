const Node = require('../Node');

// DoWhileStatement[Yield, Await, Return]: {
//   `do` Statement[?Yield, ?Await, ?Return] `while` `(` Expression[+In, ?Yield, ?Await] `)` `;`
// }

let DoWhileStatement = (Yield, Await, Return) => (given) => {
  const Statement = require('./Statement');
  const Expression = require('./Expression');
  
  let params = [Yield, Await, Return];
  const body = Statement(Yield, Await, Return)(given.body);
  const test = Expression(true, Yield, Await)(given.test);
  
  return new Node('DoWhileStatement', given, 0, [body, test], params);
}

module.exports = DoWhileStatement;

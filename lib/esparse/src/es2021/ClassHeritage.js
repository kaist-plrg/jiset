const Node = require('../Node');

// ClassHeritage[Yield, Await]: {
//   `extends` LeftHandSideExpression[?Yield, ?Await]
// }

let ClassHeritage = (Yield, Await) => (given) => {
  let LeftHandSideExpression = require('./LeftHandSideExpression');
  let params = [Yield, Await];
  let lhs = LeftHandSideExpression(Yield, Await)(given);
  return new Node('ClassHeritage', given, 0, [lhs], params);
}

module.exports = ClassHeritage;

const Node = require('../Node');

// LabelledStatement[Yield, Await, Return]: {
//   LabelIdentifier[?Yield, ?Await] `:` LabelledItem[?Yield, ?Await, ?Return]
// }

let LabelledStatement = (Yield, Await, Return) => (given) => {
  const LabelIdentifier = require('./LabelIdentifier');
  const LabelledItem = require('./LabelledItem');
  let params = [Yield, Await, Return];
  
  const label = LabelIdentifier(Yield, Await)(given.label);
  const body = LabelledItem(Yield, Await, Return)(given.body);
  
  return new Node('LabelledStatement', given, 0, [label, body], params);
}

module.exports = LabelledStatement;

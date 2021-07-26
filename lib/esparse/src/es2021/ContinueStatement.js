const Node = require('../Node');

// ContinueStatement[Yield, Await]: {
//   `continue` `;` |
//   `continue` [no LineTerminator here] LabelIdentifier[?Yield, ?Await] `;`
// }

let ContinueStatement = (Yield, Await) => (given) => {
  const LabelIdentifier = require('./LabelIdentifier');
  let params = [Yield, Await];
  
  // [0,0]
  if (given.label === null) 
    return new Node('ContinueStatement', given, 0, [], params);
  // [1,0]
  else {
    let label = LabelIdentifier(Yield, Await)(given.label);
    return new Node('ContinueStatement', given, 1, [label], params);
  }
}

module.exports = ContinueStatement;

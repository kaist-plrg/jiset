const Node = require('../Node');

// Finally[Yield, Await, Return]: {
//   `finally` Block[?Yield, ?Await, ?Return]
// }

let Finally = (Yield, Await, Return) => (given) => {
  let Block = require('./Block');
  let params = [Yield, Await, Return];
  let b = Block(Yield, Await, Return)(given);
  return new Node('Finally', given, 0, [b], params);
}

module.exports = Finally;

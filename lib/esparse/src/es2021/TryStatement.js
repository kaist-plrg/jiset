const Node = require('../Node');

// TryStatement[Yield, Await, Return] :
//    try Block[?Yield, ?Await, ?Return] Catch[?Yield, ?Await, ?Return]
//    try Block[?Yield, ?Await, ?Return] Finally[?Yield, ?Await, ?Return]
//    try Block[?Yield, ?Await, ?Return] Catch[?Yield, ?Await, ?Return] Finally[?Yield, ?Await, ?Return]
let TryStatement = (Yield, Await, Return) => (given) => {
  const Block = require('./Block');
  const Catch = require('./Catch');
  const Finally = require('./Finally');

  let params = [Yield, Await, Return];
  let { block, handler, finalizer } = given;

  let b = Block(Yield, Await, Return)(block);

  if (handler != null && finalizer == null) {
    let c = Catch(Yield, Await, Return)(handler);
    return new Node('TryStatement', given, 0, [b, c], params);
  } else if (handler == null && finalizer != null) {
    let f = Finally(Yield, Await, Return)(finalizer);
    return new Node('TryStatement', given, 1, [b, f], params);
  } else if (handler != null && finalizer != null) {
    let c = Catch(Yield, Await, Return)(handler);
    let f = Finally(Yield, Await, Return)(finalizer);
    return new Node('TryStatement', given, 2, [b, c, f], params);
  } else {
    Node.TODO('TryStatement');
  }
}

module.exports = TryStatement;

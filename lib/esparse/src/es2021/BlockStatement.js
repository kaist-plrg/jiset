const Node = require('../Node');

// BlockStatement[Yield, Await, Return] :
//    Block[?Yield, ?Await, ?Return]
let BlockStatement = (Yield, Await, Return) => (given) => {
  const Block = require('./Block');
  let params = [Yield, Await, Return];
  let block = Block(Yield, Await, Return)(given);
  return new Node('BlockStatement', given, 0, [block], params);
}

module.exports = BlockStatement;

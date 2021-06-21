const Node = require('../Node');

// BreakableStatement[Yield, Await, Return] :
//    IterationStatement[?Yield, ?Await, ?Return]
//    SwitchStatement[?Yield, ?Await, ?Return]
let BreakableStatement = (Yield, Await, Return) => (given) => {
  const IterationStatement = require('./IterationStatement');
  const SwitchStatement = require('./SwitchStatement');
  let params = [Yield, Await, Return];
  if (given.type == 'SwitchStatement') {
    let stmt = SwitchStatement(Yield, Await, Return)(given);
    return new Node('BreakableStatement', given, 1, [stmt], params);
  } else {
    let stmt = IterationStatement(Yield, Await, Return)(given);
    return new Node('BreakableStatement', given, 0, [stmt], params);
  }
}

module.exports = BreakableStatement;

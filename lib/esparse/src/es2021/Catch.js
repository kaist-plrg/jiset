const Node = require('../Node');

// Catch[Yield, Await, Return] :
//    catch ( CatchParameter[?Yield, ?Await] ) Block[?Yield, ?Await, ?Return]
//    catch Block[?Yield, ?Await, ?Return]

let Catch = (Yield, Await, Return) => (given) => {
  const CatchParameter = require('./CatchParameter');
  const Block = require('./Block');
  let params = [Yield, Await, Return];
  let { body, param } = given;
  let b = Block(Yield, Await, Return)(body)
  if (param != null) {
    let c = CatchParameter(Yield, Await)(param);
    return new Node('Catch', given, 0, [c, b], params);
  } else {
    return new Node('Catch', given, 1, [b], params);
  }
}

module.exports = Catch;

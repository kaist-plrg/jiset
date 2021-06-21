const Node = require('../Node');

// ArrowFunction[In, Yield, Await] :
//    ArrowParameters[?Yield, ?Await] [no LineTerminator here] => ConciseBody[?In]
let ArrowFunction = (In, Yield, Await) => (given) => {
  const ArrowParameters = require('./ArrowParameters');
  const ConciseBody = require('./ConciseBody');
  let params = [In, Yield, Await];
  let ps = ArrowParameters(Yield, Await)(given.params);
  let b = ConciseBody(In)(given.body);
  return new Node('ArrowFunction', given, 0, [ps, b], params);
}

module.exports = ArrowFunction;

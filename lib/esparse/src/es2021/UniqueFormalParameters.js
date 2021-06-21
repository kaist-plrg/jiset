const Node = require('../Node');

// UniqueFormalParameters[Yield, Await] :
//    FormalParameters[?Yield, ?Await]
let UniqueFormalParameters = (Yield, Await) => (given) => {
  const FormalParameters = require('./FormalParameters');
  let params = [Yield, Await];
  let ps = FormalParameters(Yield, Await)(given);
  return new Node('UniqueFormalParameters', given, 0, [ps], params);
}

module.exports = UniqueFormalParameters;

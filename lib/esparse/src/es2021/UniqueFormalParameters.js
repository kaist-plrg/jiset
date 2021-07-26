const Node = require('../Node');

// UniqueFormalParameters[Yield, Await] :
//    FormalParameters[?Yield, ?Await]
let UniqueFormalParameters = (Yield, Await) => (given, hasComma) => {
  const FormalParameters = require('./FormalParameters');
  let params = [Yield, Await];
  let ps = FormalParameters(Yield, Await)(given, hasComma);
  return new Node('UniqueFormalParameters', given, 0, [ps], params);
}

module.exports = UniqueFormalParameters;

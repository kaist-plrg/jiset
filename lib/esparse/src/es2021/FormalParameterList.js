const Node = require('../Node');

// FormalParameterList[Yield, Await] :
//    FormalParameter[?Yield, ?Await]
//    FormalParameterList[?Yield, ?Await] , FormalParameter[?Yield, ?Await]
let FormalParameterList = (Yield, Await) => (given) => {
  const FormalParameter = require('./FormalParameter');
  let genChild = FormalParameter(Yield, Await);
  let params = [Yield, Await];
  return Node.fromList('FormalParameterList', given, genChild, params);
}

module.exports = FormalParameterList;

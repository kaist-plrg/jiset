const Node = require('../Node');

// FormalParameters[Yield, Await] :
//    [empty]
//    FunctionRestParameter[?Yield, ?Await]
//    FormalParameterList[?Yield, ?Await]
//    FormalParameterList[?Yield, ?Await] ,
//    FormalParameterList[?Yield, ?Await] , FunctionRestParameter[?Yield, ?Await]
// TODO fix span
let FormalParameters = (Yield, Await) => (given, hasComma) => {
  const FunctionRestParameter = require('./FunctionRestParameter');
  const FormalParameterList = require('./FormalParameterList');
  let params = [Yield, Await];
  let size = given.length;
  let last = given[size - 1];
  if (size == 0) {
    return new Node('FormalParameters', given, 0, [], params);
  } else if (size == 1 && last.type == 'RestElement') {
    let rest = FunctionRestParameter(Yield, Await)(last);
    return new Node('FormalParameters', given, 1, [rest], params);
  } else if (last.type != 'RestElement') {
    let list = FormalParameterList(Yield, Await)(given);
    let idx = hasComma ? 3 : 2;
    return new Node('FormalParameters', given, idx, [list], params);
  } else {
    given.pop();
    let list = FormalParameterList(Yield, Await)(given);
    let rest = FunctionRestParameter(Yield, Await)(last);
    let node = new Node('FormalParameters', given, 4, [list, rest], params);
    node.start = list.start;
    node.end = rest.end;
    return node;
  }
}

module.exports = FormalParameters;

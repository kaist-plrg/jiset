const Node = require('../Node');
const { checkComma } = require('../Global');

// FormalParameters[Yield, Await] :
//    [empty]
//    FunctionRestParameter[?Yield, ?Await]
//    FormalParameterList[?Yield, ?Await]
//    FormalParameterList[?Yield, ?Await] ,
//    FormalParameterList[?Yield, ?Await] , FunctionRestParameter[?Yield, ?Await]
let FormalParameters = (Yield, Await) => (given) => {
  const FunctionRestParameter = require('./FunctionRestParameter');
  const FormalParameterList = require('./FormalParameterList');
  let params = [Yield, Await];
  let size = given.length;
  let last = given[size - 1];
  if (size == 0) {
    return new Node('FormalParameters', {}, 0, [], params);
  } else if (size == 1 && last.type == 'RestElement') {
    let rest = FunctionRestParameter(Yield, Await)(last);
    return new Node('FormalParameters', last, 1, [rest], params);
  } else if (last.type != 'RestElement') {
    let list = FormalParameterList(Yield, Await)(given);
    const hasComma = checkComma(given.start, given.end, '');
    if (hasComma) return new Node('FormalParameters', given, 3, [list, null], params);
    else return new Node('FormalParameters', given, 2, [list], params);
  } else {
    given.pop();
    let list = FormalParameterList(Yield, Await)(given);
    let rest = FunctionRestParameter(Yield, Await)(last);
    let node = new Node('FormalParameters', {}, 4, [list, rest], params);
    node.start = list.start;
    node.end = rest.end;
    return node;
  }
}

module.exports = FormalParameters;

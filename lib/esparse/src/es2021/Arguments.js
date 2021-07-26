const Node = require('../Node');

// Arguments[Yield, Await] :
//    ( )
//    ( ArgumentList[?Yield, ?Await] )
//    ( ArgumentList[?Yield, ?Await] , )
let Arguments = (Yield, Await) => (given, hasComma) => {
  const ArgumentList = require('./ArgumentList');
  let params = [Yield, Await];
  let size = given.length;
  if (size == 0) {
    return new Node('Arguments', given, 0, [], params);
  } else {
    let list = ArgumentList(Yield, Await)(given);
    let idx = hasComma ? 2 : 1;
    return new Node('Arguments', given, idx, [list], params);
  }
}

module.exports = Arguments;

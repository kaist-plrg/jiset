const Node = require('../Node');

// VariableDeclarationList[In, Yield, Await] :
//    VariableDeclaration[?In, ?Yield, ?Await]
//    VariableDeclarationList[?In, ?Yield, ?Await] , VariableDeclaration[?In, ?Yield, ?Await]
let VariableDeclarationList = (In, Yield, Await) => (given) => {
  let VariableDeclaration = require('./VariableDeclaration');

  let genChild = VariableDeclaration(In, Yield, Await);
  let params = [In, Yield, Await];
  return Node.fromList(
    'VariableDeclarationList',
    given.declarations,
    genChild,
    params
  );
}

module.exports = VariableDeclarationList;

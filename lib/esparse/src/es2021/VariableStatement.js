const Node = require('../Node');

// VariableStatement[Yield, Await] :
//    var VariableDeclarationList[+In, ?Yield, ?Await] ;
let VariableStatement = (Yield, Await) => (given) => {
  let VariableDeclarationList = require('./VariableDeclarationList');

  let declList = VariableDeclarationList(true, Yield, Await)(given);
  let params = [Yield, Await];
  return new Node('VariableStatement', given, 0, [declList], params);
}

module.exports = VariableStatement;

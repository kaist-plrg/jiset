const Node = require('../Node');

// ForDeclaration[Yield, Await] :
//    LetOrConst ForBinding[?Yield, ?Await]
let ForDeclaration = (Yield, Await) => (given) => {
  const ForBinding = require('./ForBinding');
  const LetOrConst = require('./LetOrConst');
  let params = [Yield, Await];
  let { kind, declarations } = given;
  let pre = LetOrConst(kind);
  let binding = ForBinding(Yield, Await)(declarations[0]);
  return new Node('ForDeclaration', given, 0, [pre, binding], params);
}

module.exports = ForDeclaration;

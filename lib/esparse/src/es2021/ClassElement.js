const Node = require('../Node');

// ClassElement[Yield, Await] :
//    MethodDefinition[?Yield, ?Await]
//    static MethodDefinition[?Yield, ?Await]
//    ;
let ClassElement = (Yield, Await) => (given) => {
  const MethodDefinition = require('./MethodDefinition');
  let params = [Yield, Await];
  let { static } = given;
  let index = 0;
  if (static) index = 1;
  let method = MethodDefinition(Yield, Await)(given);
  return new Node('ClassElement', given, index, [method], params);
}

module.exports = ClassElement;

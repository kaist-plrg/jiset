const Node = require('../Node');

// ClassDeclaration[Yield, Await, Default] :
//    class BindingIdentifier[?Yield, ?Await] ClassTail[?Yield, ?Await]
//    [+Default] class ClassTail[?Yield, ?Await]
let ClassDeclaration = (Yield, Await, Default) => (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  const ClassTail = require('./ClassTail');
  let params = [Yield, Await, Default];
  let { id } = given;
  let tail = ClassTail(Yield, Await)(given);
  if (id) {
    let x = BindingIdentifier(Yield, Await)(id);
    return new Node('ClassDeclaration', given, 0, [x, tail], params);
  } else {
    return new Node('ClassDeclaration', given, 1, [tail], params);
  }
}

module.exports = ClassDeclaration;

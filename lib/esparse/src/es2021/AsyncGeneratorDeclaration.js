const Node = require('../Node');

// AsyncGeneratorDeclaration[Yield, Await, Default] :
//    async [no LineTerminator here] function * BindingIdentifier[?Yield, ?Await] ( FormalParameters[+Yield, +Await] ) { AsyncGeneratorBody }
//    [+Default] async [no LineTerminator here] function * ( FormalParameters[+Yield, +Await] ) { AsyncGeneratorBody }
let AsyncGeneratorDeclaration = (Yield, Await, Default) => (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  const FormalParameters = require('./FormalParameters');
  const AsyncGeneratorBody = require('./AsyncGeneratorBody');
  let params = [Yield, Await, Default];
  let { id, body } = given;
  let ps = FormalParameters(true, true)(given.params);
  let b = AsyncGeneratorBody(body);
  if (id) {
    let x = BindingIdentifier(Yield, Await)(id);
    return new Node('AsyncGeneratorDeclaration', given, 0, [x, ps, b], params);
  } else {
    return new Node('AsyncGeneratorDeclaration', given, 1, [ps, b], params);
  }
}

module.exports = AsyncGeneratorDeclaration;

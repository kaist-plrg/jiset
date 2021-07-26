const Node = require('../Node');
const { checkComma } = require('../Global');

// AsyncFunctionDeclaration[Yield, Await, Default] :
//    async [no LineTerminator here] function BindingIdentifier[?Yield, ?Await] ( FormalParameters[~Yield, +Await] ) { AsyncFunctionBody }
//    [+Default] async [no LineTerminator here] function ( FormalParameters[~Yield, +Await] ) { AsyncFunctionBody }
let AsyncFunctionDeclaration = (Yield, Await, Default) => (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  const FormalParameters = require('./FormalParameters');
  const AsyncFunctionBody = require('./AsyncFunctionBody');
  let params = [Yield, Await, Default];
  let { id, body } = given;
  let hasComma = checkComma(id.end, body.start, ')');
  let ps = FormalParameters(false, true)(given.params, hasComma);
  let b = AsyncFunctionBody(body);
  if (id) {
    let x = BindingIdentifier(Yield, Await)(id);
    return new Node('AsyncFunctionDeclaration', given, 0, [x, ps, b], params);
  } else {
    return new Node('AsyncFunctionDeclaration', given, 1, [ps, b], params);
  }
}

module.exports = AsyncFunctionDeclaration;

const Node = require('../Node');
const { checkComma } = require('../Global');

// FunctionDeclaration[Yield, Await, Default] :
//    function BindingIdentifier[?Yield, ?Await] ( FormalParameters[~Yield, ~Await] ) { FunctionBody[~Yield, ~Await] }
//    [+Default] function ( FormalParameters[~Yield, ~Await] ) { FunctionBody[~Yield, ~Await] }
let FunctionDeclaration = (Yield, Await, Default) => (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  const FormalParameters = require('./FormalParameters');
  const FunctionBody = require('./FunctionBody');
  let params = [Yield, Await, Default];
  let { id, body } = given;
  
  let hasComma = checkComma(id.end, body.start, ')');
  let ps = FormalParameters(false, false)(given.params, hasComma);
  let b = FunctionBody(false, false)(body);
  if (id) {
    let x = BindingIdentifier(Yield, Await)(id);
    return new Node('FunctionDeclaration', given, 0, [x, ps, b], params);
  } else {
    return new Node('FunctionDeclaration', given, 1, [ps, b], params);
  }
}

module.exports = FunctionDeclaration;

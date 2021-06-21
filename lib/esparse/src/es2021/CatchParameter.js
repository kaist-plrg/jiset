const Node = require('../Node');

// CatchParameter[Yield, Await] :
//    BindingIdentifier[?Yield, ?Await]
//    BindingPattern[?Yield, ?Await]
let CatchParameter = (Yield, Await) => (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  let params = [Yield, Await];
  let { type } = given;
  if (type == 'Identifier') {
    let id = BindingIdentifier(Yield, Await)(given);
    return new Node('CatchParameter', given, 0, [id], params);
  } else {
    Node.TODO(`${type} @ CatchParameter`);
  }
}

module.exports = CatchParameter;

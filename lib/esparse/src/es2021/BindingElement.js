const Node = require('../Node');

// BindingElement[Yield, Await] :
//    SingleNameBinding[?Yield, ?Await]
//    BindingPattern[?Yield, ?Await] Initializer[+In, ?Yield, ?Await]_opt
let BindingElement = (Yield, Await) => (given) => {
  const SingleNameBinding = require('./SingleNameBinding');
  let params = [Yield, Await];
  let { type } = given;
  switch (type) {
    case 'Identifier':
    case 'AssignmentPattern': {
      let single = SingleNameBinding(Yield, Await)(given);
      return new Node('BindingElement', given, 0, [single], params);
    }
    default:
      Node.TODO(`${type} @ BindingElement`);
  }
}

module.exports = BindingElement;

const Node = require('../Node');

// BindingElement[Yield, Await] :
//    SingleNameBinding[?Yield, ?Await]
//    BindingPattern[?Yield, ?Await] Initializer[+In, ?Yield, ?Await]_opt
let BindingElement = (Yield, Await) => (given) => {
  const SingleNameBinding = require('./SingleNameBinding');
  const BindingPattern = require('./BindingPattern');
  const Initializer = require('./Initializer');
  let params = [Yield, Await];
  let { type } = given;
  switch (type) {
    case 'Identifier': {
      let single = SingleNameBinding(Yield, Await)(given);
      return new Node('BindingElement', given, 0, [single], params);
    }
    case 'AssignmentPattern': {
      let { left, right } = given;
      let bind = BindingPattern(Yield, Await)(left);
      let init = Initializer(true, Yield, Await)(right);
      return new Node('BindingElement', given, 1, [bind, init], params);
    }
    case 'ObjectPattern': {
      let bind = BindingPattern(Yield, Await)(given);
      return new Node('BindingElement', given, 1, [bind, null], params);
    }
    default:
      Node.TODO(`${type} @ BindingElement`);
  }
}

module.exports = BindingElement;

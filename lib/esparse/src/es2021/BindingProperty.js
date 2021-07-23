const Node = require('../Node');

// BindingProperty[Yield, Await]: {
//   SingleNameBinding[?Yield, ?Await] |
//   PropertyName[?Yield, ?Await] `:` BindingElement[?Yield, ?Await]
// }

let BindingProperty = (Yield, Await) => (given) => {
  let SingleNameBinding = require('./SingleNameBinding');
  let PropertyName = require('./PropertyName');
  let BindingElement = require('./BindingElement');
  let params = [Yield, Await];
  
  let {shorthand, key, value} = given;
  // [0,0]
  if (shorthand) {
    let binding;
    if (key.type === 'Identifier' && value.type === 'Identifier' && key.name === value.name)
      binding = SingleNameBinding(Yield, Await)(key);
    else binding = SingleNameBinding(Yield, Await)({left:key, right: value});
    return new Node('BindingProperty', given, 0, [binding], params);
  } 
  // [1,0]
  else {
    let name = PropertyName(Yield, Await)(given);
    let elem = BindingElement(Yield, Await)(value);
    return new Node('BindingProperty', given, 1, [name, elem], params);
  }
}

module.exports = BindingProperty;

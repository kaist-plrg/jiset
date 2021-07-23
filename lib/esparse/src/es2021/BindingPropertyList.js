const Node = require('../Node');

// BindingPropertyList[Yield, Await]: {
//   BindingProperty[?Yield, ?Await] |
//   BindingPropertyList[?Yield, ?Await] `,` BindingProperty[?Yield, ?Await]
// }

// TODO check & handle span
let BindingPropertyList = (Yield, Await) => (given) => {
  let BindingProperty = require('./BindingProperty');
  let genChild = BindingProperty(Yield, Await);
  let params = [Yield, Await];
  return Node.fromList('BindingPropertyList', given, genChild, params);
}

module.exports = BindingPropertyList;

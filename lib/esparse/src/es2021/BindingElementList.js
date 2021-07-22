const Node = require('../Node');

// BindingElementList[Yield, Await]: {
//   BindingElisionElement[?Yield, ?Await] |
//   BindingElementList[?Yield, ?Await] `,` BindingElisionElement[?Yield, ?Await]
// }

// TODO check span info
let BindingElementList = (Yield, Await) => (given) => {
  let BindingElisionElement = require('./BindingElisionElement');
  let params = [Yield, Await];
  
  let elems = [given.pop()];
  for (let i = given.length - 1; i >= 0; i -= 1) {
    if (given[i] !== null) break;
    elems.unshift(given.pop());
  }
  
  if (given.length === 0) {
    let elisionElem = BindingElisionElement(Yield, Await)(elems);
    return new Node('BindingElementList', given, 0, [elisionElem], params);
  }
  else {
    if (elems[0] === null) elems.shift();
    let elisionElem = BindingElisionElement(Yield, Await)(elems);
    let listElem = BindingElementList(Yield, Await)(given);
    return new Node('BindingElementList', given, 1, [listElem, elisionElem], params);
  }
}

module.exports = BindingElementList;

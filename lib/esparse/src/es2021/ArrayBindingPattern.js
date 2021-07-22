const Node = require('../Node');

// ArrayBindingPattern[Yield, Await]: {
//   `[` Elision? BindingRestElement[?Yield, ?Await]? `]` |
//   `[` BindingElementList[?Yield, ?Await] `]` |
//   `[` BindingElementList[?Yield, ?Await] `,` Elision? BindingRestElement[?Yield, ?Await]? `]`
// }
let ArrayBindingPattern = (Yield, Await) => (given) => {
  let Elision = require('./Elision');
  let BindingElementList = require('./BindingElementList');
  let BindingRestElement = require('./BindingRestElement');
  
  let params = [Yield, Await];
  let elements = given.elements;
  
  if (elements.length === 0)
    return new Node('ArrayBindingPattern', given, 0, [null, null], params);
  else {
    //check if last element is BindingRestElement
    let lastElem = elements.pop();
    if (lastElem.type === 'RestElement') {
      let restElem = BindingRestElement(Yield, Await)(lastElem);
      let nullList = [];
      while (elements.length > 0) {
        let top = elements.pop();
        if (top == null) nullList.push(top);
        else {
          elements.push(top);
          break;
        }
      }
      let elision = null;
      if (nullList.length > 0) elision = Elision(nullList);

      if (elements.length === 0)
        return new Node('ArrayBindingPattern', given, 0, [elision, restElem], params);
      else {
        let listElem = BindingElementList(Yield, Await)(elements);
        return new Node('ArrayBindingPattern', given, 2, [listElem, elision, restElem], params);
      }
    } else {
      elements.push(lastElem);
      let listElem = BindingElementList(Yield, Await)(elements);
      return new Node('ArrayBindingPattern', given, 1, [listElem], params);
    }
  }
}

module.exports = ArrayBindingPattern;

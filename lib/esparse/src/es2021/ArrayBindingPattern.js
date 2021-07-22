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
  
  // [0,0], [0,2]
  if (elements.every(elem => elem === null)) {
    let elision = null;
    if (elements.length > 0) elision = Elision(elements);
    return new Node('ArrayBindingPattern', given, 0, [elision, null], params);
  }
  else {
    let lastElem = elements.pop();
    
    // [2,0], [2,2]
    if(lastElem === null) {
      let nullList = [];
      for (let i = elements.length; i >= 0 ; i -= 1) {
        if (elements[i] !== null) break;
        else nullList.push(elements.pop());
      }
      let elision = null;
      if (nullList.length > 0) elision = Elision(nullList);
      let elemList = BindingElementList(Yield, Await)(elements);
      return new Node('ArrayBindingPattern', given, 2, [elemList, elision, null], params);
    } 
    // [0,1], [0,3], [2,1], [2,3]
    else if ( lastElem.type === 'RestElement') {
      let restElem = BindingRestElement(Yield, Await)(lastElem);
      // [0,1], [0,3]
      if (elements.every(elem => elem === null)) {
        let elision = null;
        if (elements.length > 0) elision = Elision(elements);
        return new Node('ArrayBindingPattern', given, 0, [elision, restElem], params);
      } 
      // [2,1], [2,3]
      elements.pop();
      let nullList = [];
      for (let i = elements.length; i >= 0 ; i -= 1) {
        if (elements[i] !== null) break;
        else nullList.push(elements.pop());
      }
      let elision = null;
      if (nullList.length > 0) elision = Elision(nullList);
      let elemList = BindingElementList(Yield, Await)(elements);
      return new Node('ArrayBindingPattern', given, 2, [elemList, elision, restElem], params);
    } 
    // [1,0]
    else {
      elements.push(lastElem);
      let elemList = BindingElementList(Yield, Await)(elements);
      return new Node('ArrayBindingPattern', given, 1, [elemList], params);
    }
  }
}

module.exports = ArrayBindingPattern;

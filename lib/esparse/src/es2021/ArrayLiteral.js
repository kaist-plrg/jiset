const Node = require('../Node');
const { checkComma } = require('../Global');

// ArrayLiteral[Yield, Await] :
//    [ Elision_opt ]
//    [ ElementList[?Yield, ?Await] ]
//    [ ElementList[?Yield, ?Await] , Elision_opt ]
let ArrayLiteral = (Yield, Await) => (given) => {
  let Elision = require('./Elision');
  let ElementList = require('./ElementList');

  let params = [Yield, Await];
  let { elements } = given;
  let size = elements.length;
  
  if (size == 0) {
    return new Node('ArrayLiteral', given, 0, [null], params);
  } else if (elements.every(x => x == null)) {
    let elision = Elision(elements);
    return new Node('ArrayLiteral', given, 0, [elision], params);
  } else if (elements[size - 1] != null) {
    let list = ElementList(Yield, Await)(elements);
    const hasComma = checkComma(given.start, given.end, ']');
    if (hasComma) return new Node('ArrayLiteral', given, 2, [list, null], params);
    else return new Node('ArrayLiteral', given, 1, [list], params);
  } else {
    let nullList = [];
    for (let i = elements.length - 1; i >= 0 ; i -= 1) {
      if (elements[i] !== null) break;
      else nullList.push(elements.pop());
    }
    let elision = null;
    if (nullList.length > 0) elision = Elision(nullList);

    let list = ElementList(Yield, Await)(elements);
    return new Node('ArrayLiteral', given, 2, [list, elision], params);
  }
}

module.exports = ArrayLiteral;

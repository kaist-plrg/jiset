const Node = require('../Node');

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
    return new Node('ArrayLiteral', given, 1, [list], params);
  } else {
    Node.TODO('ArrayLiteral');
  }
}

module.exports = ArrayLiteral;

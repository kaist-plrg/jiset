const Node = require('../Node');

// ClassElementList[Yield, Await] :
//    ClassElement[?Yield, ?Await]
//    ClassElementList[?Yield, ?Await] ClassElement[?Yield, ?Await]
let ClassElementList = (Yield, Await) => (given) => {
  const ClassElement = require('./ClassElement');
  let genChild = ClassElement(Yield, Await);
  let params = [Yield, Await];
  return Node.fromList('ClassElementList', given.body, genChild, params);
}

module.exports = ClassElementList;

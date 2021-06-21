const Node = require('../Node');

// ElementList[Yield, Await] :
//    Elision_opt AssignmentExpression[+In, ?Yield, ?Await]
//    Elision_opt SpreadElement[?Yield, ?Await]
//    ElementList[?Yield, ?Await] , Elision_opt AssignmentExpression[+In, ?Yield, ?Await]
//    ElementList[?Yield, ?Await] , Elision_opt SpreadElement[?Yield, ?Await]
let ElementList = (Yield, Await) => (given) => {
  let Elision = require('./Elision');
  let SpreadElement = require('./SpreadElement');
  let AssignmentExpression = require('./AssignmentExpression');

  let params = [Yield, Await];
  let elem = given.pop();
  let nullList = [];
  while (given.length > 0) {
    let top = given.pop();
    if (top == null) nullList.push(top);
    else {
      given.push(top);
      break;
    }
  }

  let elision = null;
  if (nullList.length > 0) elision = Elision(nullList);

  if (given.length == 0) {
    if (elem.type != 'SpreadElement') {
      let expr = AssignmentExpression(true, Yield, Await)(elem);
      return new Node('ElementList', elem, 0, [elision, expr], params);
    } else {
      let expr = SpreadElement(Yield, Await)(elem);
      return new Node('ElementList', elem, 1, [elision, expr], params);
    }
  } else {
    if (elem.type != 'SpreadElement') {
      let list = ElementList(Yield, Await)(given);
      let expr = AssignmentExpression(true, Yield, Await)(elem);
      return new Node('ElementList', elem, 2, [list, elision, expr], params);
    } else {
      let list = ElementList(Yield, Await)(given);
      let expr = SpreadElement(Yield, Await)(elem);
      return new Node('ElementList', elem, 3, [list, elision, expr], params);
    }
  }
}

module.exports = ElementList;

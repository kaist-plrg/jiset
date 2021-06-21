const Node = require('../Node');

// BindingList[In, Yield, Await] :
//    LexicalBinding[?In, ?Yield, ?Await]
//    BindingList[?In, ?Yield, ?Await] , LexicalBinding[?In, ?Yield, ?Await]
let BindingList = (In, Yield, Await) => (given) => {
  const LexicalBinding = require('./LexicalBinding');
  let genChild = LexicalBinding(In, Yield, Await);
  let params = [In, Yield, Await];
  return Node.fromList('BindingList', given, genChild, params);
}

module.exports = BindingList;

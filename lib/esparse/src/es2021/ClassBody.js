const Node = require('../Node');

// ClassBody[Yield, Await] :
//    ClassElementList[?Yield, ?Await]
let ClassBody = (Yield, Await) => (given) => {
  const ClassElementList = require('./ClassElementList');
  let params = [Yield, Await];
  let list = ClassElementList(Yield, Await)(given);
  return new Node('ClassBody', given, 0, [list], params);
}

module.exports = ClassBody;

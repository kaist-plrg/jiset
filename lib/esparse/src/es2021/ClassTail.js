const Node = require('../Node');

// ClassTail[Yield, Await] :
//    ClassHeritage[?Yield, ?Await]_opt { ClassBody[?Yield, ?Await]_opt }
let ClassTail = (Yield, Await) => (given) => {
  const ClassHeritage = require('./ClassHeritage');
  const ClassBody = require('./ClassBody');
  let params = [Yield, Await];
  let { superClass, body } = given;
  let h = null;
  if (superClass) {
    h = ClassHeritage(Yield, Await)(superClass);
  }
  let b = null;
  if (body.body.length > 0) {
    b = ClassBody(Yield, Await)(body);
  }
  return new Node('ClassTail', given, 0, [h, b], params);
}

module.exports = ClassTail;

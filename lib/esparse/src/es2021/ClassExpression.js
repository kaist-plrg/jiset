const Node = require('../Node');

// ClassExpression[Yield, Await]: {
//   `class` BindingIdentifier[?Yield, ?Await]? ClassTail[?Yield, ?Await]
// }

let ClassExpression = (Yield, Await) => (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  const ClassTail = require('./ClassTail');
  let params = [Yield, Await];
  let { id, superClass, body } = given;

  let binding = null;
  if (id != null) binding = BindingIdentifier(Yield, Await)(id);
  // TODO handle span
  let tail = ClassTail(Yield, Await)({superClass, body});
  tail.start = given.body.start;
  tail.end = given.body.end;
  tail.loc = given.body.loc;
  return new Node('ClassExpression', given, 0, [binding, tail], params);
}

module.exports = ClassExpression;

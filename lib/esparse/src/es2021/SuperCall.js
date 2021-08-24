const Node = require('../Node');

// SuperCall[Yield, Await]: {
//   `super` Arguments[?Yield, ?Await]
// }

let SuperCall = (Yield, Await) => (given) => {
  const Arguments = require('./Arguments');
  let params = [Yield, Await];
  let { callee, arguments } = given;

  // set loc info for Arguments
  arguments.start = callee.end;
  arguments.end = given.end;
  arguments.loc = given.loc;
  arguments.loc.start = callee.loc.end;

  const args = Arguments(Yield, Await)(arguments);
  return new Node('SuperCall', given, 0, [args], params);
}

module.exports = SuperCall;

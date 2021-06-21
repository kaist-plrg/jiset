const Node = require('../Node');

// BreakStatement[Yield, Await] :
//    break ;
//    break [no LineTerminator here] LabelIdentifier[?Yield, ?Await] ;
let BreakStatement = (Yield, Await) => (given) => {
  const LabelIdentifier = require('./LabelIdentifier');
  let params = [Yield, Await];
  let { label } = given;
  if (label == null) {
    return new Node('BreakStatement', given, 0, [], params);
  } else {
    let x = LabelIdentifier(Yield, Await)(label);
    return new Node('BreakStatement', given, 0, [x], params);
  }
}

module.exports = BreakStatement;

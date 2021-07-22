const Node = require('../Node');

// BindingPattern[Yield, Await]: {
//   ObjectBindingPattern[?Yield, ?Await] |
//   ArrayBindingPattern[?Yield, ?Await]
// }
let BindingPattern = (Yield, Await) => (given) => {
  let ObjectBindingPattern = require('./ObjectBindingPattern');
  let ArrayBindingPattern = require('./ArrayBindingPattern');
  
  let params = [Yield, Await];
  if (given.type === 'ArrayPattern') {
    let pat = ArrayBindingPattern(Yield, Await)(given);
    return new Node('BindingPattern', given, 1, [pat], params);
  } else {
    let pat = ObjectBindingPattern(Yield, Await)(given);
    return new Node('BindingPattern', given, 0, [pat], params);
  }
}

module.exports = BindingPattern;

const Node = require('../Node');

// AsyncConciseBody[In]: {
//   [lookahead <! {`{`}] ExpressionBody[?In, +Await] |
//   `{` AsyncFunctionBody `}`
// }

let AsyncConciseBody = (In) => (given) => {
  const ExpressionBody = require('./ExpressionBody');
  const AsyncFunctionBody = require('./AsyncFunctionBody');
  
  let params = [In];

  // [1,0]
  if (given.body) {
    // TODO fix span ??
    let b = AsyncFunctionBody(given);
    return new Node('AsyncConciseBody', given, 1, [b], params);
  }
  // [0,0]
  else {
    let expr = ExpressionBody(In, true)(given);
    return new Node('AsyncConciseBody', given, 0, [expr], params);
  }
}

module.exports = AsyncConciseBody;

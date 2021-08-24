const Node = require('../Node');
const {checkCoverCallExpressionAndAsyncArrowHead} = require('../Global');

// AsyncArrowFunction[In, Yield, Await]: {
//   `async` [no LineTerminator here] AsyncArrowBindingIdentifier[?Yield] [no LineTerminator here] `=>` AsyncConciseBody[?In] |
//   CoverCallExpressionAndAsyncArrowHead[?Yield, ?Await] [no LineTerminator here] `=>` AsyncConciseBody[?In]
// }

let AsyncArrowFunction = (In, Yield, Await) => (given) => {
  const AsyncArrowBindingIdentifier = require('./AsyncArrowBindingIdentifier');
  const AsyncConciseBody = require('./AsyncConciseBody');
  const CoverCallExpressionAndAsyncArrowHead = require('./CoverCallExpressionAndAsyncArrowHead');
  
  const isHead = checkCoverCallExpressionAndAsyncArrowHead(given.start, given.body.start);

  let params = [In, Yield, Await];
  let b = AsyncConciseBody(In)(given.body);
  
  if (isHead) {
    // create head given
    // TODO fix span
    let headGiven = {
      callee: { "type": "Identifier", "name": "async" },
      arguments: given.params,
    }
    let head = CoverCallExpressionAndAsyncArrowHead(Yield, Await)(headGiven);
    head.start = given.start;
    head.end = given.body.start;
    head.loc = given.loc;
    head.loc.end = given.body.loc.start;
    return new Node('AsyncArrowFunction', given, 1, [head, b], params);
  } else {
    let id = AsyncArrowBindingIdentifier(Yield)(given.params[0]);
    return new Node('AsyncArrowFunction', given, 0, [id, b], params);
  }
}

module.exports = AsyncArrowFunction;

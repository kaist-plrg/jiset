const Node = require('../Node');

// ConciseBody[In] :
//    [lookahead ≠ {] ExpressionBody[?In, ~Await]
//    { FunctionBody[~Yield, ~Await] }
let ConciseBody = (In) => (given) => {
  const ExpressionBody = require('./ExpressionBody');
  const FunctionBody = require('./FunctionBody');
  let params = [In];
  let { type } = given;
  if (type != 'BlockStatement') {
    let body = ExpressionBody(In, false)(given);
    return new Node('ConciseBody', given, 0, [body], params);
  } else {
    let body = FunctionBody(false, false)(given);
    return new Node('ConciseBody', given, 1, [body], params);
  }
}

module.exports = ConciseBody;

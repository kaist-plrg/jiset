const Node = require('../Node');

// ForInOfStatement[Yield, Await, Return] :
//    for ( [lookahead ≠ let [] LeftHandSideExpression[?Yield, ?Await] in Expression[+In, ?Yield, ?Await] ) Statement[?Yield, ?Await, ?Return]
//    for ( var ForBinding[?Yield, ?Await] in Expression[+In, ?Yield, ?Await] ) Statement[?Yield, ?Await, ?Return]
//    for ( ForDeclaration[?Yield, ?Await] in Expression[+In, ?Yield, ?Await] ) Statement[?Yield, ?Await, ?Return]
//    for ( [lookahead ∉ { let, async of }] LeftHandSideExpression[?Yield, ?Await] of AssignmentExpression[+In, ?Yield, ?Await] ) Statement[?Yield, ?Await, ?Return]
//    for ( var ForBinding[?Yield, ?Await] of AssignmentExpression[+In, ?Yield, ?Await] ) Statement[?Yield, ?Await, ?Return]
//    for ( ForDeclaration[?Yield, ?Await] of AssignmentExpression[+In, ?Yield, ?Await] ) Statement[?Yield, ?Await, ?Return]
//    [+Await] for await ( [lookahead ≠ let] LeftHandSideExpression[?Yield, ?Await] of AssignmentExpression[+In, ?Yield, ?Await] ) Statement[?Yield, ?Await, ?Return]
//    [+Await] for await ( var ForBinding[?Yield, ?Await] of AssignmentExpression[+In, ?Yield, ?Await] ) Statement[?Yield, ?Await, ?Return]
//    [+Await] for await ( ForDeclaration[?Yield, ?Await] of AssignmentExpression[+In, ?Yield, ?Await] ) Statement[?Yield, ?Await, ?Return]
let ForInOfStatement = (Yield, Await, Return) => (given) => {
  const LeftHandSideExpression = require('./LeftHandSideExpression');
  const Expression = require('./Expression');
  const Statement = require('./Statement');
  const ForBinding = require('./ForBinding');
  const ForDeclaration = require('./ForDeclaration');
  const AssignmentExpression = require('./AssignmentExpression');
  let params = [Yield, Await, Return];
  let { type, await, left, right, body } = given;
  let index, l, r, b;
  if (left.type != 'VariableDeclaration') {
    index = 0;
    l = LeftHandSideExpression(Yield, Await)(left);
  } else if (left.kind == 'var') {
    index = 1;
    l = ForBinding(Yield, Await)(left.declarations[0]);
  } else {
    index = 2;
    l = ForDeclaration(Yield, Await)(left);
  }
  if (type == 'ForOfStatement') {
    index += 3;
    r = AssignmentExpression(true, Yield, Await)(right);
  } else {
    r = Expression(true, Yield, Await)(right);
  }
  if (await) index += 3;
  b = Statement(Yield, Await, Return)(body);
  return new Node('ForInOfStatement', given, index, [l, r, b], params);
}

module.exports = ForInOfStatement;

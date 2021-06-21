const Node = require('../Node');

// CoverParenthesizedExpressionAndArrowParameterList[Yield, Await] :
//    ( Expression[+In, ?Yield, ?Await] )
//    ( Expression[+In, ?Yield, ?Await] , )
//    ( )
//    ( ... BindingIdentifier[?Yield, ?Await] )
//    ( ... BindingPattern[?Yield, ?Await] )
//    ( Expression[+In, ?Yield, ?Await] , ... BindingIdentifier[?Yield, ?Await] )
//    ( Expression[+In, ?Yield, ?Await] , ... BindingPattern[?Yield, ?Await] )
let CoverParenthesizedExpressionAndArrowParameterList = (Yield, Await) => (given) => {
  const Expression = require('./Expression');
  const BindingIdentifier = require('./BindingIdentifier');
  const BindingPattern = require('./BindingPattern');
  let name = 'CoverParenthesizedExpressionAndArrowParameterList';
  let params = [Yield, Await];
  if (given.type == 'ParenthesizedExpression') {
    given = given.expression;
    if (given.type == 'SequenceExpression') {
      given = given.expressions;
    } else {
      given = [given];
    }
  }
  if (given.type == 'SequenceExpression') {
    given = given.expressions;
  }
  let size = given.length;
  if (size == 0) {
    return new Node(name, {}, 2, [], params);
  }
  let last = given[size-1];
  if (last.type == 'RestElement') {
    if (size == 1) {
      if (last.argument.type == 'Identifier') {
        let x = BindingIdentifier(Yield, Await)(last.argument);
        return new Node(name, last, 3, [x], params);
      } else {
        let p = BindingPattern(Yield, Await)(last.argument);
        return new Node(name, last, 4, [p], params);
      }
    } else {
      if (last.argument.type == 'Identifier') {
        Node.TODO(`${name}[5]`);
      } else {
        Node.TODO(`${name}[6]`);
      }
    }
  } else {
    let expr = Expression(true, Yield, Await)(given);
    return new Node(name, {}, 0, [expr], params)
  }
}

module.exports = CoverParenthesizedExpressionAndArrowParameterList;

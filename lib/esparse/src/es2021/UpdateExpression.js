const Node = require('../Node');

// UpdateExpression[Yield, Await] :
//    LeftHandSideExpression[?Yield, ?Await]
//    LeftHandSideExpression[?Yield, ?Await] [no LineTerminator here] ++
//    LeftHandSideExpression[?Yield, ?Await] [no LineTerminator here] --
//    ++ UnaryExpression[?Yield, ?Await]
//    -- UnaryExpression[?Yield, ?Await]
let UpdateExpression = (Yield, Await) => (given) => {
  const LeftHandSideExpression = require('./LeftHandSideExpression');
  const UnaryExpression = require('./UnaryExpression');
  let params = [Yield, Await];
  let { type } = given;
  switch (type) {
    case 'CallExpression':
    case 'NewExpression':
    case 'MemberExpression':
    case 'ObjectExpression':
    case 'ArrayExpression':
    case 'FunctionExpression':
    case 'ClassExpression':
    case 'SequenceExpression':
    case 'ParenthesizedExpression':
    case 'TemplateLiteral':
    case 'Identifier':
    case 'ThisExpression':
    case 'Literal': {
      let expr = LeftHandSideExpression(Yield, Await)(given);
      return new Node('UpdateExpression', given, 0, [expr], params);
    }
    case 'UpdateExpression': {
      let { prefix, operator, argument } = given;
      let arg, index;
      if (!prefix) {
        arg = LeftHandSideExpression(Yield, Await)(argument);
        if (operator == '++') index = 1;
        else if (operator == '--') index = 2;
        else Node.TODO(`x${operator} @ UpdateExpression`);
      } else {
        arg = UnaryExpression(Yield, Await)(argument);
        if (operator == '++') index = 3;
        else if (operator == '--') index = 4;
        else Node.TODO(`${operator}x @ UpdateExpression`);
      }
      return new Node('UpdateExpression', given, index, [arg], params);
    }
    default:
      Node.TODO(`${type} @ UpdateExpression`);
  }
  Node.TODO('UpdateExpression');
}

module.exports = UpdateExpression;

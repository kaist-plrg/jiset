const Node = require('../Node');

// ConditionalExpression[In, Yield, Await] :
//    ShortCircuitExpression[?In, ?Yield, ?Await]
//    ShortCircuitExpression[?In, ?Yield, ?Await] ? AssignmentExpression[+In, ?Yield, ?Await] : AssignmentExpression[?In, ?Yield, ?Await]
let ConditionalExpression = (In, Yield, Await) => (given) => {
  let ShortCircuitExpression = require('./ShortCircuitExpression');

  let params = [In, Yield, Await];
  let { type } = given;
  switch (type) {
    case 'AwaitExpression':
    case 'UnaryExpression':
    case 'LogicalExpression':
    case 'BinaryExpression':
    case 'UpdateExpression':
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
      let circ = ShortCircuitExpression(In, Yield, Await)(given);
      return new Node('ConditionalExpression', given, 0, [circ], params);
    }
    default:
      Node.TODO(`${type} @ ConditionalExpression`);
  }
  Node.TODO('ConditionalExpression');
}

module.exports = ConditionalExpression;

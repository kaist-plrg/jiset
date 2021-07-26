const Node = require('../Node');

// LogicalANDExpression[In, Yield, Await] :
//    BitwiseORExpression[?In, ?Yield, ?Await]
//    LogicalANDExpression[?In, ?Yield, ?Await] && BitwiseORExpression[?In, ?Yield, ?Await]
let LogicalANDExpression = (In, Yield, Await) => (given) => {
  let BitwiseORExpression = require('./BitwiseORExpression');

  let params = [In, Yield, Await];
  let { type } = given;
  switch (type) {
    case 'AwaitExpression':
    case 'UnaryExpression':
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
    case 'ArrayPattern':
    case 'ObjectPattern':
    case 'MetaProperty':
    case 'Literal': {
      let expr = BitwiseORExpression(In, Yield, Await)(given);
      return new Node('LogicalANDExpression', given, 0, [expr], params);
    }
    case 'LogicalExpression': {
      let { operator, left, right } = given;
      if (operator == '&&') {
        let l = LogicalANDExpression(In, Yield, Await)(left);
        let r = BitwiseORExpression(In, Yield, Await)(right);
        return new Node('LogicalORExpression', given, 1, [l, r], params);
      } else {
        let expr = BitwiseORExpression(In, Yield, Await)(given);
        return new Node('LogicalANDExpression', given, 0, [expr], params);
      }
    }
    default:
      Node.TODO(`${type} @ LogicalANDExpression`);
  }
  Node.TODO('LogicalANDExpression');
}

module.exports = LogicalANDExpression;

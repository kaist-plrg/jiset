const Node = require('../Node');

// BitwiseXORExpression[In, Yield, Await] :
//    BitwiseANDExpression[?In, ?Yield, ?Await]
//    BitwiseXORExpression[?In, ?Yield, ?Await] ^ BitwiseANDExpression[?In, ?Yield, ?Await]
let BitwiseXORExpression = (In, Yield, Await) => (given) => {
  let BitwiseANDExpression = require('./BitwiseANDExpression');

  let params = [In, Yield, Await];
  let { type } = given;
  switch (type) {
    case 'AwaitExpression':
    case 'UnaryExpression':
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
      let expr = BitwiseANDExpression(In, Yield, Await)(given);
      return new Node('BitwiseXORExpression', given, 0, [expr], params);
    }
    case 'BinaryExpression': {
      let { index, children } = Node.getBinary(given, ['^'],
        BitwiseXORExpression(In, Yield, Await),
        BitwiseANDExpression(In, Yield, Await),
      );
      return new Node('BitwiseXORExpression', given, index, children, params);
    }
    default:
      Node.TODO(`${type} @ BitwiseXORExpression`);
  }
  Node.TODO('BitwiseXORExpression');
}

module.exports = BitwiseXORExpression;

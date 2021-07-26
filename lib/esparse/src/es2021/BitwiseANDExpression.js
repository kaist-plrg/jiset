const Node = require('../Node');

// BitwiseANDExpression[In, Yield, Await] :
//    EqualityExpression[?In, ?Yield, ?Await]
//    BitwiseANDExpression[?In, ?Yield, ?Await] & EqualityExpression[?In, ?Yield, ?Await]
let BitwiseANDExpression = (In, Yield, Await) => (given) => {
  let EqualityExpression = require('./EqualityExpression');

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
    case 'TaggedTemplateExpression':
    case 'Identifier':
    case 'ThisExpression':
    case 'ArrayPattern':
    case 'ObjectPattern':
    case 'MetaProperty':
    case 'Literal': {
      let expr = EqualityExpression(In, Yield, Await)(given);
      return new Node('BitwiseANDExpression', given, 0, [expr], params);
    }
    case 'BinaryExpression': {
      let { index, children } = Node.getBinary(given, ['&'],
        BitwiseANDExpression(In, Yield, Await),
        EqualityExpression(In, Yield, Await),
      );
      return new Node('BitwiseANDExpression', given, index, children, params);
    }
    default:
      Node.TODO(`${type} @ BitwiseANDExpression`);
  }
  Node.TODO('BitwiseANDExpression');
}

module.exports = BitwiseANDExpression;

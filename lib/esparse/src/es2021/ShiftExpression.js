const Node = require('../Node');

// ShiftExpression[Yield, Await] :
//    AdditiveExpression[?Yield, ?Await]
//    ShiftExpression[?Yield, ?Await] << AdditiveExpression[?Yield, ?Await]
//    ShiftExpression[?Yield, ?Await] >> AdditiveExpression[?Yield, ?Await]
//    ShiftExpression[?Yield, ?Await] >>> AdditiveExpression[?Yield, ?Await]
let ShiftExpression = (Yield, Await) => (given) => {
  let AdditiveExpression = require('./AdditiveExpression');

  let params = [Yield, Await];
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
      let expr = AdditiveExpression(Yield, Await)(given);
      return new Node('ShiftExpression', given, 0, [expr], params);
    }
    case 'BinaryExpression': {
      let { index, children } = Node.getBinary(
        given,
        ['<<', '>>', '>>>'],
        ShiftExpression(Yield, Await),
        AdditiveExpression(Yield, Await),
      );
      return new Node('ShiftExpression', given, index, children, params);
    }
    default:
      Node.TODO(`${type} @ ShiftExpression`);
  }
  Node.TODO('ShiftExpression');
}

module.exports = ShiftExpression;

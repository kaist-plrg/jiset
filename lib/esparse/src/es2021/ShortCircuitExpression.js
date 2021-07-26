const Node = require('../Node');

// ShortCircuitExpression[In, Yield, Await] :
//    LogicalORExpression[?In, ?Yield, ?Await]
//    CoalesceExpression[?In, ?Yield, ?Await]
let ShortCircuitExpression = (In, Yield, Await) => (given) => {
  let LogicalORExpression = require('./LogicalORExpression');

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
    case 'TaggedTemplateExpression':
    case 'Identifier':
    case 'ThisExpression':
    case 'ArrayPattern':
    case 'ObjectPattern':
    case 'MetaProperty':
    case 'Literal': {
      let expr = LogicalORExpression(In, Yield, Await)(given);
      return new Node('ShortCircuitExpression', given, 0, [expr], params);
    }
    default:
      Node.TODO(`${type} @ ShortCircuitExpression`);
  }
  Node.TODO('ShortCircuitExpression');
}

module.exports = ShortCircuitExpression;

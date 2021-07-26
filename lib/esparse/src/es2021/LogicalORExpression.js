const Node = require('../Node');

// LogicalORExpression[In, Yield, Await] :
//    LogicalANDExpression[?In, ?Yield, ?Await]
//    LogicalORExpression[?In, ?Yield, ?Await] || LogicalANDExpression[?In, ?Yield, ?Await]
let LogicalORExpression = (In, Yield, Await) => (given) => {
  let LogicalANDExpression = require('./LogicalANDExpression');

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
    case 'TaggedTemplateExpression':
    case 'Identifier':
    case 'ThisExpression':
    case 'ArrayPattern':
    case 'ObjectPattern':
    case 'MetaProperty':
    case 'Literal': {
      let expr = LogicalANDExpression(In, Yield, Await)(given);
      return new Node('LogicalORExpression', given, 0, [expr], params);
    }
    case 'LogicalExpression': {
      let { operator, left, right } = given;
      if (operator == '||') {
        let l = LogicalORExpression(In, Yield, Await)(left);
        let r = LogicalANDExpression(In, Yield, Await)(right);
        return new Node('LogicalORExpression', given, 1, [l, r], params);
      } else {
        let expr = LogicalANDExpression(In, Yield, Await)(given);
        return new Node('LogicalORExpression', given, 0, [expr], params);
      }
    }
    default:
      Node.TODO(`${type} @ LogicalORExpression`);
  }
  Node.TODO('LogicalORExpression');
}

module.exports = LogicalORExpression;

const Node = require('../Node');

// NewExpression[Yield, Await] :
//    MemberExpression[?Yield, ?Await]
//    new NewExpression[?Yield, ?Await]
let NewExpression = (Yield, Await) => (given) => {
  let MemberExpression = require('./MemberExpression');

  let params = [Yield, Await];
  let { type } = given;
  switch (type) {
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
    case 'ObjectPattern':
    case 'ArrayPattern':
    case 'MetaProperty':
    case 'Literal': {
      let expr = MemberExpression(Yield, Await)(given);
      return new Node('NewExpression', given, 0, [expr], params);
    }
    case 'NewExpression': {
      let { callee } = given;
      if (callee.end != given.end) {
        let expr = MemberExpression(Yield, Await)(given);
        return new Node('NewExpression', given, 0, [expr], params);
      } else {
        let expr = NewExpression(Yield, Await)(callee);
        return new Node('NewExpression', given, 1, [expr], params);
      }
    }
    default:
      Node.TODO(`${type} @ NewExpression`);
  }
  Node.TODO('NewExpression');
}

module.exports = NewExpression;

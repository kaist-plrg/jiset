const Node = require('../Node');

// LeftHandSideExpression[Yield, Await] :
//    NewExpression[?Yield, ?Await]
//    CallExpression[?Yield, ?Await]
//    OptionalExpression[?Yield, ?Await]
let LeftHandSideExpression = (Yield, Await) => (given) => {
  const NewExpression = require('./NewExpression');
  const CallExpression = require('./CallExpression');
  let params = [Yield, Await];
  let { type } = given;
  switch (type) {
    case 'MetaProperty':
    case 'NewExpression': {
      let expr = NewExpression(Yield, Await)(given);
      return new Node('LeftHandSideExpression', given, 0, [expr], params);
    }
    case 'MemberExpression': {
      let cur = given;
      while (cur?.type == 'MemberExpression') { cur = cur.object; }
      let expr, index;
      if (cur?.type == 'CallExpression') {
        index = 1;
        expr = CallExpression(Yield, Await)(given);
      } else {
        index = 0;
        expr = NewExpression(Yield, Await)(given);
      }
      return new Node('LeftHandSideExpression', given, index, [expr], params);
    }
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
    case 'Literal': {
      let expr = NewExpression(Yield, Await)(given);
      return new Node('LeftHandSideExpression', given, 0, [expr], params);
    }
    case 'CallExpression': {
      let expr = CallExpression(Yield, Await)(given);
      return new Node('LeftHandSideExpression', given, 1, [expr], params);
    }
    case 'ObjectPattern':
    case 'ArrayPattern': {
      // change array pattern to lhs expression form
      let expr = NewExpression(Yield, Await)(given);
      return new Node('LeftHandSideExpression', given, 0, [expr], params);
    }
    default:
      Node.TODO(`${type} @ LeftHandSideExpression`);
  }
  Node.TODO('LeftHandSideExpression');
}

module.exports = LeftHandSideExpression;

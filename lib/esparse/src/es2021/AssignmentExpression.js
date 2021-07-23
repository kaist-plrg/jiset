const Node = require('../Node');

// AssignmentExpression[In, Yield, Await] :
//    ConditionalExpression[?In, ?Yield, ?Await]
//    [+Yield]YieldExpression[?In, ?Await]
//    ArrowFunction[?In, ?Yield, ?Await]
//    AsyncArrowFunction[?In, ?Yield, ?Await]
//    LeftHandSideExpression[?Yield, ?Await] = AssignmentExpression[?In, ?Yield, ?Await]
//    LeftHandSideExpression[?Yield, ?Await] AssignmentOperator AssignmentExpression[?In, ?Yield, ?Await]
//    LeftHandSideExpression[?Yield, ?Await] &&= AssignmentExpression[?In, ?Yield, ?Await]
//    LeftHandSideExpression[?Yield, ?Await] ||= AssignmentExpression[?In, ?Yield, ?Await]
//    LeftHandSideExpression[?Yield, ?Await] ??= AssignmentExpression[?In, ?Yield, ?Await]
let AssignmentExpression = (In, Yield, Await) => (given) => {
  const ConditionalExpression = require('./ConditionalExpression');
  const YieldExpression = require('./YieldExpression');
  const ArrowFunction = require('./ArrowFunction');
  const AsyncArrowFunction = require('./AsyncArrowFunction');
  const LeftHandSideExpression = require('./LeftHandSideExpression');
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
    case 'ArrayPattern':
    case 'Literal': {
      let cond = ConditionalExpression(In, Yield, Await)(given);
      return new Node('AssignmentExpression', given, 0, [cond], params);
    }
    case 'YieldExpression': {
      let expr = YieldExpression(In, Await)(given);
      return new Node('AssignmentExpression', given, 1, [expr], params);
    }
    case 'ArrowFunctionExpression': {
      if (!given.async) {
        let arrow = ArrowFunction(In, Yield, Await)(given);
        return new Node('AssignmentExpression', given, 2, [arrow], params);
      } else {
        let arrow = AsyncArrowFunction(In, Yield, Await)(given);
        return new Node('AssignmentExpression', given, 3, [arrow], params);
      }
    }
    case 'AssignmentPattern': {
      const { left, right } = given;
      const lhs = LeftHandSideExpression(Yield, Await)(left);
      const expr = AssignmentExpression(In, Yield, Await)(right);
      return new Node('AssignmentExpression', given, 4, [lhs, expr], params);
    }
    case 'AssignmentExpression': {
      let { left, operator, right } = given;
      let lhs = LeftHandSideExpression(Yield, Await)(left);
      let expr = AssignmentExpression(In, Yield, Await)(right);
      let node = new Node('AssignmentExpression', given, 4, [lhs, expr], params);
      switch (operator) {
        case '=': break;
        case '&&=': node.index = 6; break;
        case '||=': node.index = 7; break;
        case '??=': node.index = 8; break;
        default: {
          let ops = ['*=', '/=', '%=', '+=', '-=',
            '<<=', '>>=', '>>>=', '&=', '^=', '|=', '**='];
          let index = ops.indexOf(operator);
          let op = new Node('AssignmentOperator', {}, index);
          node.index = 5;
          node.children = [lhs, op, expr];
        }
      }
      return node;
    }
    default:
      Node.TODO(`${type} @ AssignmentExpression`);
  }
}

module.exports = AssignmentExpression;

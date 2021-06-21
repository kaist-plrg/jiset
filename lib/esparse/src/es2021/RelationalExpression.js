const Node = require('../Node');

// RelationalExpression[In, Yield, Await] :
//    ShiftExpression[?Yield, ?Await]
//    RelationalExpression[?In, ?Yield, ?Await] < ShiftExpression[?Yield, ?Await]
//    RelationalExpression[?In, ?Yield, ?Await] > ShiftExpression[?Yield, ?Await]
//    RelationalExpression[?In, ?Yield, ?Await] <= ShiftExpression[?Yield, ?Await]
//    RelationalExpression[?In, ?Yield, ?Await] >= ShiftExpression[?Yield, ?Await]
//    RelationalExpression[?In, ?Yield, ?Await] instanceof ShiftExpression[?Yield, ?Await]
//    [+In] RelationalExpression[+In, ?Yield, ?Await] in ShiftExpression[?Yield, ?Await]
let RelationalExpression = (In, Yield, Await) => (given) => {
  let ShiftExpression = require('./ShiftExpression');

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
      let expr = ShiftExpression(Yield, Await)(given);
      return new Node('RelationalExpression', given, 0, [expr], params);
    }
    case 'BinaryExpression': {
      if (given.operator == 'in') {
        let { left, right } = given;
        let l = RelationalExpression(true, Yield, Await)(left);
        let r = ShiftExpression(Yield, Await)(right);
        return new Node('RelationalExpression', given, 6, [l, r], params);
      } else {
        let { index, children } = Node.getBinary(
          given,
          ['<', '>', '<=', '>=', 'instanceof'],
          RelationalExpression(In, Yield, Await),
          ShiftExpression(Yield, Await),
        );
        return new Node('RelationalExpression', given, index, children, params);
      }
    }
    default:
      Node.TODO(`${type} @ RelationalExpression`);
  }
  Node.TODO('RelationalExpression');
}

module.exports = RelationalExpression;

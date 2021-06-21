const Node = require('../Node');

// ExponentiationExpression[Yield, Await] :
//    UnaryExpression[?Yield, ?Await]
//    UpdateExpression[?Yield, ?Await] ** ExponentiationExpression[?Yield, ?Await]
let ExponentiationExpression = (Yield, Await) => (given) => {
  let UnaryExpression = require('./UnaryExpression');
  let UpdateExpression = require('./UpdateExpression');

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
    case 'Identifier':
    case 'ThisExpression':
    case 'Literal': {
      let expr = UnaryExpression(Yield, Await)(given);
      return new Node('ExponentiationExpression', given, 0, [expr], params);
    }
    case 'BinaryExpression': {
      let { operator, left, right } = given;
      if (operator == '**') {
        let l = UpdateExpression(Yield, Await)(left);
        let r = ExponentiationExpression(Yield, Await)(right);
        return new Node('ExponentiationExpression', given, 1, [l, r], params);
      }
      let expr = UnaryExpression(Yield, Await)(given);
      return new Node('ExponentiationExpression', given, 0, [expr], params);
    }
    default:
      Node.TODO(`${type} @ ExponentiationExpression`);
  }
  Node.TODO('ExponentiationExpression');
}

module.exports = ExponentiationExpression;

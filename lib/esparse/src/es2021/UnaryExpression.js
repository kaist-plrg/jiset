const Node = require('../Node');

// UnaryExpression[Yield, Await] :
//    UpdateExpression[?Yield, ?Await]
//    delete UnaryExpression[?Yield, ?Await]
//    void UnaryExpression[?Yield, ?Await]
//    typeof UnaryExpression[?Yield, ?Await]
//    + UnaryExpression[?Yield, ?Await]
//    - UnaryExpression[?Yield, ?Await]
//    ~ UnaryExpression[?Yield, ?Await]
//    ! UnaryExpression[?Yield, ?Await]
//    [+Await]AwaitExpression[?Yield]
let UnaryExpression = (Yield, Await) => (given) => {
  let UpdateExpression = require('./UpdateExpression');
  let AwaitExpression = require('./AwaitExpression');

  let params = [Yield, Await];
  let { type } = given;
  switch (type) {
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
      let expr = UpdateExpression(Yield, Await)(given);
      return new Node('UnaryExpression', given, 0, [expr], params);
    }
    case 'UnaryExpression': {
      let index;
      let { operator, argument } = given;
      switch (operator) {
        case 'delete': index = 1; break;
        case 'void': index = 2; break;
        case 'typeof': index = 3; break;
        case '+': index = 4; break;
        case '-': index = 5; break;
        case '~': index = 6; break;
        case '!': index = 7; break;
        default: throw `[Error] unknown unary operator: ${operator}`;
      }
      let expr = UnaryExpression(Yield, Await)(argument);
      return new Node('UnaryExpression', given, index, [expr], params);
    }
    case 'AwaitExpression': {
      let expr = AwaitExpression(Yield)(given);
      return new Node('UnaryExpression', given, 8, [expr], params);
    }
    default:
      Node.TODO(`${type} @ UnaryExpression`);
  }
  Node.TODO('UnaryExpression');
}

module.exports = UnaryExpression;

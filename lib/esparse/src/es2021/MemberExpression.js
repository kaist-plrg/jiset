const Node = require('../Node');

// MemberExpression[Yield, Await] :
//    PrimaryExpression[?Yield, ?Await]
//    MemberExpression[?Yield, ?Await] [ Expression[+In, ?Yield, ?Await] ]
//    MemberExpression[?Yield, ?Await] . IdentifierName
//    MemberExpression[?Yield, ?Await] TemplateLiteral[?Yield, ?Await, +Tagged]
//    SuperProperty[?Yield, ?Await]
//    MetaProperty
//    new MemberExpression[?Yield, ?Await] Arguments[?Yield, ?Await]
let MemberExpression = (Yield, Await) => (given) => {
  const PrimaryExpression = require('./PrimaryExpression');
  const CallExpression = require('./CallExpression');
  const Expression = require('./Expression');
  const SuperProperty = require('./SuperProperty');
  const MetaProperty = require('./MetaProperty');
  const Arguments = require('./Arguments');
  let params = [Yield, Await];
  let { type } = given;
  switch (type) {
    case 'ObjectExpression':
    case 'ArrayExpression':
    case 'FunctionExpression':
    case 'ClassExpression':
    case 'SequenceExpression':
    case 'ParenthesizedExpression':
    case 'TemplateLiteral':
    case 'Identifier':
    case 'ThisExpression':
    case 'ObjectPattern':
    case 'ArrayPattern':
    case 'Literal': {
      let expr = PrimaryExpression(Yield, Await)(given);
      return new Node('MemberExpression', given, 0, [expr], params);
    }
    case 'NewExpression': {
      let member = MemberExpression(Yield, Await)(given.callee);
      let args = Arguments(Yield, Await)(given.arguments);
      return new Node('MemberExpression', given, 6, [member, args], params);
    }
    case 'MetaProperty': {
      let meta = MetaProperty(given);
      return new Node('MemberExpression', given, 5, [meta], params);
    }
    case 'MemberExpression': {
      let { object, meta, property, computed } = given;
      if (meta) {
        let metaProp = MetaProperty(Yield, Await)(given);
        return new Node('MemberExpression', given, 5, [metaProp], params);
      } else if (object && object.type == 'Super') {
        let superProp = SuperProperty(Yield, Await)(given);
        return new Node('MemberExpression', given, 4, [superProp], params);
      } else if (object && object.type === 'CallExpression') {
        if (computed)
          Node.TODO('computed @ MemberExpression');

        // e.g. g.next().value
        return CallExpression(Yield, Await)(given);
      } else if (object && computed) {
        let member = MemberExpression(Yield, Await)(object);
        let expr = Expression(true, Yield, Await)(property);
        return new Node('MemberExpression', given, 1, [member, expr], params);
      } else if (object && !computed) {
        let member = MemberExpression(Yield, Await)(object);
        let name = property.name;
        return new Node('MemberExpression', given, 2, [member, name], params);
      } else {
        Node.TODO('MemberExpression');
      }
      break;
    }
    default:
      Node.TODO(`${type} @ MemberExpression`);
  }
}

module.exports = MemberExpression;

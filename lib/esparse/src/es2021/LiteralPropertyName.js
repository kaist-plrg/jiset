const Node = require('../Node');

// LiteralPropertyName :
//    IdentifierName
//    StringLiteral
//    NumericLiteral
let LiteralPropertyName = (given) => {
  let { type, name, value, raw } = given;
  if (type == 'Identifier') {
    return new Node('LiteralPropertyName', given, 0, [name]);
  } else if (typeof value === 'string') {
    return new Node('LiteralPropertyName', given, 1, [raw]);
  } else if (typeof value === 'number') {
    return new Node('LiteralPropertyName', given, 2, [raw]);
  } else {
    Node.TODO(`${value} @ Literal`);
  }
}

module.exports = LiteralPropertyName;

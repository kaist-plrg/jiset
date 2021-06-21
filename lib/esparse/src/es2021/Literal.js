const Node = require('../Node');

// Literal :
//    NullLiteral
//    BooleanLiteral
//    NumericLiteral
//    StringLiteral
let Literal = (given) => {
  let { value, raw } = given;
  if (value === null) {
    return new Node('Literal', given, 0, [raw]);
  } else if (typeof value === 'boolean') {
    return new Node('Literal', given, 1, [raw]);
  } else if (typeof value === 'number') {
    return new Node('Literal', given, 2, [raw]);
  } else if (typeof value === 'string') {
    return new Node('Literal', given, 3, [raw]);
  } else {
    Node.TODO(`${value} @ Literal`);
  }
}

module.exports = Literal;

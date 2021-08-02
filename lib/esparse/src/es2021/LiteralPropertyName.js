const Node = require('../Node');
const LexicalNode = require('../LexicalNode');

// LiteralPropertyName :
//    IdentifierName
//    StringLiteral
//    NumericLiteral
let LiteralPropertyName = (given) => {
  let { type, name, value, raw } = given;
  if (type == 'Identifier') {
    let lexical = new LexicalNode('IdentifierName', name);
    return new Node('LiteralPropertyName', given, 0, [lexical]);
  } else if (typeof value === 'string') {
    let lexical = new LexicalNode('StringLiteral', raw);
    return new Node('LiteralPropertyName', given, 1, [lexical]);
  } else if (typeof value === 'number') {
    let lexical = new LexicalNode('NumericLiteral', raw);
    return new Node('LiteralPropertyName', given, 2, [lexical]);
  } else {
    Node.TODO(`${value} @ Literal`);
  }
}

module.exports = LiteralPropertyName;

const Node = require('../Node');
const LexicalNode = require('../LexicalNode');

// Literal :
//    NullLiteral
//    BooleanLiteral
//    NumericLiteral
//    StringLiteral
let Literal = (given) => {
  let { value, raw } = given;
  if (value === null) {
    let lexical = new LexicalNode('NullLiteral', raw);
    return new Node('Literal', given, 0, [lexical]);
  } else if (typeof value === 'boolean') {
    let lexical = new LexicalNode('BooleanLiteral', raw);
    return new Node('Literal', given, 1, [lexical]);
  } else if (typeof value === 'number' || typeof value === 'bigint') {
    let lexical = new LexicalNode('NumericLiteral', raw);
    return new Node('Literal', given, 2, [lexical]);
  } else if (typeof value === 'string') {
    let lexical = new LexicalNode('StringLiteral', raw);
    return new Node('Literal', given, 3, [lexical]);
  } else {
    Node.TODO(`${value} @ Literal`);
  }
}

module.exports = Literal;

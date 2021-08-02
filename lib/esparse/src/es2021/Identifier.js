const Node = require('../Node');
const LexicalNode = require('../LexicalNode');

// Identifier :
//    IdentifierName but not ReservedWord
let Identifier = (given) => {
  let idName = new LexicalNode('(IdentifierName \\ (ReservedWord))', given.name);
  return new Node('Identifier', given, 0, [idName]);
}

module.exports = Identifier;

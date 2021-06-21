const Node = require('../Node');

// Identifier :
//    IdentifierName but not ReservedWord
let Identifier = (given) => {
  return new Node('Identifier', given, 0, [given.name]);
}

module.exports = Identifier;

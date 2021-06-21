const Node = require('../Node');

let EmptyStatement = (given) => {
  return new Node('EmptyStatement', given, 0);
}

module.exports = EmptyStatement;

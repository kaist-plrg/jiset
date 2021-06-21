const Node = require('../Node');

let LabelledStatement = (Yield, Await, Return) => (given) => {
  Yield, Await, Return, given;
  Node.TODO('LabelledStatement');
}

module.exports = LabelledStatement;

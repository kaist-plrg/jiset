const Node = require('../Node');

// LabelledItem[Yield, Await, Return]: {
//   Statement[?Yield, ?Await, ?Return] |
//   FunctionDeclaration[?Yield, ?Await, ~Default]
// }

let LabelledItem = (Yield, Await, Return) => (given) => {
  const Statement = require('./Statement');
  const FunctionDeclaration = require('./FunctionDeclaration');
  let params = [Yield, Await, Return];
  switch(given.type) {
    case 'WhileStatement':
    case 'ForStatement':
    case 'ExpressionStatement':
    case 'BlockStatement': {
      let stmt = Statement(Yield, Await, Return)(given);
      return new Node('LabelledItem', given, 0, [stmt], params);
    }
    case 'FunctionDeclaration': {
      let func = FunctionDeclaration(Yield, Await, false)(given);
      return new Node('LabelledItem', given, 1, [func], params);
    }
    default:
      Node.TODO(`${given.type} @ LabelledItem`);
  }
}

module.exports = LabelledItem;

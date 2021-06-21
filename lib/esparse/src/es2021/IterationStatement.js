const Node = require('../Node');

// IterationStatement[Yield, Await, Return] :
//    DoWhileStatement[?Yield, ?Await, ?Return]
//    WhileStatement[?Yield, ?Await, ?Return]
//    ForStatement[?Yield, ?Await, ?Return]
//    ForInOfStatement[?Yield, ?Await, ?Return]
let IterationStatement = (Yield, Await, Return) => (given) => {
  const DoWhileStatement = require('./DoWhileStatement');
  const WhileStatement = require('./WhileStatement');
  const ForStatement = require('./ForStatement');
  const ForInOfStatement = require('./ForInOfStatement');
  let params = [Yield, Await, Return];
  let { type } = given;
  switch (type) {
    case 'DoWhileStatement': {
      let stmt = DoWhileStatement(Yield, Await, Return)(given);
      return new Node('IterationStatement', given, 0, [stmt], params);
    }
    case 'WhileStatement': {
      let stmt = WhileStatement(Yield, Await, Return)(given);
      return new Node('IterationStatement', given, 1, [stmt], params);
    }
    case 'ForStatement': {
      let stmt = ForStatement(Yield, Await, Return)(given);
      return new Node('IterationStatement', given, 2, [stmt], params);
    }
    case 'ForOfStatement':
    case 'ForInStatement': {
      let stmt = ForInOfStatement(Yield, Await, Return)(given);
      return new Node('IterationStatement', given, 3, [stmt], params);
    }
  }
}

module.exports = IterationStatement;

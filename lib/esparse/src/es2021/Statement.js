const Node = require('../Node');

// Statement[Yield, Await, Return] :
//    BlockStatement[?Yield, ?Await, ?Return]
//    VariableStatement[?Yield, ?Await]
//    EmptyStatement
//    ExpressionStatement[?Yield, ?Await]
//    IfStatement[?Yield, ?Await, ?Return]
//    BreakableStatement[?Yield, ?Await, ?Return]
//    ContinueStatement[?Yield, ?Await]
//    BreakStatement[?Yield, ?Await]
//    [+Return] ReturnStatement[?Yield, ?Await]
//    WithStatement[?Yield, ?Await, ?Return]
//    LabelledStatement[?Yield, ?Await, ?Return]
//    ThrowStatement[?Yield, ?Await]
//    TryStatement[?Yield, ?Await, ?Return]
//    DebuggerStatement
let Statement = (Yield, Await, Return) => (given) => {
  const BlockStatement = require('./BlockStatement');
  const VariableStatement = require('./VariableStatement');
  const EmptyStatement = require('./EmptyStatement');
  const ExpressionStatement = require('./ExpressionStatement');
  const IfStatement = require('./IfStatement');
  const BreakableStatement = require('./BreakableStatement');
  const ContinueStatement = require('./ContinueStatement');
  const BreakStatement = require('./BreakStatement');
  const ReturnStatement = require('./ReturnStatement');
  const WithStatement = require('./WithStatement');
  const LabelledStatement = require('./LabelledStatement');
  const ThrowStatement = require('./ThrowStatement');
  const TryStatement = require('./TryStatement');
  const DebuggerStatement = require('./DebuggerStatement');

  let nameList = [
    'BlockStatement',
    'VariableStatement',
    'EmptyStatement',
    'ExpressionStatement',
    'IfStatement',
    'BreakableStatement',
    'ContinueStatement',
    'BreakStatement',
    'ReturnStatement',
    'WithStatement',
    'LabelledStatement',
    'ThrowStatement',
    'TryStatement',
    'DebuggerStatement',
  ];

  let genList = [
    BlockStatement(Yield, Await, Return),
    VariableStatement(Yield, Await),
    EmptyStatement,
    ExpressionStatement(Yield, Await),
    IfStatement(Yield, Await, Return),
    BreakableStatement(Yield, Await, Return),
    ContinueStatement(Yield, Await),
    BreakStatement(Yield, Await),
    ReturnStatement(Yield, Await),
    WithStatement(Yield, Await, Return),
    LabelledStatement(Yield, Await, Return),
    ThrowStatement(Yield, Await),
    TryStatement(Yield, Await, Return),
    DebuggerStatement,
  ];
  let params = [Yield, Await, Return];
  switch (given.type) {
    case 'VariableDeclaration': {
      let varStmt = VariableStatement(Yield, Await)(given);
      return new Node('Statement', given, 1, [varStmt], params);
    }
    case 'SwitchStatement':
    case 'WhileStatement':
    case 'DoWhileStatement':
    case 'ForStatement':
    case 'ForInStatement':
    case 'ForOfStatement': {
      let stmt = BreakableStatement(Yield, Await, Return)(given);
      return new Node('Statement', given, 5, [stmt], params);
    }
    default: {
      let rhs = Node.getRhs(nameList, genList, given);
      if (rhs === null) Node.TODO(`${given.type} @ Statement`);
      let { index, child } = rhs;
      return new Node('Statement', given, index, [child], params);
    }
  }
}

module.exports = Statement;

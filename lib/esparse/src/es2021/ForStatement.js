const Node = require('../Node');

// ForStatement[Yield, Await, Return] :
//    for ( [lookahead ≠ let [] Expression[~In, ?Yield, ?Await]_opt ; Expression[+In, ?Yield, ?Await]_opt ; Expression[+In, ?Yield, ?Await]_opt ) Statement[?Yield, ?Await, ?Return]
//    for ( var VariableDeclarationList[~In, ?Yield, ?Await] ; Expression[+In, ?Yield, ?Await]_opt ; Expression[+In, ?Yield, ?Await]_opt ) Statement[?Yield, ?Await, ?Return]
//    for ( LexicalDeclaration[~In, ?Yield, ?Await] Expression[+In, ?Yield, ?Await]_opt ; Expression[+In, ?Yield, ?Await]_opt ) Statement[?Yield, ?Await, ?Return]
let ForStatement = (Yield, Await, Return) => (given) => {
  const Expression = require('./Expression');
  const Statement = require('./Statement');
  const LexicalDeclaration = require('./LexicalDeclaration');
  const VariableDeclarationList = require('./VariableDeclarationList');
  let params = [Yield, Await, Return];
  let { init, test, update, body } = given;
  let index;
  let i = null;
  if (init != null) switch (init.type) {
    case 'VariableDeclaration': {
      if (init.kind == 'var') {
        index = 1;
        i = VariableDeclarationList(false, Yield, Await)(init);
      } else {
        index = 2;
        i = LexicalDeclaration(false, Yield, Await)(init);
      }
      break;
    }
    default:
      index = 0;
      i = Expression(false, Yield, Await)(init)
  }
  let t = null
  if (test != null) t = Expression(true, Yield, Await)(test)
  let u = null
  if (update != null) u = Expression(true, Yield, Await)(update)
  let b = Statement(Yield, Await, Return)(body)
  return new Node('ForStatement', given, index, [i, t, u, b], params);
}

module.exports = ForStatement;

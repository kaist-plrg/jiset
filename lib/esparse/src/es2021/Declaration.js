const Node = require('../Node');

// Declaration[Yield, Await] :
//    HoistableDeclaration[?Yield, ?Await, ~Default]
//    ClassDeclaration[?Yield, ?Await, ~Default]
//    LexicalDeclaration[+In, ?Yield, ?Await]
let Declaration = (Yield, Await) => (given) => {
  const LexicalDeclaration = require('./LexicalDeclaration');
  const HoistableDeclaration = require('./HoistableDeclaration');
  const ClassDeclaration = require('./ClassDeclaration');
  let params = [Yield, Await];
  let { type } = given;
  switch (type) {
    case 'FunctionDeclaration': {
      let hoist = HoistableDeclaration(Yield, Await, false)(given);
      return new Node('Declaration', given, 0, [hoist], params);
    }
    case 'ClassDeclaration': {
      let decl = ClassDeclaration(Yield, Await, false)(given);
      return new Node('Declaration', given, 1, [decl], params);
    }
    case 'VariableDeclaration': {
      let lex = LexicalDeclaration(true, Yield, Await)(given);
      return new Node('Declaration', given, 2, [lex], params);
    }
    default:
      Node.TODO(`${type} @ Declaration`);
  }
}

module.exports = Declaration;

const Node = require('../Node');

// ScriptBody : StatementList[~Yield, ~Await, ~Return]
let ScriptBody = (given) => {
  const StatementList = require('./StatementList');

  let list = StatementList(false, false, false)(given.body);
  return new Node('ScriptBody', given, 0, [list]);
}

module.exports = ScriptBody;

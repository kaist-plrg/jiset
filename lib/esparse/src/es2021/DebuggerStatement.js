const Node = require('../Node');

// DebuggerStatement: {
//   `debugger` `;`
// }

let DebuggerStatement = (given) => {
  return new Node('DebuggerStatement', given, 0, []);
}

module.exports = DebuggerStatement;

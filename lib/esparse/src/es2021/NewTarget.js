const Node = require('../Node');

// NewTarget: {
//   `new` `.` `target`
// }

let NewTarget = (given) => {
  return new Node('NewTarget', given, 0, [], []);
}

module.exports = NewTarget;

const Node = require('../Node');

let Finally = (Yield, Await, Return) => (given) => {
  Yield, Await, Return, given;
  Node.TODO('Finally');
}

module.exports = Finally;

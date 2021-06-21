const Node = require('../Node');

let AsyncGeneratorMethod = (Yield, Await) => (given) => {
  Yield, Await, given;
  Node.TODO('AsyncGeneratorMethod');
}

module.exports = AsyncGeneratorMethod;

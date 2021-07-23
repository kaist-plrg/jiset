const Node = require('../Node');

// PropertySetParameterList: {
//   FormalParameter[~Yield, ~Await]
// }

let PropertySetParameterList = () => (given) => {
  const FormalParameter = require('./FormalParameter');
  let p = FormalParameter(false, false)(given);
  return new Node('PropertySetParameterList', given, 0, [p], []);
}

module.exports = PropertySetParameterList;

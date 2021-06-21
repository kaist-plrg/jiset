const Node = require('../Node');

// LetOrConst :
//    let
//    const
let LetOrConst = (given) => {
  let index = 0;
  if (given == 'const') index = 1;
  return new Node('LetOrConst', {}, index);
}

module.exports = LetOrConst;

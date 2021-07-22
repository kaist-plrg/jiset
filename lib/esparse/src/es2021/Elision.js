const Node = require('../Node');

// Elision :
//    ,
//    Elision ,
let Elision = (given) => {
  let size = given.length;
  let elision = new Node('Elision', {}, 0);
  for (let i = 1; i < size; i++) {
    elision = new Node('Elision', {}, 1, [elision]);
  }
  return elision;
}

module.exports = Elision;

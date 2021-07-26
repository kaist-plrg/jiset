const Node = require('../Node');

// MetaProperty: {
//   NewTarget |
//   ImportMeta
// }

let MetaProperty = (given) => {
  const NewTarget = require('./NewTarget');
  
  switch (given.meta.name) {
    case 'new':
      return new Node('MetaProperty', given, 0, [NewTarget(given)], []);
    default:
      Node.TODO(`${given.meta.name} @ MetaProperty`);
  }
}

module.exports = MetaProperty;

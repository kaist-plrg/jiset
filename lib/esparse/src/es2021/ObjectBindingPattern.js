const Node = require('../Node');

// ObjectBindingPattern[Yield, Await]: {
//   `{` `}` |
//   `{` BindingRestProperty[?Yield, ?Await] `}` |
//   `{` BindingPropertyList[?Yield, ?Await] `}` |
//   `{` BindingPropertyList[?Yield, ?Await] `,` BindingRestProperty[?Yield, ?Await]? `}`
// }
let ObjectBindingPattern = (Yield, Await) => (given) => {
  Node.TODO('ObjectBindingPattern');
}

module.exports = ObjectBindingPattern;

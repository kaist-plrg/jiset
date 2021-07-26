const Node = require('../Node');
const { checkComma } = require('../Global');

// ObjectBindingPattern[Yield, Await]: {
//   `{` `}` |
//   `{` BindingRestProperty[?Yield, ?Await] `}` |
//   `{` BindingPropertyList[?Yield, ?Await] `}` |
//   `{` BindingPropertyList[?Yield, ?Await] `,` BindingRestProperty[?Yield, ?Await]? `}`
// }
let ObjectBindingPattern = (Yield, Await) => (given) => {
  let BindingRestProperty = require('./BindingRestProperty');
  let BindingPropertyList = require('./BindingPropertyList');
  
  let params = [Yield, Await];
  let properties = given.properties;
  
  // [0,0]
  if (properties.length === 0) 
    return new Node('ObjectBindingPattern', given, 0, [], params);
  else {
    // check if last element is BindingRestProperty
    let lastProp = properties.pop();
    
    // [3,0]
    const hasComma = checkComma(given.start, given.end, '}');
    if (lastProp === null || hasComma) {
      if (hasComma) properties.push(lastProp);
      let propList = BindingPropertyList(Yield, Await)(properties);
      return new Node('ObjectBindingPattern', given, 3, [propList, null], params);
    }
    // [1,0], [3,1]
    else if (lastProp.type === 'RestElement') {
      let restProp = BindingRestProperty(Yield, Await)(lastProp);
      // [1,0]
      if (properties.length === 0)
        return new Node('ObjectBindingPattern', given, 1, [restProp], params);
      // [3,1]
      else {
        let propList = BindingPropertyList(Yield, Await)(properties);
        return new Node('ObjectBindingPattern', given, 3, [propList, restProp], params);
      }
    }
    // [2,0]
    else {
      properties.push(lastProp);
      let propList = BindingPropertyList(Yield, Await)(properties);
      return new Node('ObjectBindingPattern', given, 2, [propList], params);
    }
  }
}

module.exports = ObjectBindingPattern;

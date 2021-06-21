const Node = require('../Node');

// ObjectLiteral[Yield, Await] :
//    { }
//    { PropertyDefinitionList[?Yield, ?Await] }
//    { PropertyDefinitionList[?Yield, ?Await] , }
let ObjectLiteral = (Yield, Await) => (given) => {
  let PropertyDefinitionList = require('./PropertyDefinitionList');

  let params = [Yield, Await];
  let { properties } = given;
  if (properties.length == 0) {
    return new Node('ObjectLiteral', given, 0, [], params);
  } else {
    let list = PropertyDefinitionList(Yield, Await)(given);
    return new Node('ObjectLiteral', given, 1, [list], params);
  }
}

module.exports = ObjectLiteral;

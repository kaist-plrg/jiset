const Node = require('../Node');

// PropertyDefinitionList[Yield, Await] :
//    PropertyDefinition[?Yield, ?Await]
//    PropertyDefinitionList[?Yield, ?Await] , PropertyDefinition[?Yield, ?Await]
let PropertyDefinitionList = (Yield, Await) => (given) => {
  let PropertyDefinition = require('./PropertyDefinition');

  let genChild = PropertyDefinition(Yield, Await);
  let params = [Yield, Await];
  let { properties } = given;
  return Node.fromList('PropertyDefinitionList', properties, genChild, params);
}

module.exports = PropertyDefinitionList;

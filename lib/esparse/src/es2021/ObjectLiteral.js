const Node = require('../Node');
const { checkComma } = require('../Global');

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
    const hasComma = checkComma(given.loc);
    return new Node('ObjectLiteral', given, hasComma ? 2 : 1, [list], params);
  }
}

module.exports = ObjectLiteral;

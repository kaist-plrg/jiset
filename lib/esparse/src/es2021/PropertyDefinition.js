const Node = require('../Node');

// PropertyDefinition[Yield, Await] :
//    IdentifierReference[?Yield, ?Await]
//    CoverInitializedName[?Yield, ?Await]
//    PropertyName[?Yield, ?Await] : AssignmentExpression[+In, ?Yield, ?Await]
//    MethodDefinition[?Yield, ?Await]
//    ... AssignmentExpression[+In, ?Yield, ?Await]
let PropertyDefinition = (Yield, Await) => (given) => {
  let IdentifierReference = require('./IdentifierReference');
  let PropertyName = require('./PropertyName');

  let AssignmentExpression = require('./AssignmentExpression');
  let params = [Yield, Await];
  let { method, shorthand, key, value } = given;
  if (shorthand) {
    let ref = IdentifierReference(Yield, Await)(key);
    return new Node('PropertyDefinition', given, 0, [ref], params);
  } else if (!method) {
    let name = PropertyName(Yield, Await)(given);
    let expr = AssignmentExpression(true, Yield, Await)(value);
    return new Node('PropertyDefinition', given, 2, [name, expr], params);
  } else {
    Node.TODO('PropertyDefinition');
  }
}

module.exports = PropertyDefinition;

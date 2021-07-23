const Node = require('../Node');

// PropertyDefinition[Yield, Await] :
//    IdentifierReference[?Yield, ?Await]
//    CoverInitializedName[?Yield, ?Await]
//    PropertyName[?Yield, ?Await] : AssignmentExpression[+In, ?Yield, ?Await]
//    MethodDefinition[?Yield, ?Await]
//    ... AssignmentExpression[+In, ?Yield, ?Await]
let PropertyDefinition = (Yield, Await) => (given) => {
  let IdentifierReference = require('./IdentifierReference');
  let CoverInitializedName = require('./CoverInitializedName');
  let PropertyName = require('./PropertyName');
  let MethodDefinition = require('./MethodDefinition');

  let AssignmentExpression = require('./AssignmentExpression');
  let params = [Yield, Await];
  let { method, shorthand, key, value } = given;
  
  switch (given.kind) {
    case 'get': 
    case 'set': {
      let method = MethodDefinition(Yield, Await)(given);
      return new Node('PropertyDefinition', given, 3, [method], params);
    }
    case 'init': {
      if (shorthand && value.type === 'AssignmentPattern') {
        let cov = CoverInitializedName(Yield, Await)(value);
        return new Node('PropertyDefinition', given, 1, [cov], params);
      } else if (shorthand) {
        let ref = IdentifierReference(Yield, Await)(key);
        return new Node('PropertyDefinition', given, 0, [ref], params);
      } else if (!method) {
        let name = PropertyName(Yield, Await)(given);
        let expr = AssignmentExpression(true, Yield, Await)(value);
        return new Node('PropertyDefinition', given, 2, [name, expr], params);
      } else if (method) {
        let method = MethodDefinition(Yield, Await)(given);
        return new Node('PropertyDefinition', given, 3, [method], params);
      }
      return Node.TODO(`init @ PropertyDefinition`);
    }
    default:
      Node.TODO(`${given.kind} @ PropertyDefinition`);
  }
}

module.exports = PropertyDefinition;

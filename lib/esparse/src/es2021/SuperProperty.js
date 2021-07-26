const Node = require('../Node');

// SuperProperty[Yield, Await] :
//   super [ Expression[+In, ?Yield, ?Await] ]
//   super . IdentifierName

let SuperProperty = (Yield, Await) => (given) => {
  const Expression = require('./Expression');

  let params = [Yield, Await];
  let { object, property } = given;
  if (object.type === 'Super') {
    if (property.type === 'Identifier') {
      return new Node('SuperProperty', given, 1, [property.name]);
    } else {
      let expr = Expression(true, Yield, Await)(property);
      return new Node('SuperProperty', property, 0, [expr], params);
    }
  } else {
    Node.TODO('SuperProperty');
  }
}

module.exports = SuperProperty;

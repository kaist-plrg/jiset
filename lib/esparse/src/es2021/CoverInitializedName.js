const Node = require('../Node');

// CoverInitializedName[Yield, Await] :
//     IdentifierReference[?Yield, ?Await] Initializer[+In, ?Yield, ?Await]
let CoverInitializedName = (Yield, Await) => (given) => {
  const IdentifierReference = require('./IdentifierReference');
  const Initializer = require('./Initializer');

  let params = [Yield, Await];
  let { left, right } = given;
  let ref = IdentifierReference(Yield, Await)(left);
  let init = Initializer(true, Yield, Await)(right);
  return new Node('CoverInitializedName', given, 0, [ref, init], params);
}

module.exports = CoverInitializedName;
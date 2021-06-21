const Node = require('../Node');

// ArrowParameters[Yield, Await] :
//    BindingIdentifier[?Yield, ?Await]
//    CoverParenthesizedExpressionAndArrowParameterList[?Yield, ?Await]
let ArrowParameters = (Yield, Await) => (given) => {
  const CoverParenthesizedExpressionAndArrowParameterList = require('./CoverParenthesizedExpressionAndArrowParameterList');
  let params = [Yield, Await];
  // we treat `x => ...` as `(x) => ...` because acorn cannot distinguish them.
  let cover = CoverParenthesizedExpressionAndArrowParameterList(Yield, Await)(given);
  return new Node('ArrowParameters', given, 1, [cover], params);
}

module.exports = ArrowParameters;

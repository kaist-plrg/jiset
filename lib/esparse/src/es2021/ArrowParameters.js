const Node = require('../Node');
const { checkCoverCallExpressionAndAsyncArrowHead } = require('../Global');

// ArrowParameters[Yield, Await] :
//    BindingIdentifier[?Yield, ?Await]
//    CoverParenthesizedExpressionAndArrowParameterList[?Yield, ?Await]
// TODO fix span
let ArrowParameters = (Yield, Await) => (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  const CoverParenthesizedExpressionAndArrowParameterList = require('./CoverParenthesizedExpressionAndArrowParameterList');
  let params = [Yield, Await];
  
  // check if CoverParenthesizedExpressionAndArrowParameterList
  let isCover = checkCoverCallExpressionAndAsyncArrowHead(given.start, given.end);
  
  if (isCover) {
    let cover = CoverParenthesizedExpressionAndArrowParameterList(Yield, Await)(given);
    cover.start = given.start;
    cover.end = given.end;
    cover.loc = given.loc;
    return new Node('ArrowParameters', given, 1, [cover], params);
  } else {
    let binding = BindingIdentifier(Yield, Await)(given[0]);
    return new Node('ArrowParameters', given, 0, [binding], params);
  }
}

module.exports = ArrowParameters;

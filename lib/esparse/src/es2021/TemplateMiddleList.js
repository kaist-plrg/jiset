const Node = require('../Node');

// TemplateMiddleList[Yield, Await, Tagged] :
//    TemplateMiddle Expression[+In, ?Yield, ?Await]
//    TemplateMiddleList[?Yield, ?Await, ?Tagged] TemplateMiddle Expression[+In, ?Yield, ?Await]
let TemplateMiddleList = (Yield, Await, Tagged) => (given) => {
  const Expression = require('./Expression');
  let params = [Yield, Await, Tagged];
  let { quasis, expressions } = given;
  let middle = '}' + quasis.pop().value.raw + '${';
  let expr = Expression(true, Yield, Await)(expressions.pop());
  if (quasis.length == 0) {
    return new Node('TemplateMiddleList', given, 0, [middle, expr], params);
  } else {
    let list = TemplateMiddleList(Yield, Await, Tagged)(given);
    return new Node('TemplateMiddleList', given, 1, [list, middle, expr], params);
  }
}

module.exports = TemplateMiddleList;

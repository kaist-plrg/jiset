const Node = require('../Node');

// TemplateSpans[Yield, Await, Tagged] :
//    TemplateTail
//    TemplateMiddleList[?Yield, ?Await, ?Tagged] TemplateTail
let TemplateSpans = (Yield, Await, Tagged) => (given) => {
  const TemplateMiddleList = require('./TemplateMiddleList');
  let params = [Yield, Await, Tagged];
  let { quasis, expressions } = given;
  let tail = '}' + quasis.pop().value.raw + '`';
  if (expressions.length == 0) {
    return new Node('TemplateSpans', given, 0, [tail], params);
  } else {
    let list = TemplateMiddleList(Yield, Await, Tagged)(given);
    return new Node('TemplateSpans', given, 1, [list, tail], params);
  }
}

module.exports = TemplateSpans;

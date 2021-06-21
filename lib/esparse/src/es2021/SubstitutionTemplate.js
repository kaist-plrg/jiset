const Node = require('../Node');

// SubstitutionTemplate[Yield, Await, Tagged] :
//    TemplateHead Expression[+In, ?Yield, ?Await] TemplateSpans[?Yield, ?Await, ?Tagged]
let SubstitutionTemplate = (Yield, Await, Tagged) => (given) => {
  const Expression = require('./Expression');
  const TemplateSpans = require('./TemplateSpans');
  let params = [Yield, Await, Tagged];
  let { quasis, expressions } = given;
  let head = '`' + quasis.shift().value.raw + '${';
  let expr = Expression(true, Yield, Await)(expressions.shift());
  let spans = TemplateSpans(Yield, Await, Tagged)(given);
  return new Node('SubstitutionTemplate', given, 0, [head, expr, spans], params);
}

module.exports = SubstitutionTemplate;

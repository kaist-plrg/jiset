const Node = require('../Node');

// TemplateLiteral[Yield, Await, Tagged] :
//    NoSubstitutionTemplate
//    SubstitutionTemplate[?Yield, ?Await, ?Tagged]
let TemplateLiteral = (Yield, Await, Tagged) => (given) => {
  const SubstitutionTemplate = require('./SubstitutionTemplate');
  let params = [Yield, Await, Tagged];
  let { quasis, expressions } = given;
  if (expressions.length == 0 && quasis.length == 1) {
    let temp = '`' + quasis[0].value.raw + '`'
    return new Node('TemplateLiteral', given, 0, [temp], params);
  } else {
    let subs = SubstitutionTemplate(Yield, Await, Tagged)(given);
    return new Node('TemplateLiteral', given, 1, [subs], params);
  }
}

module.exports = TemplateLiteral;

const Node = require('../Node');
const LexicalNode = require('../LexicalNode');

// TemplateLiteral[Yield, Await, Tagged] :
//    NoSubstitutionTemplate
//    SubstitutionTemplate[?Yield, ?Await, ?Tagged]
let TemplateLiteral = (Yield, Await, Tagged) => (given) => {
  const SubstitutionTemplate = require('./SubstitutionTemplate');
  let params = [Yield, Await, Tagged];
  let { quasis, expressions } = given;
  if (expressions.length == 0 && quasis.length == 1) {
    let temp = '`' + quasis[0].value.raw + '`'
    let lexical = new LexicalNode('NoSubstitutionTemplate', temp);
    return new Node('TemplateLiteral', given, 0, [lexical], params);
  } else {
    let subs = SubstitutionTemplate(Yield, Await, Tagged)(given);
    return new Node('TemplateLiteral', given, 1, [subs], params);
  }
}

module.exports = TemplateLiteral;

const Node = require('../Node');
const { createSemicolon } = require('../Global');

// ClassTail[Yield, Await] :
//    ClassHeritage[?Yield, ?Await]_opt { ClassBody[?Yield, ?Await]_opt }
let ClassTail = (Yield, Await) => (given) => {
  const ClassHeritage = require('./ClassHeritage');
  const ClassBody = require('./ClassBody');
  let params = [Yield, Await];
  let { superClass, body } = given;
  let h = null;
  if (superClass) {
    h = ClassHeritage(Yield, Await)(superClass);
  }
  // TODO fix span
  // handle ClassElement[2,0]
  let b = null;
  let start = body.start;
  let end = body.end;
  
  if (body.body.length === 0)
    body.body = createSemicolon(start, end);
  else {
    let idx = 0;
    while(true) {
      let elem = body.body[idx];
      let arr0 = body.body.slice(0, idx);
      let semis = createSemicolon(start, elem.start);
      let arr1 = body.body.slice(idx, body.body.length);
      body.body = arr0.concat(semis).concat(arr1);
      idx += 1 + semis.length;
      start = elem.end;
      if (idx >= body.body.length) {
        let lastSemis = createSemicolon(elem.end, end);
        body.body = body.body.concat(lastSemis);
        break;
      }
    }
  }
  if (body.body.length > 0) {
    b = ClassBody(Yield, Await)(body);
  }
  return new Node('ClassTail', given, 0, [h, b], params);
}

module.exports = ClassTail;

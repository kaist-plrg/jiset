Grammar = (() => {
  ////////////////////////////////////////////////////////////////////////////////
  // Context-Free-Grammar (CFG)
  ////////////////////////////////////////////////////////////////////////////////
  class Grammar {
    constructor(lexProds, prods) {
      this.lexProds = lexProds;
      this.prods = prods;
    }

    static ES2018() {
      // get elements
      function getElem(id) { return document.getElementById(id) }

      // get productions froms section
      function getSection(name) {
        return Array.from(getElem(name).children)
          .filter(child => child.tagName == Prod.tag)
          .map(Prod.from)
      }

      // get production from an element
      function getProd(name) {
        return Prod.from(getElem(name));
      }

      // remove default white spcae and line terminators
      let spaceNames = new Set(['WhiteSpace', 'LineTerminator', 'LineTerminatorSequence', 'Comment'])
      let removedElems = [];
      for (let elem of document.getElementsByTagName('emu-production')) {
        if (spaceNames.has(elem.getAttribute('name'))) removedElems.push(elem);
      }
      removedElems.forEach(elem => elem.remove());

      // extract productions
      let lexProds = getSection('sec-lexical-grammar');
      lexProds = lexProds.concat([
        'prod-NotEscapeSequence',
        'prod-NotCodePoint',
        'prod-CodePoint'
      ].map(getProd));
      let prods = [];
      prods = prods.concat(getSection('sec-expressions'));
      prods = prods.concat(getSection('sec-statements'));
      prods = prods.concat(getSection('sec-functions-and-classes'));
      prods = prods.concat(getSection('sec-scripts-and-modules'));
      prods = prods.concat([
        'prod-AsyncGeneratorMethod',
        'prod-AsyncGeneratorDeclaration',
        'prod-AsyncGeneratorExpression',
        'prod-AsyncGeneratorBody',
        'prod-AssignmentRestProperty',
        'prod-SubstitutionTemplate',
        'prod-BindingRestProperty'
      ].map(getProd));

      // return grammar
      return new Grammar(lexProds, prods)
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Productions
  ////////////////////////////////////////////////////////////////////////////////
  class Prod {
    static tag = 'EMU-PRODUCTION'

    constructor(lhs, rhsList) {
      this.lhs = lhs;
      this.rhsList = rhsList;
      this.semantics = []; // TODO
    }

    static from(elem) {
      let lhsElem = elem.getElementsByTagName(Nonterminal.tag)[0];
      let lhs = Lhs.from(lhsElem);

      let rhsElemList = elem.getElementsByTagName(Rhs.tag);
      let rhsList = [];

      let isOneOf = elem.getAttribute('oneof') != null
      if (isOneOf) {
        for (let tokenElem of rhsElemList[0].children) {
          rhsList.push(new Rhs([Token.from(tokenElem)], ''));
        }
      } else {
        for (let rhsElem of rhsElemList) {
          rhsList.push(Rhs.from(rhsElem));
        }
      }

      return new Prod(lhs, rhsList);
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Left-Hand-Sides (LHSs)
  ////////////////////////////////////////////////////////////////////////////////
  class Lhs {
    constructor(name, params) {
      this.name = name;
      this.params = params;
    }

    static from(elem) {
      let name = elem.getElementsByTagName('A')[0].innerText.trim();

      let paramsAttr = elem.getAttribute('params');
      let params = [];
      if (paramsAttr != null) params = paramsAttr.split(',').map(x => 'p' + x.trim());

      return new Lhs(name, params);
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Right-Hand-Sides (RHSs)
  ////////////////////////////////////////////////////////////////////////////////
  class Rhs {
    constructor(tokens, cond) {
      this.tokens = tokens;
      this.cond = cond;
    }

    static tag = 'EMU-RHS'

    static from(elem) {
      let condAttr = elem.getAttribute('constraints');
      let cond = '';
      if (condAttr != null) switch (condAttr[0]) {
        case '+': cond = 'p' + condAttr.substr(1); break;
        case '~': cond = '!p' + condAttr.substr(1); break;
      }

      let tokenElemList = Array.from(elem.children).reverse();
      if (condAttr != null) tokenElemList.pop();
      let tokens = [];
      while (tokenElemList.length > 0) {
        let token = Token.fromList(tokenElemList);
        tokens.push(token);
      }

      return new Rhs(tokens, cond);
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Tokens
  ////////////////////////////////////////////////////////////////////////////////
  class Token {
    static from(tokenElem) {
      switch (tokenElem.tagName) {
        case Terminal.tag: return Terminal.from(tokenElem);
        case Nonterminal.tag: return Nonterminal.from(tokenElem);
        case ButNotToken.tag: throw 'wrong place of the "but not" token.';
        default:
          let text = tokenElem.innerText.trim();
          if (text == EmptyToken.text) return "EmptyToken";
          if (text == NoLineTerminatorToken.text) return "NoLineTerminatorToken";
          if (text == UnicodeAny.text) return "UnicodeAny";
          if (text == UnicodeIdStart.text) return "UnicodeIdStart";
          if (text == UnicodeIdContinue.text) return "UnicodeIdContinue";
          if (text.startsWith('<') && text.endsWith('>')) return new Unicode(text.substr(1, text.length - 2));
          if (text.startsWith('[lookahead ')) return Lookahead.from(tokenElem);
          throw 'wrong token element: ' + text;
      }
    }

    static fromList(tokenElemList) {
      let tokenElem = tokenElemList.pop();
      let token = Token.from(tokenElem);

      let nextElem = tokenElemList.pop();
      if (nextElem !== undefined) {
        if (nextElem.tagName == 'EMU-GMOD') {
          return ButNotToken.from(token, nextElem)
        } else {
          tokenElemList.push(nextElem);
          return token;
        }
      }
      return token;
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Terminal
  ////////////////////////////////////////////////////////////////////////////////
  class Terminal extends Token {
    static tag = 'EMU-T'

    constructor(term) {
      super();
      this.term = term;
    }

    static from(elem) {
      return new Terminal(elem.innerText.trim());
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Nonterminal
  ////////////////////////////////////////////////////////////////////////////////
  class Nonterminal extends Token {
    static tag = 'EMU-NT'

    constructor(name, args, optional) {
      super();
      this.name = name;
      this.args = args;
      this.optional = optional;
    }

    static from(elem) {
      let name = elem.getElementsByTagName('A')[0].innerText.trim();

      let argsAttr = elem.getAttribute('params');
      let args = [];
      if (argsAttr != null) args = argsAttr.split(',').map(str => {
        str = str.trim();
        switch (str[0]) {
          case '+': return 'true';
          case '~': return 'false';
          case '?': return 'p' + str.substr(1);
          default: throw 'unexpected prefix: ' + str[0];
        }
      });

      let optAttr = elem.getAttribute('optional');
      let optional = optAttr != null;

      return new Nonterminal(name, args, optional);
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Token with Exclusive Cases
  ////////////////////////////////////////////////////////////////////////////////
  class ButNotToken extends Token {
    static tag = 'EMU-GMOD'

    constructor(base, cases) {
      super();
      this.base = base;
      this.cases = cases;
    }

    static from(base, casesElem) {
      let cases = Array.from(casesElem.children).map(caseElem => Token.from(caseElem))
      return new ButNotToken(base, cases);
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Token that Checks Lookaheads
  ////////////////////////////////////////////////////////////////////////////////
  class Lookahead extends Token {
    constructor(contains, cases) {
      super();
      this.contains = contains;
      this.cases = cases;
    }

    static from(elem) {
      let op = elem.innerText[11];

      let contains;
      switch (op) {
        case '≠':
        case '∉':
          contains = false;
          break;
        case '=':
        case '∈':
          contains = true;
          break;
        default: throw 'wrong operator for lookahead: ' + op;
      }

      let cases = [[]];
      Array.from(elem.children).forEach(child => {
        let prev = child.previousSibling;
        let token = Token.from(child);
        let top = cases.pop();
        if (prev.nodeName == '#text' && prev.textContent.indexOf(',') != -1) {
          if (top) cases.push(top);
          cases.push([token]);
        } else {
          top.push(token);
          cases.push(top);
        }
      });

      return new Lookahead(contains, cases);
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Special Tokens
  ////////////////////////////////////////////////////////////////////////////////
  // predefined unicodes
  class Unicode extends Token {
    constructor(code) {
      super();
      this.code = code;
    }
  }

  // empty token
  let EmptyToken = new Token;
  EmptyToken.text = '[empty]';

  // no line terminator token
  let NoLineTerminatorToken = new Token;
  NoLineTerminatorToken.text = '[no LineTerminator here]';

  // any Unicode code point
  let UnicodeAny = new Token;
  UnicodeAny.text = 'any Unicode code point';

  // any Unicode code point with the Unicode property “ID_Start”
  let UnicodeIdStart = new Token;
  UnicodeIdStart.text = 'any Unicode code point with the Unicode property “ID_Start”';

  // any Unicode code point with the Unicode property “ID_Continue”
  let UnicodeIdContinue = new Token;
  UnicodeIdContinue.text = 'any Unicode code point with the Unicode property “ID_Continue”';

  ////////////////////////////////////////////////////////////////////////////////
  // Synthesize ASTs / Parsers
  ////////////////////////////////////////////////////////////////////////////////
  return Grammar;
})();

// download given data into a file
function save(data, filename) {
  if (data === undefined) {
    console.error('No data');
    return;
  }

  // save as jsmodel form
  if (filename === undefined) filename = 'dump.jsmodel';

  var blob = new Blob([data], {type: 'text/json'});
  e = document.createEvent('MouseEvents');
  a = document.createElement('a');

  a.download = filename;
  a.href = window.URL.createObjectURL(blob);
  a.dataset.downloadurl = ['text/json', a.download, a.href].join(':');
  e.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
  a.dispatchEvent(e);
}

// save ES2018 grammar
json = JSON.stringify({
  globalMethods: [],
  grammar: Grammar.ES2018(),
  tys: []
}, null, 2);
save(json, "spec.json")

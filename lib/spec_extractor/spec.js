SpecExtractor = (() => {
  ////////////////////////////////////////////////////////////////////////////////
  // Spec
  ////////////////////////////////////////////////////////////////////////////////
  class Spec {
    constructor(globalMethods, consts, grammar, symbols, intrinsics, tys) {
      this.globalMethods = globalMethods;
      this.consts = consts;
      this.grammar = grammar;
      this.symbols = symbols;
      this.intrinsics = intrinsics;
      this.tys = tys;
    }
  }
  ////////////////////////////////////////////////////////////////////////////////
  // Algorithm
  ////////////////////////////////////////////////////////////////////////////////
  // split texts
  function splitText(text) {
    let tokens = [];
    let prevWordChar = false;
    for (ch of text) {
      let isWordChar = /\w/.test(ch);
      let isSpace = /\s/.test(ch);
      if (prevWordChar && isWordChar) tokens.push(tokens.pop() + ch);
      else if (!isSpace) tokens.push(ch);
      prevWordChar = isWordChar;
    }
    return tokens;
  }
  // get steps
  function getStep(li, grammar) {
    let blocks = Array.from(li.childNodes);
    let tokens = [];
    for (let block of blocks) {
      let text = block.innerText;
      switch (block.nodeName) {
        case 'EMU-CONST': tokens.push({ const: text }); break;
        case 'CODE': tokens.push({ code: text }); break;
        case 'EMU-VAL': tokens.push({ value: text }); break;
        case 'VAR': tokens.push({ id: text }); break;
        case 'OL': tokens.push({ steps: getSteps(block, grammar) }); break;
        case 'UL': tokens.push({ steps: getSteps(block, grammar) }); break;
        case '#text': tokens = tokens.concat(splitText(block.textContent)); break;
        case 'EMU-NT': tokens.push({ nt: Array.from(block.childNodes).filter((f) => (f.nodeName !== "EMU-MODS")).map((f) => f.innerText).join() }); break;
        case 'EMU-XREF': tokens = tokens.concat(splitText(text)); break;
        case 'SUP': tokens.push({ sup: getStep(block, grammar) }); break;
        case 'SUB': tokens = tokens.concat(splitText(text)); break;
        case 'I': tokens = tokens.concat(splitText(text)); break;
        case 'B': tokens = tokens.concat(splitText(text)); break;
        case 'PRE': tokens.push({ code: block.innerText }); break;
        case 'A': tokens.push({ url: block.innerText }); break;
        case 'EMU-GRAMMAR':
          let name = norm(text)
            .replace(/\[noLineTerminatorhere\]/g, '')
            .replace(/\[lookahead[^\]]+\]/g, '')
            .replace(/\[empty\]/g, '');
          if (name in grammar.idxMap) {
            let prod = block.children[0];
            let lhs = prod.getAttribute('name');
            let subs = [];
            for (let sub of prod.children[2].getElementsByTagName('emu-nt')) {
              subs.push(sub.innerText);
            }
            let subName = lhs + grammar.idxMap[name].idx;
            tokens.push({ grammar: subName, subs: subs });
          } else {
            tokens.push({ grammar: 'not-found', subs: [] });
          }
          break;
        default:
          error('not yet defined tokens: ' + block);
          tokens = tokens.concat(splitText(text));
      }
    }
    return { tokens: tokens };
  }

  // get step lists
  function getSteps(ol, grammar) {
    let steps = [];
    for (let li of ol.children) {
      steps.push(getStep(li, grammar));
    }
    return steps;
  }

  // get lines
  function getTokens(line) {
    let blocks = Array.from(line.childNodes);
  }

  // get head information of algorithms
  function getHead(algo) {
    let head = algo.parentElement.getElementsByTagName('H1')[0]
    let secnoElem = head.childNodes[0];
    let secno = secnoElem.innerText

    let str = head.innerText.slice(secno.length);
    let name;
    let kind = 'Method';
    if (str.indexOf('(') == -1) name = str;
    else name = str.substring(0, str.indexOf('('));
    str = str.slice(name.length).replace('#','');
    name = norm(name);
    if (name.startsWith('StaticSemantics')) kind = 'StaticSemantics';
    if (name.startsWith('RuntimeSemantics')) kind = 'RuntimeSemantics';
    name = name.replace(/.*Semantics:/g, '');
    let arr = [];
    for (x of str.matchAll(/[^\s,()\[\]]+/g)) arr.push(x[0]);

    let length = 0;
    let idx1 = str.indexOf('[');
    let idx2 = str.indexOf('...');
    let idx;
    if (idx1 != -1 && idx2 != -1) idx = Math.min(idx1, idx2);
    else if(idx1 != -1) idx = idx1;
    else if(idx2 != -1) idx = idx2;
    else idx = str.length;
    str = str.substr(0, idx);
    if (idx != -1) str = str.substr(0, idx);
    for (x of str.matchAll(/[^\s,()\[\]]+/g)) length++;

    return {
      kind: kind,
      name: name,
      params: arr,
      length: length
    };
  }

  // get algorithm structures
  function getAlgo(algo, grammar) {
    try {
      let head = getHead(algo)
      return {
        name: head.name,
        length: head.length,
        body: {
          kind: head.kind,
          params: head.params,
          steps: getSteps(algo.children[0], grammar)
        }
      };
    } catch (e) {
      error(e);
    }
  }
  ////////////////////////////////////////////////////////////////////////////////
  // Context-Free-Grammar (CFG)
  ////////////////////////////////////////////////////////////////////////////////
  class Grammar {
    constructor(lexProds, prods) {
      this.lexProds = lexProds;
      this.prods = prods;

      // generate index map
      this.idxMap = {};
      for (let prod of prods) {
        let lhs = prod.lhs;
        let i = 0;
        for (let rhs of prod.rhsList) {
          let names = [lhs.name + ':'];
          for (let token of rhs.tokens) {
            let newNames = [];
            if (token instanceof Terminal) names.forEach((name) => {
              newNames.push(name + token.term);
            });
            else if (token instanceof Nonterminal) names.forEach((name) => {
              if (token.optional) newNames.push(name);
              newNames.push(name + token.name);
            });
            else if (token instanceof ButNotToken) names.forEach((name) => {
              newNames.push(name + token.base.name + "butnot" + token.cases.map((x) => x.name).join(''));
            });
            else newNames = names;
            names = newNames;
          }
          let j = 0;
          for (name of names) {
            this.idxMap[norm(name)] = { idx: i, subIdx: j };
            j++;
          }
          i++;
        }
      }
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
      if (paramsAttr != null) params = paramsAttr.split(',').map(x => x.trim());

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
          if (text == UnicodeIdStart.text || text == UnicodeIdStart.oldText) return "UnicodeIdStart";
          if (text == UnicodeIdContinue.text || text == UnicodeIdContinue.oldText) return "UnicodeIdContinue";
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
      if (argsAttr != null) args = argsAttr.split(',').map(str => str.trim());

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
  UnicodeIdStart.oldText = 'any Unicode code point with the Unicode property &ldquo;ID_Start&rdquo;';

  // any Unicode code point with the Unicode property “ID_Continue”
  let UnicodeIdContinue = new Token;
  UnicodeIdContinue.text = 'any Unicode code point with the Unicode property “ID_Continue”';
  UnicodeIdContinue.oldText = 'any Unicode code point with the Unicode property &ldquo;ID_Continue&rdquo;';

  ////////////////////////////////////////////////////////////////////////////////
  // Helpers
  ////////////////////////////////////////////////////////////////////////////////
  // string normalization
  function norm(str) {
    return str
      .replace(/\s+/g , '')
      .replace(/\//g , '')
      .replace('#','');
  }
  // load script
  function loadScript(url) {
    let script = document.createElement('script');
    script.src = url;
    document.head.append(script);
  }
  loadScript('https://cdnjs.cloudflare.com/ajax/libs/jszip/3.2.0/jszip.min.js');
  loadScript('https://cdnjs.cloudflare.com/ajax/libs/FileSaver.js/1.3.8/FileSaver.min.js');

  // save data into a file
  function save(zip, data, filename) {
    if (data === undefined) {
      error('No data');
      return;
    }
    let json = JSON.stringify(data);
    zip.file(filename, json);
  }

  // download collected data into a zip file
  function download(zip, version) {
    showMsg('preparing to download spec...');
    zip.generateAsync({ type: "blob" })
      .then(function(content) {
        showMsg('downloading spec...');
        saveAs(content, `${version}.zip`);
      });
  }

  // show message
  function showMsg(msg) {
    console.log('%c' + msg, 'background: #006400; color: white; font-weight: bold;');
  }

  // load spec
  function loadSpec(doit) {
    if (document.readyState == 'complete' || document.readyState == 'loaded') {
      showMsg('constructing spec...');
      doit();
    } else {
      showMsg('loading ECMASCript page...');
      window.onload = () => {
        showMsg('constructing spec...');
        doit();
      }
    }
  }

  // get elements
  function getElem(id) { return document.getElementById(id) }

  // get productions from sections
  function getSection(name) {
    return Array.from(getElem(name).children)
      .filter(child => child.tagName == Prod.tag)
      .map(Prod.from)
  }

  // get production from an element
  function getProd(name) {
    return Prod.from(getElem(name));
  }

  // show error message
  function error(msg) {
    console.error(msg);
  }

  return {
    Spec: Spec,
    Grammar: Grammar,
    Prod: Prod,
    Lhs: Lhs,
    Rhs: Rhs,
    Token: Token,
    Terminal: Terminal,
    Nonterminal: Nonterminal,
    ButNotToken: ButNotToken,
    Lookahead: Lookahead,
    Unicode: Unicode,
    splitText: splitText,
    getStep: getStep,
    getSteps: getSteps,
    getTokens: getTokens,
    getHead: getHead,
    getAlgo: getAlgo,
    norm: norm,
    loadScript: loadScript,
    save: save,
    download: download,
    showMsg: showMsg,
    loadSpec: loadSpec,
    getElem: getElem,
    getSection: getSection,
    getProd: getProd,
    error: error
  };
})();

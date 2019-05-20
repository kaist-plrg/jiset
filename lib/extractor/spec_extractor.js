Spec = (() => {
  ////////////////////////////////////////////////////////////////////////////////
  // Spec
  ////////////////////////////////////////////////////////////////////////////////
  class Spec {
    constructor(globalMethods, grammar, tys) {
      this.globalMethods = globalMethods;
      this.grammar = grammar;
      this.tys = tys;
    }

    static ES2018() {
      let globalMethods = [];
      let grammar;
      let tys = [];

      // JSZip setting
      zip = new JSZip();

      // Global Methods
      ////////////////////////////////////////////////////////////////////////////
      let globalElementIds = [
        'sec-updateempty', // 6.2.3.4
        'sec-reference-specification-type', // 6.2.4
        'sec-property-descriptor-specification-type', // 6.2.5
        'sec-data-blocks', // 6.2.7
        'sec-abstract-operations', // 7
        'sec-lexical-environment-operations', // 8.1.2
        'sec-code-realms', // 8.2
        'sec-execution-contexts', // 8.3
        'sec-jobs-and-job-queues', // 8.4
        'sec-initializehostdefinedrealm', // 8.5
        'sec-runjobs', // 8.6
        'sec-agents' // 8.7
      ];
      for (let id of globalElementIds) {
        for (let elem of getElem(id).getElementsByTagName('emu-alg')) {
          let algo = getAlgo(elem);
          if (algo) {
            globalMethods.push(algo.name);
            save(algo.code, `es2018/algorithm/${algo.name}.algorithm`);
          } else {
            console.error(elem);
          }
        }
      }

      // Grammar
      ////////////////////////////////////////////////////////////////////////////
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

      // grammar
      grammar = new Grammar(lexProds, prods)

      // Grammar Methods
      ////////////////////////////////////////////////////////////////////////////
      let semMap = {};
      for (let prod of grammar.prods) {
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
            else newNames = names;
            names = newNames;
          }
          let j = 0;
          for (name of names) {
            semMap[norm(name)] = { idx: i, subIdx: j, sem: rhs.semantics };
            j++;
          }
          i++;
        }
      }
      let grammarSections = [
        'sec-ecmascript-language-expressions', // 12
        'sec-ecmascript-language-statements-and-declarations', // 13
        'sec-ecmascript-language-functions-and-classes', // 14
        'sec-scripts' // 15.1
      ];
      let succ = 0;
      let fail = 0;
      for (let id of grammarSections) {
        let section = document.getElementById(id);
        for (let elem of section.getElementsByTagName('emu-alg')) {
          let algo = getAlgo(elem);
          if (algo) {
            let grammar = elem.previousElementSibling;
            if (grammar.tagName == 'EMU-GRAMMAR') {
              let rules = [];
              for (let prod of grammar.getElementsByTagName('emu-production')) {
                for (let rhs of prod.getElementsByTagName('emu-rhs')) {
                  let lhsName = prod.getAttribute('name');
                  let name = norm(lhsName + ':' + rhs.innerText)
                    .replace(/\[noLineTerminatorhere\]/g, '')
                    .replace(/\[lookahead[^\]]+\]/g, '')
                    .replace(/\[empty\]/g, '');
                  if (name in semMap) {
                    let obj = semMap[name];
                    let type = `${lhsName}${obj.idx}`;
                    let algoName = `${algo.name}${obj.subIdx}`;
                    obj.sem.push(algoName);
                    save(algo.code, `es2018/algorithm/${type}.${algoName}.algorithm`);
                  } else {
                    console.error(name);
                    console.error(elem);
                  }
                }
              }
            } else {
              globalMethods.push(algo.name);
              save(algo.code, `es2018/algorithm/${algo.name}.algorithm`);
            }
          }
        }
      }
      console.log(semMap);

      // Types
      ////////////////////////////////////////////////////////////////////////////
      let tyMap = {
        DeclarativeEnvironmentRecord: [
          'sec-declarative-environment-records' // 8.1.1.1
        ],
        ObjectEnvironmentRecord: [
          'sec-object-environment-records' // 8.1.1.2
        ],
        FunctionEnvironmentRecord: [
          'sec-function-environment-records' // 8.1.1.3
        ],
        GlobalEnvironmentRecord: [
          'sec-global-environment-records' // 8.1.1.4
        ],
        ModuleEnvironmentRecord: [
          'sec-module-environment-records' // 8.1.1.5
        ]
      }
      for (let tname in tyMap) {
        let methods = [];
        for (let id of tyMap[tname]) {
          for (let elem of document.getElementById(id).getElementsByTagName('emu-alg')) {
            let algo = getAlgo(elem);
            if (algo) {
              let name = `${tname}.${algo.name}`
              methods.push(algo.name);
              let lines = algo.code.split('\n');
              let code = [lines[0], '  <param>this</param>'].concat(lines.slice(1)).join('\n');
              save(code, `es2018/algorithm/${name}.algorithm`);
            } else {
              console.error(elem);
            }
          }
        }
        tys.push({
          name: tname,
          methods: methods
        });
      }

      // save ES2018 spec
      let spec = new Spec(globalMethods, grammar, tys)
      save(JSON.stringify(spec, null, 2), 'es2018/spec.json');

      download('es2018');

      return spec;
    }
  }
  ////////////////////////////////////////////////////////////////////////////////
  // Algorithm
  ////////////////////////////////////////////////////////////////////////////////
  // get steps
  function getStep(li, pre) {
    let blocks = Array.from(li.childNodes);
    let tokens = [];
    for (let block of blocks) {
      let text = block.innerText;
      switch (block.nodeName) {
        case 'CODE':
        case 'EMU-CONST':
        case 'EMU-VAL': tokens.push('<value>' + text + '</value>'); break;
        case 'VAR': tokens.push('<id>' + text + '</id>'); break;
        case 'OL': tokens.push(getStepList(block, pre)); break;
        case '#text': tokens.push(block.textContent.trim()); break;
        default: tokens.push(text.trim()); break;
      }
    }
    return pre + '<step>' + tokens.join(' ') + '</step>\n';
  }

  // get step lists
  function getStepList(ol, pre) {
    let code = '';
    code += pre + '<step-list>' +'\n';
    for (let li of ol.children) {
      code += getStep(li, pre + '  ');
    }
    code += pre + '</step-list>';
    return code;
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
    if (str.indexOf('(') == -1) name = str;
    else name = str.substring(0, str.indexOf('('));
    str = str.slice(name.length);
    name = norm(name);
    name = name.replace(/.*Semantics:/g, '');
    let arr = [];
    for (x of str.matchAll(/[^\s,()\[\]]+/g)) arr.push(x[0]);

    return {
      secno: secno,
      name: name,
      params: arr
    };
  }

  // get algorithm structures
  function getAlgo(algo) {
    try {
      let head = getHead(algo)
      let code = '';
      code += '<algorithm>' + '\n';
      head.params.forEach((param) => code += `  <param>${param}</param>` + '\n');
      code += getStepList(algo.children[0], '  ') + '\n';
      code += '</algorithm>';
      return {
        secno: head.secno,
        name: head.name,
        code: code
      };
    } catch (e) {
      console.error(e);
    }
  }
  ////////////////////////////////////////////////////////////////////////////////
  // Context-Free-Grammar (CFG)
  ////////////////////////////////////////////////////////////////////////////////
  class Grammar {
    constructor(lexProds, prods) {
      this.lexProds = lexProds;
      this.prods = prods;
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
      this.semantics = [];
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
  // Helpers
  ////////////////////////////////////////////////////////////////////////////////
  // string normalization
  function norm(str) {
    return str
      .replace(/\s+/g , '')
      .replace(/\//g , '');
  }
  // load script
  function loadScript(url) {
    let script = document.createElement('script');
    script.src = url;
    document.head.append(script);
  }
  loadScript('https://cdnjs.cloudflare.com/ajax/libs/jszip/3.2.0/jszip.min.js');
  loadScript('https://cdnjs.cloudflare.com/ajax/libs/FileSaver.js/1.3.8/FileSaver.min.js');
  var zip;

  // save data into a file
  function save(data, filename) {
    if (data === undefined) {
      console.error('No data');
      return;
    }
    let json = JSON.stringify(data, null, 2);
    zip.file(filename, data);
  }

  // download collected data into a zip file
  function download(name) {
    zip.generateAsync({ type: "blob" })
      .then(function(content) {
        saveAs(content, `${name}.zip`);
      });
  }

  // return Spec class
  return Spec;
})();
Spec.ES2018();

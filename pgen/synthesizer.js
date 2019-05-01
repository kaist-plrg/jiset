Synthesizer = (() => {
  let debug = true;
  ////////////////////////////////////////////////////////////////////////////////
  // Context-Free-Grammar (CFG)
  ////////////////////////////////////////////////////////////////////////////////
  class Grammar {
    constructor(lexProds, prods) {
      this.lexProds = lexProds;
      this.prods = prods;
      this.lexNames = new Set(this.lexProds.map(prod => prod.lhs.name));
    }

    getNode() {
      let code = '';
      code += 'package pgen.ast\n\n';
      code += 'trait Node\n\n';
      code += this.prods.map(prod => prod.getNode(this.lexNames)).join('');
      return code;
    }

    getParser() {
      let code = parserStartCode;
      code += this.lexProds.map(prod => prod.getStrParser()).join('');
      code += this.prods.map(prod => prod.getParser(this.lexNames)).join('');
      code += parserEndCode;
      return code;
    }

    toString() {
      let { lexProds, prods } = this;
      return (
        'Lexical Grammar:\n' + lexProds.map(prod => prod.toString()).join('') +
        'Grammar:\n' + prods.map(prod => prod.toString()).join('')
      );
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
          rhsList.push(new Rhs([Token.from(tokenElem)]));
        }
      } else {
        for (let rhsElem of rhsElemList) {
          rhsList.push(Rhs.from(rhsElem));
        }
      }

      return new Prod(lhs, rhsList);
    }

    getNode(lexNames) {
      let { lhs, rhsList } = this;
      let type = lhs.name;
      let code = `trait ${type} extends Node` + '\n';
      for (let i = 0; i < rhsList.length; i++) {
        let params = rhsList[i].getNodeParams(lexNames);
        let string = rhsList[i].getString();
        if (params.length > 0) {
          code += `case class ${lhs.name}${i}(${params.join(', ')}) extends ${type}`;
        } else code += `case object ${lhs.name}${i} extends ${type}`;
        code += ' {' + '\n';
        code += '  override def toString: String = {' + '\n';
        code += `    s"${string}"` + '\n';
        code += '  }' + '\n';
        code += '}' + '\n';
      }
      return code;
    }

    getStrParser() {
      let code = '';
      let name = this.lhs.name;
      let subName = 'sub' + name;

      let main = [];
      let sub = [];
      let i = 0;
      for (let rhs of this.rhsList) {
        let arr;
        let tokens = rhs.tokens;
        if (rhs.tokens[0].name == name) {
          arr = sub;
          tokens = tokens.slice(1);
        } else arr = main;
        let parser = `seq(${tokens.map(token => token.getStrParser()).join(', ')}, ${subName})`;
        // if (debug) parser = `log(${parser})("${name + i}")`;
        arr.push('    ' + parser + ' |||\n');
        i++;
      }

      code += `  lazy val ${name}: Parser[String] =` + '\n';
      code += main.join('');
      code += `    STR_MISMATCH` + '\n';
      code += `  lazy val ${subName}: Parser[String] =` + '\n';
      code += sub.join('');
      code += `    STR_MATCH` + '\n';
      return code;
    }

    getParser(lexNames) {
      let code = '';
      let name = this.lhs.name;
      let subName = 'sub' + name;
      let params = this.lhs.params;
      let lookahead = '';

      let main = '';
      let sub = '';
      let j = 0, k = 0;

      function appendParsers(base, tokens) {
        if (tokens.length == 0) return base;
        let head = tokens[0];
        let tail = tokens.slice(1);
        let parser = head.appendParserTo(base, lexNames);
        return appendParsers(parser, tail);
      }

      function getParsers(tokens, cond, idx, isSub) {
        let parser = appendParsers('MATCH', tokens);
        let argStrList = params? '(' + params.join(', ') + ')' : '';
        parser = `${parser} ~ ${subName}${argStrList} ^^ { case `;
        let count = tokens.filter(token => token instanceof Nonterminal || token instanceof ButNotToken).length;
        let ids = Array.from(Array(count).keys()).map(i => 'x' + i );
        let astName = name + idx;

        if (isSub) {
          if (ids.length == 0) parser += `_ ~ y => ((x: ast.${name}) => y(ast.${astName}(x))) }`;
          else parser += `_ ~ ${ids.join(' ~ ')} ~ y => ((x: ast.${name}) => y(ast.${astName}(x, ${ids.join(', ')}))) }`;
        } else {
          if (ids.length == 0) parser += `_ ~ y =>  y(ast.${astName}) }`;
          else parser += `_ ~ ${ids.join(' ~ ')} ~ y =>  y(ast.${astName}(${ids.join(', ')})) }`;
        }

        let debugMessage = `"""construct ${astName}"""`
        if (cond) parser = `(if(${cond}) ${parser} else MISMATCH)`;
        if (debug) parser = `log(${parser})("${name + idx}")`;
        return `    ${parser} |` + '\n';
      }

      for (let i = 0; i < this.rhsList.length; i++) {
        let rhs = this.rhsList[i];
        let cond = rhs.cond
        let tokens = rhs.tokens;
        if (rhs.tokens[0].name == name) {
          tokens = tokens.slice(1);
          sub += getParsers(tokens, cond, i, true);
        } else {
          main  += getParsers(tokens, cond, i, false);
        }
      }

      let pre = 'lazy val';
      let post;
      if (!params) {
        post = `0[ast.${name}] = {`;
      } else {
        let paramLen = params.length
        let paramStr = '(' + params.join(', ' ) + ')'
        post = `${paramLen}[ast.${name}] = memo { case ${paramStr} =>`;
      }

      code += `  ${pre} ${name}: P${post}` + '\n';
      code += main;
      code += `    MISMATCH` + '\n';
      code += `  }` + '\n';
      code += `  ${pre} sub${name}: R${post}` + '\n';
      code += sub;
      code += `    MATCH ^^^ { x => x }` + '\n';
      code += `  }` + '\n';
      return code;
    }

    toString() {
      let { lhs, rhsList } = this;
      return '  ' + lhs.toString() + ' ::\n' + rhsList.map(rhs => rhs.toString()).join('');
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
      let params;
      if (paramsAttr != null) params = paramsAttr.split(',').map(x => 'p' + x.trim());

      return new Lhs(name, params);
    }

    toString() {
      let { name, params } = this;
      if (params) return `${name}[${params.join(', ')}]`;
      return name;
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
      let cond;
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

    getNodeParams(lexNames) {
      let tokens = this.tokens;
      let params = [];
      for (let i = 0; i < tokens.length; i++) {
        let paramType = tokens[i].getType();
        if (lexNames.has(paramType)) paramType = 'String';
        if (paramType !== undefined) {
          params.push(`x${i}: ${paramType}`);
        }
      }
      return params;
    }

    getString() {
      let tokens = this.tokens;
      let strings = [];
      for (let i = 0; i < tokens.length; i++) {
        let token = tokens[i];
        let str = token.getString();
        if (str === undefined) {
          let name = `x${i}`;
          str = '${' + name;
          if (token.optional) str += '.getOrElse("")';
          str += '}';
          strings.push(str);
        } else if (str != "") strings.push(str);
      }
      return strings.join(' ');
    }

    toString() {
      let { cond, tokens } = this;
      if (cond) return `    [${cond}] ${tokens.map(token => token.toString()).join(' ')}` + '\n';
      else return `    ${tokens.map(token => token.toString()).join(' ')}` + '\n';
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
          if (text == EmptyToken.text) return EmptyToken;
          if (text == NoLineTerminatorToken.text) return NoLineTerminatorToken;
          if (text == UnicodeAny.text) return UnicodeAny;
          if (text == UnicodeIdStart.text) return UnicodeIdStart;
          if (text == UnicodeIdContinue.text) return UnicodeIdContinue;
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

    getType() { return undefined; }

    getString() { return undefined; }

    getStrParser() { throw `unhandled Token.getStrParser: ${this}`; }

    appendParserTo(base, lexNames) { return `unhandled Token.appendParserTo: ${this}`; }

    toString() {
      if (this === EmptyToken ||
        this === NoLineTerminatorToken ||
        this === UnicodeAny ||
        this === UnicodeIdStart ||
        this === UnicodeIdContinue) return this.text;
      else this.toString();
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Terminal
  ////////////////////////////////////////////////////////////////////////////////
  class Terminal extends Token {
    static tag = 'EMU-T'

    constructor(name) {
      super();
      this.name = name;
    }

    static from(elem) {
      return new Terminal(elem.innerText.trim());
    }

    getString() { return this.name; }

    getStrParser() {  return `"${norm(this.name)}"`; }

    appendParserTo(base, lexNames) { return `(${base} <~ term(${this.getStrParser()}))` }

    toString() { return this.name; }
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
      let args;
      if (argsAttr != null) args = argsAttr.split(',').map(str => {
        str = str.trim();
        switch (str[0]) {
          case '+': return true;
          case '~': return false;
          case '?': return 'p' + str.substr(1);
          default: throw 'unexpected prefix: ' + str[0];
        }
      });

      let optAttr = elem.getAttribute('optional');
      let optional = optAttr != null;

      return new Nonterminal(name, args, optional);
    }

    getType() {
      let { name, optional } = this;
      if (optional) return `Option[${name}]`;
      else return name;
    }

    getStrParser() {
      let parser = this.name;
      if (this.optional) return `strOpt(${parser})`;
      else return parser;
    }

    appendParserTo(base, lexNames) {
      let parser = this.name;
      if (lexNames.has(parser)) parser = `term("${parser}", ${parser})`;
      else if (this.args) parser += `(${this.args.join(', ')})`;
      if (this.optional) parser = `opt(${parser})`;
      return `${base} ~ ${parser}`;
    }

    toString() {
      let code = this.name;
      if (this.args) code += `[${this.args.join(', ')}]`;
      if (this.optional) code += '_opt';
      return code;
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

    getType() { return this.base.getType(); }

    getStrParser() {
      let parser = this.base.getStrParser();
      let notParser = this.cases.map(token => token.getStrParser()).join(' ||| ');
      return `(${parser} \\ ${notParser})`;
    }

    appendParserTo(base, lexNames) {
      let parser = this.getStrParser();
      return `${base} ~ term("""${parser}""", ${parser})`
    }

    toString() { return `${this.base} but not [${this.cases.map(c => c.toString()).join(', ')}]`; }
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

    get symbol() {
      if (this.contains) return '∈';
      else return '∉';
    }

    getString() { return ''; }

    getStrParser() {
      let parser = this.cases.map(c => `seq(${c.map(token => token.getStrParser()).join(', ')})`).join(' ||| ');
      if (this.contains) return `"" <~ +(${parser})`;
      else return `"" <~ -(${parser})`;
    }

    appendParserTo(base, lexNames) {
      let parser = this.cases.map(c => `seq(${c.map(token => token.getStrParser()).join(', ')})`).join(' | ');
      if (this.contains) return `(${base} <~ +term("", ${parser}))`;
      else return `(${base} <~ -term("", ${parser}))`;
    }

    toString() { return `[lookahead ${this.symbol} { ${this.cases.map(c => c.toString()).join(', ')} }]`; }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Special Tokens
  ////////////////////////////////////////////////////////////////////////////////
  // predefined unicodes
  class Unicode extends Token {
    constructor(name) {
      super();
      this.name = name;
    }

    getStrParser() { return this.name; }

    toString() { return `<${this.name}>`; }
  }

  // empty token
  let EmptyToken = new Token;
  EmptyToken.text = '[empty]';
  EmptyToken.getStrParser = () => { return 'STR_MATCH'; }
  EmptyToken.appendParserTo = (base, lexNames) => { return `${base} ~ MATCH`; }
  EmptyToken.getString = () => { return ''; }

  // no line terminator token
  let NoLineTerminatorToken = new Token;
  NoLineTerminatorToken.text = '[no LineTerminator here]';
  NoLineTerminatorToken.getStrParser = () => { return `strNoLineTerminator` }
  NoLineTerminatorToken.appendParserTo = (base, lexNames) => { return `(${base} <~ NoLineTerminator)` }
  NoLineTerminatorToken.getString = () => { return ''; }

  // any Unicode code point
  let UnicodeAny = new Token;
  UnicodeAny.text = 'any Unicode code point';
  UnicodeAny.getStrParser = () => { return 'Unicode'; }

  // any Unicode code point with the Unicode property “ID_Start”
  let UnicodeIdStart = new Token;
  UnicodeIdStart.text = 'any Unicode code point with the Unicode property “ID_Start”';
  UnicodeIdStart.getStrParser = () => { return 'IDStart'; }

  // any Unicode code point with the Unicode property “ID_Continue”
  let UnicodeIdContinue = new Token;
  UnicodeIdContinue.text = 'any Unicode code point with the Unicode property “ID_Continue”';
  UnicodeIdContinue.getStrParser = () => { return 'IDContinue'; }

  ////////////////////////////////////////////////////////////////////////////////
  // Helper functions
  ////////////////////////////////////////////////////////////////////////////////
  function norm(str) {
    return str.replace(/\\/g, "\\\\").replace(/"/g, "\\\"");
  }

  let parserStartCode = `package pgen

object Parser extends ESParsers {
`;

  let parserEndCode = `}`;

  ////////////////////////////////////////////////////////////////////////////////
  // Synthesize ASTs / Parsers
  ////////////////////////////////////////////////////////////////////////////////
  return {
    Lhs: Lhs,
    Prod: Prod,
    get ES2018() {
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
      removedElems = [];
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

      // get grammar
      let grammar = new Grammar(lexProds, prods)
      for (let name of spaceNames) grammar.lexNames.add(name);

      return grammar;
    }
  }
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

// get ES2018 grammar
grammar = Synthesizer.ES2018;

node = grammar.getNode();
save(node, 'Node.scala');

parser = grammar.getParser();
save(parser, 'Parser.scala');

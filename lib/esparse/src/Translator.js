const acorn = require('acorn');
const { init, addComma } = require('./Global');

class Translator {
  constructor(ecmaVersion) {
    this.ecmaVersion = ecmaVersion;
    this.Script = require(`./es${this.ecmaVersion}/Script`);
  }

  acornParse(code) {
    try {
      init(code);
      return acorn.parse(code, {
        ecmaVersion: this.ecmaVersion,
        preserveParens: true,
        locations: true,
        onTrailingComma: (offset, _) => addComma(offset)
      });
    } catch (e) {
      console.error(`[SyntaxError] ${e}`);
    }
  }

  trans(acornAst) {
    return this.Script(acornAst);
  }

  parse(code) {
    const acornAst = this.acornParse(code);
    return this.trans(acornAst);
  }
}

module.exports = Translator;

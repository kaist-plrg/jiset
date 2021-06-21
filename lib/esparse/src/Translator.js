const acorn = require('acorn');
const fs = require('fs');

class Translator {
  constructor(ecmaVersion) {
    this.ecmaVersion = ecmaVersion;
    this.Script = require(`./es${this.ecmaVersion}/Script`);
  }

  acornParse(code) {
    try {
      return acorn.parse(code, {
        ecmaVersion: this.ecmaVersion,
        preserveParens: true,
        locations: true,
      });
    } catch (e) {
      console.error(`[SyntaxError] ${e}`);
      process.exit(1);
    }
  }

  trans(acornAst) {
    return this.Script(acornAst);
  }

  parse(code) {
    const acornAst = this.acornParse(code);
    return this.trans(acornAst);
  }

  parseFile(filename) {
    const code = fs.readFileSync(filename, 'utf8');
    return this.parse(code);
  }
}

module.exports = Translator;

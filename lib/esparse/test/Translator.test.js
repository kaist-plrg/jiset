const fs = require('fs');
const assert = require('assert');
const { check } = require('../src/checker');
const Translator = require('../src/Translator');
const EXT = '.js';
const EXT_LEN = EXT.length;
const RESET = '\033[0m';
const TEST_DIR = '../../tests/esparse';

describe('Translator', function() {
  describe('es2021', function() {
    let logfile = '.test.log';
    if (fs.existsSync(logfile)) {
      fs.unlinkSync(logfile);
    }
    let translator = new Translator('2021');
    let todoList = new Set();
    let notyet = 0;
    for (filename of fs.readdirSync(TEST_DIR)) {
      if (!filename.endsWith(EXT)) continue;
      let name = filename.slice(0, -EXT_LEN);
      let jsPath = `${TEST_DIR}/${name}.js`;
      let astPath = `${TEST_DIR}/${name}.json`;
      if (fs.existsSync(astPath)) {
        let parsed;
        try {
          const code = fs.readFileSync(jsPath, 'utf8');
          parsed = translator.parse(code);
          it(`should successfully parse ${name}.js`, () => {
            let expected = JSON.parse(fs.readFileSync(astPath, 'utf8'));
            check(parsed, expected, 'AST');
          });
        } catch(e) {
          let msg = e.toString();
          notyet++;
          if (msg.startsWith('[TODO]')) todoList.add(msg);
          else throw e;
        }
      }
    }
    let logs = Array.from(todoList);
    if (notyet > 0) logs.push(`* ${notyet} tests have features not yet implemented.`);
    fs.writeFileSync(logfile, logs.join('\n'), 'utf8');
  });
});

const fs = require('fs');
const { target, file, detail, isString } = require('./args');
const Translator = require('./Translator');

// loading translator
const translator = new Translator(target);

let code;
if (isString) {
  code = file
} else {
  code = fs.readFileSync(file, 'utf8');
}
if (detail) {
  console.log("- given JavaScript program");
  console.log("----------------------------------------");
  console.log(code);
  console.log("----------------------------------------");
}

// parse a given file using acorn
let acornAst = translator.acornParse(code);
if (detail) {
  console.log("- AST produced by `acorn`");
  console.log("----------------------------------------");
  console.log(JSON.stringify(acornAst, null, 2));
  console.log("----------------------------------------");
}

// translate acorn AST to ECMAScript AST
const ast = translator.trans(acornAst);
if (detail) {
  console.log("- AST produced by `esparse`");
  console.log("----------------------------------------");
  console.log(JSON.stringify(ast));
  console.log("----------------------------------------");
  console.log("- compressed AST produced by `esparse`");
  console.log("----------------------------------------");
}
console.log(JSON.stringify(ast.compress()));
if (detail) {
  console.log("----------------------------------------");
}

const babel = require("@babel/core");
const fs = require("fs");
const path = require("path");

// compilation options
const options = {
  presets: [
    ["@babel/preset-env", {
      "spec": true,
      "useBuiltIns": false,
      "modules": false,
    }],
  ],
};

// XXX test for sample.js
// console.log(babel.transformFileSync("sample.js", options).code);

// load $JISET_HOME
const baseDir = process.env.JISET_HOME;

// read target Test262 programs listed in `tests/analyze-test262`
const targetsPath = `${baseDir}/tests/analyze-test262`
const targets = fs.readFileSync(targetsPath, "utf8").trim().split("\n");

// error lists
const errors = [];

// make a directory to dump compiled Test262 programs
const resultDir = `${baseDir}/logs/compile/test262`;
const harnessResultDir = `${baseDir}/logs/compile/harness`;

// a directory for Test262
const test262Dir = `${baseDir}/tests/test262/test`;
const harnessDir = `${baseDir}/tests/test262/harness`;

// compile harness programs
console.log("* compile harness programs.");
fs.mkdirSync(harnessResultDir, { recursive: true }, e => { throw e; });
fs.readdirSync(harnessDir).forEach(target => {
  if (!target.endsWith(".js")) return;
  try {
    // compile programs
    const harnessPath = `${harnessDir}/${target}`;
    const compiled = babel.transformFileSync(harnessPath, options).code;

    // dump compiled programs
    const resultPath = `${harnessResultDir}/${target}`;
    fs.writeFileSync(resultPath, compiled);
  } catch(e) {
    console.warn(`[Babel Failed] ${target}`);
  }
});

// compile target Test262 programs
console.log(`* compile ${targets.length} Test262 programs.`);
targets.map(target => {
  try {
    // compile programs
    const test262Path = `${test262Dir}/${target}`;
    const compiled = babel.transformFileSync(test262Path, options).code;

    // dump compiled programs
    const resultPath = `${resultDir}/${target}`;
    fs.mkdirSync(path.dirname(resultPath), { recursive: true }, e => { throw e; });
    fs.writeFileSync(resultPath, compiled);
  } catch (error) {
    console.warn(`[Babel Failed] ${target}`);
    errors.push({ target, error });
  }
});
const errorPath = `${baseDir}/logs/compile/errors`;
fs.writeFileSync(errorPath, JSON.stringify(errors, null, 2));

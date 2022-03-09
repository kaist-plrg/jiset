const fs = require("fs");
const path = require("path");
const child_process = require("child_process");

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

// compile programs
function compile(from, to) {
  fs.copyFileSync(from, "./input.temp.js");
  child_process.execSync("npx webpack");
  fs.copyFileSync("./output.temp.js", to);
}

// compile harness programs
console.log("* compile harness programs.");
fs.mkdirSync(harnessResultDir, { recursive: true }, e => { throw e; });
idx = 0;
fs.readdirSync(harnessDir).forEach(target => {
  if (!target.endsWith(".js")) return;
  try {
    // compile programs
    const harnessPath = `${harnessDir}/${target}`;
    const resultPath = `${harnessResultDir}/${target}`;
    compile(harnessPath, resultPath)
    console.log(`[${idx}] compiled - ${target}`);
  } catch(e) {
    console.log(`[${idx}] failed - ${target}`);
  }
  idx++;
});

// compile target Test262 programs
console.log(`* compile ${targets.length} Test262 programs.`);
idx = 0;
targets.forEach(target => {
  try {
    // compile programs
    const test262Path = `${test262Dir}/${target}`;
    const resultPath = `${resultDir}/${target}`;
    fs.mkdirSync(path.dirname(resultPath), { recursive: true }, e => { throw e; });
    compile(test262Path, resultPath)
    console.log(`[${idx}] compiled - ${target}`);
  } catch (error) {
    console.log(`[${idx}] failed - ${target}`);
    errors.push({ target, error });
  }
  idx++;
});
const errorPath = `${baseDir}/logs/compile/errors`;
fs.writeFileSync(errorPath, JSON.stringify(errors, null, 2));

const babel = require("@babel/core");
const fs = require("fs");

const baseDir = process.env.JISET_HOME
const targetsPath = `${baseDir}/tests/analyze-test262`
const targets = fs.readFileSync(targetsPath, "utf8").trim().split("\n");
const test262Path = `${baseDir}/tests/test262/test`;
const options = { presets: [ ["@babel/preset-env", {"useBuiltIns":"entry", "corejs":3}] ] };
const results = targets.map(target => {
  const path = `${test262Path}/${target}`;
  console.log("----------------------------------------");
  console.log(path);
  const compiled = babel.transformFileSync(path, options).code;
  console.log(compiled);
  return { path, compiled };
});

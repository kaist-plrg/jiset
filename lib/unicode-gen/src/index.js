const fs = require('fs');
const { property, filename } = require('./args');

function read(property) {
  let path = `@unicode/unicode-13.0.0/Binary_Property/${property}/code-points.js`;
  try {
    return require(path);
  } catch {
    console.error(`improper name of Unicode property: ${property}`);
    process.exit(1);
  }
}

function compress(cps) {
  let result = [];
  let cur;
  let prev = -1;
  for (let cp of cps) {
    if (prev == -1 || prev + 1 != cp) {
      cur = [cp,cp];
      result.push(cur);
    } else {
      cur[1] = cp;
    }
    prev = cp;
  }
  for (let i = 0; i < result.length; i++) {
    let cur = result[i];
    if (cur[0] == cur[1]) result[i] = cur[0];
  }
  return result;
}

const cps = read(property);
const result = compress(cps);
const json = JSON.stringify(result);
fs.writeFileSync(filename, json);

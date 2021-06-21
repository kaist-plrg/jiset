const assert = require('assert');
const Node = require('./Node');
const { isArray } = Array;

function showTarget(f, target) {
  try {
    f()
  } catch (e) {
    e.stack = (
      `\n- TARGET: ${target}`
    );
    throw e;
  }
}

function checkEqual(given, expected, target) {
  showTarget(() => assert.equal(given, expected), target);
}

function checkArray(given, target) {
  showTarget(() => {
    if (!isArray(given)) {
      assert.fail(`Not an array: ${given}`);
    }
  }, target);
}

function checkNode(given, target) {
  showTarget(() => {
    if (!(given instanceof Node)) {
      assert.fail(`Not a node: ${given}`);
    }
  }, target);
}

function check(given, expected, target) {
  if (isArray(expected)) {
    checkNode(given, target);
    let { kind, index, children, params } = given;

    target += `.${kind}`;

    // check index
    checkEqual(index, expected[0], target + '.index');

    target += `[${index}]`;

    // check parser parameters
    checkElems(params, expected[2].map(x => x == 1), target + '.params');

    // check children
    checkElems(children, expected[1], target);
  } else {
    checkEqual(given, expected, target);
  }
}

function checkElems(given, expected, target) {
  checkArray(given, target);
  checkEqual(given.length, expected.length, target + '.length');
  for (let i = 0; i < expected.length; i++) {
    check(given[i], expected[i], `${target}[${i}]`);
  }
}

module.exports = {
  check,
};

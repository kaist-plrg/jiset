const assert = require('assert');

const acorn = require('acorn')
const walk = require('acorn-walk')
const config = {
  ecmaVersion: 2021
};

describe('acorn', function() {
  describe('parse', function() {
    it('should successfully parse `var x = 42;`', function() {
      acorn.parse(`var x = 42;`, config);
    });
    it('should be failed to parse `var x = ;`', function() {
      assert.throws(() => acorn.parse(`var x = ;`, config));
    });
    it('should successfully parse es2021 features', function() {
      acorn.parse(`x ||= y;`, config);
      acorn.parse(`x &&= y;`, config);
      acorn.parse(`x ??= y;`, config);
      acorn.parse(`const billGatesNetWorth = 1_200_044_555;`, config);
    });
  });
});

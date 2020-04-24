#8 https://github.com/tc39/ecma262/pull/1967
- 18 Global Object의 [[Prototype]]은 다음과 같이 정의됨
  "has a [[Prototype]] internal slot whose value is implementation-dependent."
  그런데, 다음의 test들은 Object.prototype을 가지는 것처럼 검사를 하고 있음.
  - test262/test/built-ins/Object/defineProperty/15.2.3.6-4-625gs.js
  - test262/test/built-ins/Object/getPrototypeOf/15.2.3.2-2-30.js
  - test262/test/built-ins/decodeURI/S15.1.3.1_A5.5.js
  - test262/test/built-ins/decodeURIComponent/S15.1.3.2_A5.5.js
  - test262/test/built-ins/encodeURI/S15.1.3.3_A5.5.js
  - test262/test/built-ins/encodeURIComponent/S15.1.3.4_A5.5.js
  - test262/test/built-ins/eval/prop-desc-enumerable.js
  - test262/test/built-ins/parseFloat/S15.1.2.3_A7.5.js
  - test262/test/built-ins/parseInt/S15.1.2.2_A9.5.js
  - test262/test/language/global-code/decl-lex.js

#0
try3 : 2020에서는 step에 super 쓰는지 검사하는 syntaxError test가 있던게 다
early Error로 숨겨져있을 시점이라 안되는게 맞음 18.2.1.1Runtime Semantics:
PerformEval If inDerivedConstructor is false, and body Contains SuperCall,
throw a SyntaxError exception.  이게 2019에 없음

#1 https://github.com/tc39/ecma262/pull/1456
- create: https://github.com/tc39/ecma262/pull/1250
- await : Await 함수 등 에서 promiseResolve의 2nd argument를 잘못 전달하는 듯
- 6.2.3.1Await
  Let promise be ?  PromiseResolve(%Promise%, value).
- 2019에는
  Let promise be ?  PromiseResolve(%Promise%, « value »).

#2 https://github.com/tc39/ecma262/pull/1915
- ForInOfHeadEvaluation에서 Assert: iterationKind is iterate
- pull request 날려서 확인받고 merge 됨

#3 https://github.com/tc39/ecma262/pull/1403
- 12.11.3Runtime Semantics: Evaluation EqualityExpression !=
  RelationalExpression 에서 Abstract Equality Comparison 한 후 RetrnIfAbrupt
  없음
- 다른 사람이 고쳐서 올려두었음

#4 https://github.com/tc39/ecmarkup/pull/165
- 14.1.12 Static Semantics: IsFunctionDefinition에서 BindingIdentifier가 없는
  경우가 정의가 안되어 있었음
- 다른 사람이 ecmarkup에 bug를 찾아서 해결함

#5 https://github.com/tc39/ecma262/pull/1284
- 13.7.5.8 Static Semantics: VarScopedDeclarations에서 다음이 두번 나옴
  for await ( var ForBinding of AssignmentExpression ) Statement
- 서로 다른 semantics를 가져서 혼란을 야기함
- 실제로는 두번째 경우여야 했는데 다른 사람이 고쳐줘서 수정됨

#6 https://github.com/tc39/ecma262/pull/1301
- 14.1.7 Static Semantics: ExpectedArgumentCount에서 다음의 두 경우가 없음
  FormalParameters: FunctionRestParameter
  FormalParameterList: FormalParameter
- 다른 사람이 고쳐줘서 수정됨

#7 https://github.com/tc39/ecma262/pull/1752
- -0와 =를 통해서 비교하면 +0도 같아져서 문제가 발생함
- Integer-Indexed exotic objects와 관련해서는 위의 pull request로 해결됨
- 이 부분이 inessential builtin 부분이어서 안잡힐 거 같음
- 하지만 String 관련은 아직 해결이 안되었으니 pull request 해야됨

#8 YET
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

#9 https://github.com/tc39/ecma262/pull/1954
- 12.4.{4,5,6,7} oldValue로 변수를 정의해놓고, oldvalue로 사용
- 가장 최근 버전에도 존재하는 버그
- oldValue로 4개의 이름을 바꾸고, pull request를 날려둠

#10 https://github.com/tc39/ecma262/pull/1955
- 6.1.6.1.11 Number::unsignedRightShift ( x, y )에서 lnum을 구할 때,
  ToInt32를 해버려서 문제가 발생함
- 가장 최근 버전에도 존재하는 버그
- ToUint32를 사용하도록 수정 후, pull request를 날려둠

#11 https://github.com/tc39/ecma262/pull/1956
- 6.1.6.1.16 NumberBitwiseOp ( op, x, y )에서 rnum을 구할 때,
  ToUint32를 해버려서 문제가 발생함
- 가장 최근 버전에도 존재하는 버그
- ToInt32를 사용하도록 수정 후, pull request를 날려둠

#12 https://github.com/tc39/ecma262/pull/1766
- 20.1.1.1 Number ( value )에서 바로 value에다가 ToNumber를 해버려서
  BigInt인 경우에 문제가 발생함
- 이미 현재 버전에서는 위의 pull request로 해결이 됨

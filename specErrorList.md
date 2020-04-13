#0
try3 : 2020에서는 step에 super 쓰는지 검사하는 syntaxError test가 있던게 다
early Error로 숨겨져있을 시점이라 안되는게 맞음 18.2.1.1Runtime Semantics:
PerformEval If inDerivedConstructor is false, and body Contains SuperCall,
throw a SyntaxError exception.  이게 2019에 없음

#1 https://github.com/tc39/ecma262/pull/1456
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

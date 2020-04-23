#5 https://github.com/tc39/ecma262/pull/1284
- create : https://github.com/tc39/ecma262/pull/1066
- 13.7.5.8 Static Semantics: VarScopedDeclarations에서 다음이 두번 나옴
  for await ( var ForBinding of AssignmentExpression ) Statement
- 서로 다른 semantics를 가져서 혼란을 야기함
- 실제로는 두번째 경우여야 했는데 다른 사람이 고쳐줘서 수정됨

## failed tests list()

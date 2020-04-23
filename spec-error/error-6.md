#6 https://github.com/tc39/ecma262/pull/1301
- create : https://github.com/tc39/ecma262/pull/711
- 14.1.7 Static Semantics: ExpectedArgumentCount에서 다음의 두 경우가 없음
  FormalParameters: FunctionRestParameter
  FormalParameterList: FormalParameter
- 원래는 없었던 버그였지만, FormalsList라는 non-terminal 을 삭제하고 FormalParameterList로 대체하는 과정에서 기존의 static semantics 가 삭제됨
- 다른 사람이 고쳐줘서 수정됨

## failed tests list()

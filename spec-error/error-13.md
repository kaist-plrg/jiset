#13 https://github.com/tc39/ecma262/pull/1966
- create : es6 , June 2015
- 13.6The if Statement의 syntax가 ambiguous한데 이를 그냥 말로 else가
  가장 가까운 if에 붙어야 한다고 되어있었음
- 이를 unambiguous하도록 바꿔서 pull request를 날림

## failed tests list(1)
- /test/language/statements/if/S12.5_A12_T3: Fail

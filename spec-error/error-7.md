#7 https://github.com/tc39/ecma262/pull/1963
- create : es6 , June 2015
- -0와 =를 통해서 비교하면 +0도 같아져서 문제가 발생함
- Integer-Indexed exotic objects와 관련해서는 다음의 pull request로 해결됨
  https://github.com/tc39/ecma262/pull/1963
  그런데 이 부분은 inessential builtin 부분이어서 안잡힐 거 같음
- 하지만 String 관련은 아직 해결이 안되어서 pull request를 보냈고,
  test로도 도달할것으로 예상이 돼서 한번 구현을 수정한 후에 돌려봐야할 것 같음

## failed tests list

#7 https://github.com/tc39/ecma262/pull/1752
- create : es6 , June 2015
- -0와 =를 통해서 비교하면 +0도 같아져서 문제가 발생함
- Integer-Indexed exotic objects와 관련해서는 위의 pull request로 해결됨
- 이 부분이 inessential builtin 부분이어서 안잡힐 거 같음
- 하지만 String 관련은 아직 해결이 안되었으니 pull request 해야됨

## failed tests list

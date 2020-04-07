
#0

try3 : 2020에서는 step에 super 쓰는지 검사하는 syntaxError test가 있던게 다 early Error로 숨겨져있을 시점이라 안되는게 맞음

18.2.1.1Runtime Semantics: PerformEval

If inDerivedConstructor is false, and body Contains SuperCall, throw a SyntaxError exception.

이게 2019에 없음

#1

await : Await 함수 등 에서 promiseResolve의 2nd argument를 잘못 전달하는 듯 [https://github.com/jmdyck/ecma262/commit/306cc93a4f97db0ff5a37c58ee55a6e50eff4607](https://github.com/jmdyck/ecma262/commit/306cc93a4f97db0ff5a37c58ee55a6e50eff4607)

6.2.3.1Await

Let promise be ? PromiseResolve(%Promise%, value).

2019에는

Let promise be ? PromiseResolve(%Promise%, « value »).

#2

ForInOfHeadEvaluation에서 Assert: iterationKind is iterate

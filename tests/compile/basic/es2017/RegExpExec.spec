            1. Assert: Type(_R_) is Object.
            1. Assert: Type(_S_) is String.
            1. Let _exec_ be ? Get(_R_, `"exec"`).
            1. If IsCallable(_exec_) is *true*, then
              1. Let _result_ be ? Call(_exec_, _R_, « _S_ »).
              1. If Type(_result_) is neither Object or Null, throw a *TypeError* exception.
              1. Return _result_.
            1. If _R_ does not have a [[RegExpMatcher]] internal slot, throw a *TypeError* exception.
            1. Return ? RegExpBuiltinExec(_R_, _S_).
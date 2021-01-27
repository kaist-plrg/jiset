          1. If Type(_ref_) is Reference, then
            1. If IsPropertyReference(_ref_) is *true*, then
              1. Let _thisValue_ be GetThisValue(_ref_).
            1. Else,
              1. Assert: the base of _ref_ is an Environment Record.
              1. Let _refEnv_ be GetBase(_ref_).
              1. Let _thisValue_ be _refEnv_.WithBaseObject().
          1. Else,
            1. Let _thisValue_ be *undefined*.
          1. Let _argList_ be ? ArgumentListEvaluation of _arguments_.
          1. If Type(_func_) is not Object, throw a *TypeError* exception.
          1. If IsCallable(_func_) is *false*, throw a *TypeError* exception.
          1. If _tailPosition_ is *true*, perform PrepareForTailCall().
          1. Let _result_ be Call(_func_, _thisValue_, _argList_).
          1. Assert: If _tailPosition_ is *true*, the above call will not return here, but instead evaluation will continue as if the following return has already occurred.
          1. Assert: If _result_ is not an abrupt completion, then Type(_result_) is an ECMAScript language type.
          1. Return _result_.
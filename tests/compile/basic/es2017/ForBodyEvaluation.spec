          1. Let _V_ be *undefined*.
          1. Perform ? CreatePerIterationEnvironment(_perIterationBindings_).
          1. Repeat,
            1. If _test_ is not ~[empty]~, then
              1. Let _testRef_ be the result of evaluating _test_.
              1. Let _testValue_ be ? GetValue(_testRef_).
              1. If ToBoolean(_testValue_) is *false*, return NormalCompletion(_V_).
            1. Let _result_ be the result of evaluating _stmt_.
            1. If LoopContinues(_result_, _labelSet_) is *false*, return Completion(UpdateEmpty(_result_, _V_)).
            1. If _result_.[[Value]] is not ~empty~, set _V_ to _result_.[[Value]].
            1. Perform ? CreatePerIterationEnvironment(_perIterationBindings_).
            1. If _increment_ is not ~[empty]~, then
              1. Let _incRef_ be the result of evaluating _increment_.
              1. Perform ? GetValue(_incRef_).
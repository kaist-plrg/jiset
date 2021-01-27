          1. Let _V_ be *undefined*.
          1. Repeat
            1. Let _exprRef_ be the result of evaluating |Expression|.
            1. Let _exprValue_ be ? GetValue(_exprRef_).
            1. If ToBoolean(_exprValue_) is *false*, return NormalCompletion(_V_).
            1. Let _stmt_ be the result of evaluating |Statement|.
            1. If LoopContinues(_stmt_, _labelSet_) is *false*, return Completion(UpdateEmpty(_stmt_, _V_)).
            1. If _stmt_.[[Value]] is not ~empty~, let _V_ be _stmt_.[[Value]].
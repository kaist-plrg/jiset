          1. Let _V_ be *undefined*.
          1. Repeat,
            1. Let _exprRef_ be the result of evaluating |Expression|.
            1. Let _exprValue_ be ? GetValue(_exprRef_).
            1. If ToBoolean(_exprValue_) is *false*, return NormalCompletion(_V_).
            1. Let _stmtResult_ be the result of evaluating |Statement|.
            1. If LoopContinues(_stmtResult_, _labelSet_) is *false*, return Completion(UpdateEmpty(_stmtResult_, _V_)).
            1. If _stmtResult_.[[Value]] is not ~empty~, set _V_ to _stmtResult_.[[Value]].
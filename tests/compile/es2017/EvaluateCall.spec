          1. Let _func_ be ? GetValue(_ref_).
          1. If Type(_ref_) is Reference, then
            1. If IsPropertyReference(_ref_) is *true*, then
              1. Let _thisValue_ be GetThisValue(_ref_).
            1. Else the base of _ref_ is an Environment Record,
              1. Let _refEnv_ be GetBase(_ref_).
              1. Let _thisValue_ be _refEnv_.WithBaseObject().
          1. Else Type(_ref_) is not Reference,
            1. Let _thisValue_ be *undefined*.
          1. Return ? EvaluateDirectCall(_func_, _thisValue_, _arguments_, _tailPosition_).
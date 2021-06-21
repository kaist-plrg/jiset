          1. Let _baseReference_ be the result of evaluating |MemberExpression|.
          1. Let _baseValue_ be ? GetValue(_baseReference_).
          1. If _baseValue_ is *undefined* or *null*, then
            1. Return *undefined*.
          1. Return the result of performing ChainEvaluation of |OptionalChain| with arguments _baseValue_ and _baseReference_.
          1. Let _optionalChain_ be |OptionalChain|.
          1. Let _newReference_ be ? ChainEvaluation of _optionalChain_ with arguments _baseValue_ and _baseReference_.
          1. Let _newValue_ be ? GetValue(_newReference_).
          1. If the code matched by this |OptionalChain| is strict mode code, let _strict_ be *true*; else let _strict_ be *false*.
          1. Return ? EvaluatePropertyAccessWithExpressionKey(_newValue_, |Expression|, _strict_).
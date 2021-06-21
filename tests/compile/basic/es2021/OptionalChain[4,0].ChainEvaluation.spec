          1. Let _optionalChain_ be |OptionalChain|.
          1. Let _newReference_ be ? ChainEvaluation of _optionalChain_ with arguments _baseValue_ and _baseReference_.
          1. Let _newValue_ be ? GetValue(_newReference_).
          1. Let _thisChain_ be this |OptionalChain|.
          1. Let _tailCall_ be IsInTailPosition(_thisChain_).
          1. Return ? EvaluateCall(_newValue_, _newReference_, |Arguments|, _tailCall_).
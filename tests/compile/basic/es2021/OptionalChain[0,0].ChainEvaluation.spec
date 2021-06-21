          1. Let _thisChain_ be this |OptionalChain|.
          1. Let _tailCall_ be IsInTailPosition(_thisChain_).
          1. Return ? EvaluateCall(_baseValue_, _baseReference_, |Arguments|, _tailCall_).
          1. Let _ref_ be the result of evaluating |CallExpression|.
          1. Let _func_ be ? GetValue(_ref_).
          1. Let _thisCall_ be this |CallExpression|.
          1. Let _tailCall_ be IsInTailPosition(_thisCall_).
          1. Return ? EvaluateCall(_func_, _ref_, |Arguments|, _tailCall_).
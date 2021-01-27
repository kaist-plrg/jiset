          1. Let _tagRef_ be the result of evaluating |CallExpression|.
          1. Let _thisCall_ be this |CallExpression|.
          1. Let _tailCall_ be IsInTailPosition(_thisCall_).
          1. Return ? EvaluateCall(_tagRef_, |TemplateLiteral|, _tailCall_).
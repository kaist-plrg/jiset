          1. Let _tagRef_ be the result of evaluating |MemberExpression|.
          1. Let _tagFunc_ be ? GetValue(_tagRef_).
          1. Let _thisCall_ be this |MemberExpression|.
          1. Let _tailCall_ be IsInTailPosition(_thisCall_).
          1. Return ? EvaluateCall(_tagFunc_, _tagRef_, |TemplateLiteral|, _tailCall_).
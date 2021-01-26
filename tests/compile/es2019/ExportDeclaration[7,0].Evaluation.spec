          1. If IsAnonymousFunctionDefinition(|AssignmentExpression|) is *true*, then
            1. Let _value_ be the result of performing NamedEvaluation for |AssignmentExpression| with argument `"default"`.
          1. Else,
            1. Let _rhs_ be the result of evaluating |AssignmentExpression|.
            1. Let _value_ be ? GetValue(_rhs_).
          1. Let _env_ be the running execution context's LexicalEnvironment.
          1. Perform ? InitializeBoundName(`"*default*"`, _value_, _env_).
          1. Return NormalCompletion(~empty~).
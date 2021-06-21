          1. Let _baseReference_ be the result of evaluating |CallExpression|.
          1. Let _baseValue_ be ? GetValue(_baseReference_).
          1. If the code matched by this |CallExpression| is strict mode code, let _strict_ be *true*; else let _strict_ be *false*.
          1. Return ? EvaluatePropertyAccessWithExpressionKey(_baseValue_, |Expression|, _strict_).
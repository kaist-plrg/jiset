          1. Let _expr_ be the result of evaluating |UnaryExpression|.
          1. Let _oldValue_ be ! ToBoolean(? GetValue(_expr_)).
          1. If _oldValue_ is *true*, return *false*.
          1. Return *true*.
          1. Let _expr_ be the result of evaluating |UnaryExpression|.
          1. Let _oldValue_ be ? ToNumber(? GetValue(_expr_)).
          1. If _oldValue_ is *NaN*, return *NaN*.
          1. Return the result of negating _oldValue_; that is, compute a Number with the same magnitude but opposite sign.
          1. Let _expr_ be the result of evaluating |UnaryExpression|.
          1. Let _oldValue_ be ? ToInt32(? GetValue(_expr_)).
          1. Return the result of applying bitwise complement to _oldValue_. The result is a signed 32-bit integer.
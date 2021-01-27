          1. Let _expr_ be the result of evaluating |UnaryExpression|.
          1. Let _oldValue_ be ? ToNumeric(? GetValue(_expr_)).
          1. Let _newValue_ be ! Type(_oldValue_)::add(_oldValue_, Type(_oldValue_)::unit).
          1. Perform ? PutValue(_expr_, _newValue_).
          1. Return _newValue_.
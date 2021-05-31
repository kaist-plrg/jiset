          1. Let _lhs_ be the result of evaluating |LeftHandSideExpression|.
          1. Let _oldValue_ be ? ToNumeric(? GetValue(_lhs_)).
          1. Let _newValue_ be ! Type(_oldValue_)::subtract(_oldValue_, Type(_oldValue_)::unit).
          1. Perform ? PutValue(_lhs_, _newValue_).
          1. Return _oldValue_.
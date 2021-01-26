          1. Let _lhs_ be the result of evaluating |LeftHandSideExpression|.
          1. Let _oldValue_ be ? ToNumber(? GetValue(_lhs_)).
          1. Let _newValue_ be the result of adding the value 1 to _oldValue_, using the same rules as for the `+` operator (see <emu-xref href="#sec-applying-the-additive-operators-to-numbers"></emu-xref>).
          1. Perform ? PutValue(_lhs_, _newValue_).
          1. Return _oldValue_.
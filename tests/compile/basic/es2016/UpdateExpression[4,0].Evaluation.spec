          1. Let _expr_ be the result of evaluating |UnaryExpression|.
          1. Let _oldValue_ be ? ToNumber(? GetValue(_expr_)).
          1. Let _newValue_ be the result of subtracting the value `1` from _oldValue_, using the same rules as for the `-` operator (see <emu-xref href="#sec-applying-the-additive-operators-to-numbers"></emu-xref>).
          1. Perform ? PutValue(_expr_, _newValue_).
          1. Return _newValue_.
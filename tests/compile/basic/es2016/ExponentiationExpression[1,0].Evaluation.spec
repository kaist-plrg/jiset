        1. Let _left_ be the result of evaluating _UpdateExpression_.
        1. Let _leftValue_ be ? GetValue(_left_).
        1. Let _right_ be the result of evaluating _ExponentiationExpression_.
        1. Let _rightValue_ be ? GetValue(_right_).
        1. Let _base_ be ? ToNumber(_leftValue_).
        1. Let _exponent_ be ? ToNumber(_rightValue_).
        1. Return the result of <emu-xref href="#sec-applying-the-exp-operator" title>Applying the ** operator</emu-xref> with _base_ and _exponent_ as specified in <emu-xref href="#sec-applying-the-exp-operator"></emu-xref>.
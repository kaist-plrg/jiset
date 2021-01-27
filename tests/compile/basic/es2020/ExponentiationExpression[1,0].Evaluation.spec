        1. Let _left_ be the result of evaluating |UpdateExpression|.
        1. Let _leftValue_ be ? GetValue(_left_).
        1. Let _right_ be the result of evaluating |ExponentiationExpression|.
        1. Let _rightValue_ be ? GetValue(_right_).
        1. Let _base_ be ? ToNumeric(_leftValue_).
        1. Let _exponent_ be ? ToNumeric(_rightValue_).
        1. If Type(_base_) is different from Type(_exponent_), throw a *TypeError* exception.
        1. Return ? Type(_base_)::exponentiate(_base_, _exponent_).
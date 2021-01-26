        1. Let _left_ be the result of evaluating |MultiplicativeExpression|.
        1. Let _leftValue_ be ? GetValue(_left_).
        1. Let _right_ be the result of evaluating |ExponentiationExpression|.
        1. Let _rightValue_ be ? GetValue(_right_).
        1. Let _lnum_ be ? ToNumber(_leftValue_).
        1. Let _rnum_ be ? ToNumber(_rightValue_).
        1. Return the result of applying the |MultiplicativeOperator| (`*`, `/`, or `%`) to _lnum_ and _rnum_ as specified in <emu-xref href="#sec-applying-the-mul-operator"></emu-xref>, <emu-xref href="#sec-applying-the-div-operator"></emu-xref>, or <emu-xref href="#sec-applying-the-mod-operator"></emu-xref>.
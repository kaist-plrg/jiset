          1. Let _expr_ be the result of evaluating |UnaryExpression|.
          1. Let _oldValue_ be ? ToNumeric(? GetValue(_expr_)).
          1. Let _T_ be Type(_oldValue_).
          1. Return ! _T_::bitwiseNOT(_oldValue_).
          1. Let _R_ be the *this* value.
          1. Perform ? RequireInternalSlot(_R_, [[RegExpMatcher]]).
          1. Let _S_ be ? ToString(_string_).
          1. Return ? RegExpBuiltinExec(_R_, _S_).
          1. Let _R_ be the *this* value.
          1. If Type(_R_) is not Object, throw a *TypeError* exception.
          1. If _R_ does not have a [[RegExpMatcher]] internal slot, throw a *TypeError* exception.
          1. Let _S_ be ? ToString(_string_).
          1. Return ? RegExpBuiltinExec(_R_, _S_).
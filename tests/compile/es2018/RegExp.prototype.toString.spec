          1. Let _R_ be the *this* value.
          1. If Type(_R_) is not Object, throw a *TypeError* exception.
          1. Let _pattern_ be ? ToString(? Get(_R_, `"source"`)).
          1. Let _flags_ be ? ToString(? Get(_R_, `"flags"`)).
          1. Let _result_ be the string-concatenation of `"/"`, _pattern_, `"/"`, and _flags_.
          1. Return _result_.
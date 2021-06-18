          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _R_ be _S_.
          1. For each element _next_ of _args_, do
            1. Let _nextString_ be ? ToString(_next_).
            1. Set _R_ to the string-concatenation of _R_ and _nextString_.
          1. Return _R_.
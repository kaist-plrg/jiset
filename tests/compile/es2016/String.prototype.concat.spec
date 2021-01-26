          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _args_ be a List whose elements are the arguments passed to this function.
          1. Let _R_ be _S_.
          1. Repeat, while _args_ is not empty
            1. Remove the first element from _args_ and let _next_ be the value of that element.
            1. Let _nextString_ be ? ToString(_next_).
            1. Let _R_ be the String value consisting of the code units of the previous value of _R_ followed by the code units of _nextString_.
          1. Return _R_.
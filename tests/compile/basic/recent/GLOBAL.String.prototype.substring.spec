          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _len_ be the length of _S_.
          1. Let _intStart_ be ? ToIntegerOrInfinity(_start_).
          1. If _end_ is *undefined*, let _intEnd_ be _len_; else let _intEnd_ be ? ToIntegerOrInfinity(_end_).
          1. Let _finalStart_ be the result of clamping _intStart_ between 0 and _len_.
          1. Let _finalEnd_ be the result of clamping _intEnd_ between 0 and _len_.
          1. Let _from_ be min(_finalStart_, _finalEnd_).
          1. Let _to_ be max(_finalStart_, _finalEnd_).
          1. Return the substring of _S_ from _from_ to _to_.
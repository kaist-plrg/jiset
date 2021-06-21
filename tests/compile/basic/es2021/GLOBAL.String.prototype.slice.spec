          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _len_ be the length of _S_.
          1. Let _intStart_ be ? ToIntegerOrInfinity(_start_).
          1. If _intStart_ is -∞, let _from_ be 0.
          1. Else if _intStart_ < 0, let _from_ be max(_len_ + _intStart_, 0).
          1. Else, let _from_ be min(_intStart_, _len_).
          1. If _end_ is *undefined*, let _intEnd_ be _len_; else let _intEnd_ be ? ToIntegerOrInfinity(_end_).
          1. If _intEnd_ is -∞, let _to_ be 0.
          1. Else if _intEnd_ < 0, let _to_ be max(_len_ + _intEnd_, 0).
          1. Else, let _to_ be min(_intEnd_, _len_).
          1. If _from_ ≥ _to_, return the empty String.
          1. Return the substring of _S_ from _from_ to _to_.
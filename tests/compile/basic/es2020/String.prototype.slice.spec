          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _len_ be the length of _S_.
          1. Let _intStart_ be ? ToInteger(_start_).
          1. If _end_ is *undefined*, let _intEnd_ be _len_; else let _intEnd_ be ? ToInteger(_end_).
          1. If _intStart_ < 0, let _from_ be max(_len_ + _intStart_, 0); otherwise let _from_ be min(_intStart_, _len_).
          1. If _intEnd_ < 0, let _to_ be max(_len_ + _intEnd_, 0); otherwise let _to_ be min(_intEnd_, _len_).
          1. Let _span_ be max(_to_ - _from_, 0).
          1. Return the String value containing _span_ consecutive code units from _S_ beginning with the code unit at index _from_.
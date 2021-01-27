          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _len_ be the length of _S_.
          1. Let _intStart_ be ? ToInteger(_start_).
          1. If _end_ is *undefined*, let _intEnd_ be _len_; else let _intEnd_ be ? ToInteger(_end_).
          1. Let _finalStart_ be min(max(_intStart_, 0), _len_).
          1. Let _finalEnd_ be min(max(_intEnd_, 0), _len_).
          1. Let _from_ be min(_finalStart_, _finalEnd_).
          1. Let _to_ be max(_finalStart_, _finalEnd_).
          1. Return the String value whose length is _to_ - _from_, containing code units from _S_, namely the code units with indices _from_ through _to_ - 1, in ascending order.
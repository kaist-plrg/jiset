          1. Let _len_ be the actual number of arguments passed to this function.
          1. Let _items_ be the List of arguments passed to this function.
          1. Let _C_ be the *this* value.
          1. If IsConstructor(_C_) is *true*, then
            1. Let _A_ be ? Construct(_C_, « _len_ »).
          1. Else,
            1. Let _A_ be ? ArrayCreate(_len_).
          1. Let _k_ be 0.
          1. Repeat, while _k_ < _len_
            1. Let _kValue_ be _items_[_k_].
            1. Let _Pk_ be ! ToString(_k_).
            1. Perform ? CreateDataPropertyOrThrow(_A_, _Pk_, _kValue_).
            1. Set _k_ to _k_ + 1.
          1. Perform ? Set(_A_, *"length"*, _len_, *true*).
          1. Return _A_.
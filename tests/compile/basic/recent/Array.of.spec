          1. Let _len_ be the number of elements in _items_.
          1. Let _lenNumber_ be 𝔽(_len_).
          1. Let _C_ be the *this* value.
          1. If IsConstructor(_C_) is *true*, then
            1. Let _A_ be ? Construct(_C_, « _lenNumber_ »).
          1. Else,
            1. Let _A_ be ? ArrayCreate(_len_).
          1. Let _k_ be 0.
          1. Repeat, while _k_ < _len_,
            1. Let _kValue_ be _items_[_k_].
            1. Let _Pk_ be ! ToString(𝔽(_k_)).
            1. Perform ? CreateDataPropertyOrThrow(_A_, _Pk_, _kValue_).
            1. Set _k_ to _k_ + 1.
          1. Perform ? Set(_A_, *"length"*, _lenNumber_, *true*).
          1. Return _A_.
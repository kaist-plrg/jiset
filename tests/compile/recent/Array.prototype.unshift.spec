          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? LengthOfArrayLike(_O_).
          1. Let _argCount_ be the number of elements in _items_.
          1. If _argCount_ > 0, then
            1. If _len_ + _argCount_ > 2<sup>53</sup> - 1, throw a *TypeError* exception.
            1. Let _k_ be _len_.
            1. Repeat, while _k_ > 0,
              1. Let _from_ be ! ToString(𝔽(_k_ - 1)).
              1. Let _to_ be ! ToString(𝔽(_k_ + _argCount_ - 1)).
              1. Let _fromPresent_ be ? HasProperty(_O_, _from_).
              1. If _fromPresent_ is *true*, then
                1. Let _fromValue_ be ? Get(_O_, _from_).
                1. Perform ? Set(_O_, _to_, _fromValue_, *true*).
              1. Else,
                1. Assert: _fromPresent_ is *false*.
                1. Perform ? DeletePropertyOrThrow(_O_, _to_).
              1. Set _k_ to _k_ - 1.
            1. Let _j_ be *+0*<sub>𝔽</sub>.
            1. For each element _E_ of _items_, do
              1. Perform ? Set(_O_, ! ToString(_j_), _E_, *true*).
              1. Set _j_ to _j_ + *1*<sub>𝔽</sub>.
          1. Perform ? Set(_O_, *"length"*, 𝔽(_len_ + _argCount_), *true*).
          1. Return 𝔽(_len_ + _argCount_).
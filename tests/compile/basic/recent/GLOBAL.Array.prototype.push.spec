          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? LengthOfArrayLike(_O_).
          1. Let _argCount_ be the number of elements in _items_.
          1. If _len_ + _argCount_ > 2<sup>53</sup> - 1, throw a *TypeError* exception.
          1. For each element _E_ of _items_, do
            1. Perform ? Set(_O_, ! ToString(𝔽(_len_)), _E_, *true*).
            1. Set _len_ to _len_ + 1.
          1. Perform ? Set(_O_, *"length"*, 𝔽(_len_), *true*).
          1. Return 𝔽(_len_).
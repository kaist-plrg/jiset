          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? LengthOfArrayLike(_O_).
          1. Let _relativeStart_ be ? ToInteger(_start_).
          1. If _relativeStart_ < 0, let _actualStart_ be max((_len_ + _relativeStart_), 0); else let _actualStart_ be min(_relativeStart_, _len_).
          1. If the number of actual arguments is 0, then
            1. Let _insertCount_ be 0.
            1. Let _actualDeleteCount_ be 0.
          1. Else if the number of actual arguments is 1, then
            1. Let _insertCount_ be 0.
            1. Let _actualDeleteCount_ be _len_ - _actualStart_.
          1. Else,
            1. Let _insertCount_ be the number of actual arguments minus 2.
            1. Let _dc_ be ? ToInteger(_deleteCount_).
            1. Let _actualDeleteCount_ be min(max(_dc_, 0), _len_ - _actualStart_).
          1. If _len_ + _insertCount_ - _actualDeleteCount_ > 2<sup>53</sup> - 1, throw a *TypeError* exception.
          1. Let _A_ be ? ArraySpeciesCreate(_O_, _actualDeleteCount_).
          1. Let _k_ be 0.
          1. Repeat, while _k_ < _actualDeleteCount_
            1. Let _from_ be ! ToString(_actualStart_ + _k_).
            1. Let _fromPresent_ be ? HasProperty(_O_, _from_).
            1. If _fromPresent_ is *true*, then
              1. Let _fromValue_ be ? Get(_O_, _from_).
              1. Perform ? CreateDataPropertyOrThrow(_A_, ! ToString(_k_), _fromValue_).
            1. Set _k_ to _k_ + 1.
          1. Perform ? Set(_A_, *"length"*, _actualDeleteCount_, *true*).
          1. Let _items_ be a List whose elements are, in left to right order, the portion of the actual argument list starting with the third argument. The list is empty if fewer than three arguments were passed.
          1. Let _itemCount_ be the number of elements in _items_.
          1. If _itemCount_ < _actualDeleteCount_, then
            1. Set _k_ to _actualStart_.
            1. Repeat, while _k_ < (_len_ - _actualDeleteCount_)
              1. Let _from_ be ! ToString(_k_ + _actualDeleteCount_).
              1. Let _to_ be ! ToString(_k_ + _itemCount_).
              1. Let _fromPresent_ be ? HasProperty(_O_, _from_).
              1. If _fromPresent_ is *true*, then
                1. Let _fromValue_ be ? Get(_O_, _from_).
                1. Perform ? Set(_O_, _to_, _fromValue_, *true*).
              1. Else,
                1. Assert: _fromPresent_ is *false*.
                1. Perform ? DeletePropertyOrThrow(_O_, _to_).
              1. Set _k_ to _k_ + 1.
            1. Set _k_ to _len_.
            1. Repeat, while _k_ > (_len_ - _actualDeleteCount_ + _itemCount_)
              1. Perform ? DeletePropertyOrThrow(_O_, ! ToString(_k_ - 1)).
              1. Set _k_ to _k_ - 1.
          1. Else if _itemCount_ > _actualDeleteCount_, then
            1. Set _k_ to (_len_ - _actualDeleteCount_).
            1. Repeat, while _k_ > _actualStart_
              1. Let _from_ be ! ToString(_k_ + _actualDeleteCount_ - 1).
              1. Let _to_ be ! ToString(_k_ + _itemCount_ - 1).
              1. Let _fromPresent_ be ? HasProperty(_O_, _from_).
              1. If _fromPresent_ is *true*, then
                1. Let _fromValue_ be ? Get(_O_, _from_).
                1. Perform ? Set(_O_, _to_, _fromValue_, *true*).
              1. Else,
                1. Assert: _fromPresent_ is *false*.
                1. Perform ? DeletePropertyOrThrow(_O_, _to_).
              1. Set _k_ to _k_ - 1.
          1. Set _k_ to _actualStart_.
          1. Repeat, while _items_ is not empty
            1. Remove the first element from _items_ and let _E_ be the value of that element.
            1. Perform ? Set(_O_, ! ToString(_k_), _E_, *true*).
            1. Set _k_ to _k_ + 1.
          1. Perform ? Set(_O_, *"length"*, _len_ - _actualDeleteCount_ + _itemCount_, *true*).
          1. Return _A_.
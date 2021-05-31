          1. If NewTarget is *undefined*, let _newTarget_ be the active function object; else let _newTarget_ be NewTarget.
          1. Let _proto_ be ? GetPrototypeFromConstructor(_newTarget_, *"%Array.prototype%"*).
          1. Let _numberOfArgs_ be the number of elements in _values_.
          1. If _numberOfArgs_ = 0, then
            1. Return ! ArrayCreate(0, _proto_).
          1. Else if _numberOfArgs_ = 1, then
            1. Let _len_ be _values_[0].
            1. Let _array_ be ! ArrayCreate(0, _proto_).
            1. If Type(_len_) is not Number, then
              1. Perform ! CreateDataPropertyOrThrow(_array_, *"0"*, _len_).
              1. Let _intLen_ be *1*<sub>𝔽</sub>.
            1. Else,
              1. Let _intLen_ be ! ToUint32(_len_).
              1. If _intLen_ is not the same value as _len_, throw a *RangeError* exception.
            1. Perform ! Set(_array_, *"length"*, _intLen_, *true*).
            1. Return _array_.
          1. Else,
            1. Assert: _numberOfArgs_ ≥ 2.
            1. Let _array_ be ? ArrayCreate(_numberOfArgs_, _proto_).
            1. Let _k_ be 0.
            1. Repeat, while _k_ < _numberOfArgs_,
              1. Let _Pk_ be ! ToString(𝔽(_k_)).
              1. Let _itemK_ be _values_[_k_].
              1. Perform ! CreateDataPropertyOrThrow(_array_, _Pk_, _itemK_).
              1. Set _k_ to _k_ + 1.
            1. Assert: The mathematical value of _array_'s *"length"* property is _numberOfArgs_.
            1. Return _array_.
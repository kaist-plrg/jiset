          1. Let _numberOfArgs_ be the number of arguments passed to this function call.
          1. Assert: _numberOfArgs_ ≥ 2.
          1. If NewTarget is *undefined*, let _newTarget_ be the active function object; else let _newTarget_ be NewTarget.
          1. Let _proto_ be ? GetPrototypeFromConstructor(_newTarget_, *"%Array.prototype%"*).
          1. Let _array_ be ? ArrayCreate(_numberOfArgs_, _proto_).
          1. Let _k_ be 0.
          1. Let _items_ be a zero-origined List containing the argument items in order.
          1. Repeat, while _k_ < _numberOfArgs_
            1. Let _Pk_ be ! ToString(_k_).
            1. Let _itemK_ be _items_[_k_].
            1. Perform ! CreateDataPropertyOrThrow(_array_, _Pk_, _itemK_).
            1. Set _k_ to _k_ + 1.
          1. Assert: The value of _array_'s *"length"* property is _numberOfArgs_.
          1. Return _array_.
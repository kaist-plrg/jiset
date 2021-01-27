          1. Let _C_ be the *this* value.
          1. If _mapfn_ is *undefined*, let _mapping_ be *false*.
          1. Else,
            1. If IsCallable(_mapfn_) is *false*, throw a *TypeError* exception.
            1. Let _mapping_ be *true*.
          1. Let _usingIterator_ be ? GetMethod(_items_, @@iterator).
          1. If _usingIterator_ is not *undefined*, then
            1. If IsConstructor(_C_) is *true*, then
              1. Let _A_ be ? Construct(_C_).
            1. Else,
              1. Let _A_ be ! ArrayCreate(0).
            1. Let _iteratorRecord_ be ? GetIterator(_items_, ~sync~, _usingIterator_).
            1. Let _k_ be 0.
            1. Repeat,
              1. If _k_ ≥ 2<sup>53</sup> - 1, then
                1. Let _error_ be ThrowCompletion(a newly created *TypeError* object).
                1. Return ? IteratorClose(_iteratorRecord_, _error_).
              1. Let _Pk_ be ! ToString(_k_).
              1. Let _next_ be ? IteratorStep(_iteratorRecord_).
              1. If _next_ is *false*, then
                1. Perform ? Set(_A_, *"length"*, _k_, *true*).
                1. Return _A_.
              1. Let _nextValue_ be ? IteratorValue(_next_).
              1. If _mapping_ is *true*, then
                1. Let _mappedValue_ be Call(_mapfn_, _thisArg_, « _nextValue_, _k_ »).
                1. If _mappedValue_ is an abrupt completion, return ? IteratorClose(_iteratorRecord_, _mappedValue_).
                1. Set _mappedValue_ to _mappedValue_.[[Value]].
              1. Else, let _mappedValue_ be _nextValue_.
              1. Let _defineStatus_ be CreateDataPropertyOrThrow(_A_, _Pk_, _mappedValue_).
              1. If _defineStatus_ is an abrupt completion, return ? IteratorClose(_iteratorRecord_, _defineStatus_).
              1. Set _k_ to _k_ + 1.
          1. NOTE: _items_ is not an Iterable so assume it is an array-like object.
          1. Let _arrayLike_ be ! ToObject(_items_).
          1. Let _len_ be ? LengthOfArrayLike(_arrayLike_).
          1. If IsConstructor(_C_) is *true*, then
            1. Let _A_ be ? Construct(_C_, « _len_ »).
          1. Else,
            1. Let _A_ be ? ArrayCreate(_len_).
          1. Let _k_ be 0.
          1. Repeat, while _k_ < _len_
            1. Let _Pk_ be ! ToString(_k_).
            1. Let _kValue_ be ? Get(_arrayLike_, _Pk_).
            1. If _mapping_ is *true*, then
              1. Let _mappedValue_ be ? Call(_mapfn_, _thisArg_, « _kValue_, _k_ »).
            1. Else, let _mappedValue_ be _kValue_.
            1. Perform ? CreateDataPropertyOrThrow(_A_, _Pk_, _mappedValue_).
            1. Set _k_ to _k_ + 1.
          1. Perform ? Set(_A_, *"length"*, _len_, *true*).
          1. Return _A_.
          1. Assert: IsPropertyKey(_P_) is *true*.
          1. Assert: _O_ is an Object that has a [[ViewedArrayBuffer]] internal slot.
          1. If Type(_P_) is String, then
            1. Let _numericIndex_ be ! CanonicalNumericIndexString(_P_).
            1. If _numericIndex_ is not *undefined*, then
              1. If IsInteger(_numericIndex_) is *false*, return *false*.
              1. If _numericIndex_ = *-0*, return *false*.
              1. If _numericIndex_ < 0, return *false*.
              1. Let _length_ be _O_.[[ArrayLength]].
              1. If _numericIndex_ ≥ _length_, return *false*.
              1. If IsAccessorDescriptor(_Desc_) is *true*, return *false*.
              1. If _Desc_ has a [[Configurable]] field and if _Desc_.[[Configurable]] is *true*, return *false*.
              1. If _Desc_ has an [[Enumerable]] field and if _Desc_.[[Enumerable]] is *false*, return *false*.
              1. If _Desc_ has a [[Writable]] field and if _Desc_.[[Writable]] is *false*, return *false*.
              1. If _Desc_ has a [[Value]] field, then
                1. Let _value_ be _Desc_.[[Value]].
                1. Return ? IntegerIndexedElementSet(_O_, _numericIndex_, _value_).
              1. Return *true*.
          1. Return ! OrdinaryDefineOwnProperty(_O_, _P_, _Desc_).
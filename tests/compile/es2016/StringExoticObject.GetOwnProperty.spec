          1. Assert: IsPropertyKey(_P_) is *true*.
          1. Let _desc_ be OrdinaryGetOwnProperty(_S_, _P_).
          1. If _desc_ is not *undefined*, return _desc_.
          1. If Type(_P_) is not String, return *undefined*.
          1. Let _index_ be ! CanonicalNumericIndexString(_P_).
          1. If _index_ is *undefined*, return *undefined*.
          1. If IsInteger(_index_) is *false*, return *undefined*.
          1. If _index_ = *-0*, return *undefined*.
          1. Let _str_ be the String value of the [[StringData]] internal slot of _S_.
          1. Let _len_ be the number of elements in _str_.
          1. If _index_ < 0 or _len_ ≤ _index_, return *undefined*.
          1. Let _resultStr_ be a String value of length 1, containing one code unit from _str_, specifically the code unit at index _index_.
          1. Return a PropertyDescriptor{[[Value]]: _resultStr_, [[Writable]]: *false*, [[Enumerable]]: *true*, [[Configurable]]: *false*}.
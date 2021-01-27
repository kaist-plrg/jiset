        1. Let _stack_ be a new empty List.
        1. Let _indent_ be the empty String.
        1. Let _PropertyList_ and _ReplacerFunction_ be *undefined*.
        1. If Type(_replacer_) is Object, then
          1. If IsCallable(_replacer_) is *true*, then
            1. Let _ReplacerFunction_ be _replacer_.
          1. Else,
            1. Let _isArray_ be ? IsArray(_replacer_).
            1. If _isArray_ is *true*, then
              1. Let _PropertyList_ be a new empty List.
              1. Let _len_ be ? ToLength(? Get(_replacer_, `"length"`)).
              1. Let _k_ be 0.
              1. Repeat, while _k_<_len_,
                1. Let _v_ be ? Get(_replacer_, ! ToString(_k_)).
                1. Let _item_ be *undefined*.
                1. If Type(_v_) is String, let _item_ be _v_.
                1. Else if Type(_v_) is Number, let _item_ be ! ToString(_v_).
                1. Else if Type(_v_) is Object, then
                  1. If _v_ has a [[StringData]] or [[NumberData]] internal slot, let _item_ be ? ToString(_v_).
                1. If _item_ is not *undefined* and _item_ is not currently an element of _PropertyList_, then
                  1. Append _item_ to the end of _PropertyList_.
                1. Let _k_ be _k_+1.
        1. If Type(_space_) is Object, then
          1. If _space_ has a [[NumberData]] internal slot, then
            1. Let _space_ be ? ToNumber(_space_).
          1. Else if _space_ has a [[StringData]] internal slot, then
            1. Let _space_ be ? ToString(_space_).
        1. If Type(_space_) is Number, then
          1. Let _space_ be min(10, ToInteger(_space_)).
          1. Set _gap_ to the String value containing _space_ occurrences of the code unit 0x0020 (SPACE). This will be the empty String if _space_ is less than 1.
        1. Else if Type(_space_) is String, then
          1. If the length of _space_ is 10 or less, set _gap_ to _space_; otherwise set _gap_ to the String value consisting of the first 10 elements of _space_.
        1. Else,
          1. Set _gap_ to the empty String.
        1. Let _wrapper_ be ObjectCreate(%ObjectPrototype%).
        1. Let _status_ be CreateDataProperty(_wrapper_, the empty String, _value_).
        1. Assert: _status_ is *true*.
        1. Return ? SerializeJSONProperty(the empty String, _wrapper_).
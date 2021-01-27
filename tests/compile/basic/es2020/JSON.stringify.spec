        1. Let _stack_ be a new empty List.
        1. Let _indent_ be the empty String.
        1. Let _PropertyList_ and _ReplacerFunction_ be *undefined*.
        1. If Type(_replacer_) is Object, then
          1. If IsCallable(_replacer_) is *true*, then
            1. Set _ReplacerFunction_ to _replacer_.
          1. Else,
            1. Let _isArray_ be ? IsArray(_replacer_).
            1. If _isArray_ is *true*, then
              1. Set _PropertyList_ to a new empty List.
              1. Let _len_ be ? LengthOfArrayLike(_replacer_).
              1. Let _k_ be 0.
              1. Repeat, while _k_ < _len_,
                1. Let _v_ be ? Get(_replacer_, ! ToString(_k_)).
                1. Let _item_ be *undefined*.
                1. If Type(_v_) is String, set _item_ to _v_.
                1. Else if Type(_v_) is Number, set _item_ to ! ToString(_v_).
                1. Else if Type(_v_) is Object, then
                  1. If _v_ has a [[StringData]] or [[NumberData]] internal slot, set _item_ to ? ToString(_v_).
                1. If _item_ is not *undefined* and _item_ is not currently an element of _PropertyList_, then
                  1. Append _item_ to the end of _PropertyList_.
                1. Set _k_ to _k_ + 1.
        1. If Type(_space_) is Object, then
          1. If _space_ has a [[NumberData]] internal slot, then
            1. Set _space_ to ? ToNumber(_space_).
          1. Else if _space_ has a [[StringData]] internal slot, then
            1. Set _space_ to ? ToString(_space_).
        1. If Type(_space_) is Number, then
          1. Set _space_ to min(10, ! ToInteger(_space_)).
          1. If _space_ < 1, let _gap_ be the empty String; otherwise let _gap_ be the String value containing _space_ occurrences of the code unit 0x0020 (SPACE).
        1. Else if Type(_space_) is String, then
          1. If the length of _space_ is 10 or less, let _gap_ be _space_; otherwise let _gap_ be the String value consisting of the first 10 code units of _space_.
        1. Else,
          1. Let _gap_ be the empty String.
        1. Let _wrapper_ be OrdinaryObjectCreate(%Object.prototype%).
        1. Perform ! CreateDataPropertyOrThrow(_wrapper_, the empty String, _value_).
        1. Let _state_ be the Record { [[ReplacerFunction]]: _ReplacerFunction_, [[Stack]]: _stack_, [[Indent]]: _indent_, [[Gap]]: _gap_, [[PropertyList]]: _PropertyList_ }.
        1. Return ? SerializeJSONProperty(_state_, the empty String, _wrapper_).
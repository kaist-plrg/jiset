          1. Assert: Type(_array_) is Object.
          1. Assert: _kind_ is ~key+value~, ~key~, or ~value~.
          1. Let _closure_ be a new Abstract Closure with no parameters that captures _kind_ and _array_ and performs the following steps when called:
            1. Let _index_ be 0.
            1. Repeat,
              1. If _array_ has a [[TypedArrayName]] internal slot, then
                1. If IsDetachedBuffer(_array_.[[ViewedArrayBuffer]]) is *true*, throw a *TypeError* exception.
                1. Let _len_ be _array_.[[ArrayLength]].
              1. Else,
                1. Let _len_ be ? LengthOfArrayLike(_array_).
              1. If _index_ ≥ _len_, return *undefined*.
              1. If _kind_ is ~key~, perform ? Yield(𝔽(_index_)).
              1. Else,
                1. Let _elementKey_ be ! ToString(𝔽(_index_)).
                1. Let _elementValue_ be ? Get(_array_, _elementKey_).
                1. If _kind_ is ~value~, perform ? Yield(_elementValue_).
                1. Else,
                  1. Assert: _kind_ is ~key+value~.
                  1. Perform ? Yield(! CreateArrayFromList(« 𝔽(_index_), _elementValue_ »)).
              1. Set _index_ to _index_ + 1.
          1. Return ! CreateIteratorFromClosure(_closure_, *"%ArrayIteratorPrototype%"*, %ArrayIteratorPrototype%).
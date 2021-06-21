          1. Assert: Type(_S_) is String.
          1. Assert: Type(_global_) is Boolean.
          1. Assert: Type(_fullUnicode_) is Boolean.
          1. Let _closure_ be a new Abstract Closure with no parameters that captures _R_, _S_, _global_, and _fullUnicode_ and performs the following steps when called:
            1. Repeat,
              1. Let _match_ be ? RegExpExec(_R_, _S_).
              1. If _match_ is *null*, return *undefined*.
              1. If _global_ is *false*, then
                1. Perform ? Yield(_match_).
                1. Return *undefined*.
              1. Let _matchStr_ be ? ToString(? Get(_match_, *"0"*)).
              1. If _matchStr_ is the empty String, then
                1. Let _thisIndex_ be ℝ(? ToLength(? Get(_R_, *"lastIndex"*))).
                1. Let _nextIndex_ be ! AdvanceStringIndex(_S_, _thisIndex_, _fullUnicode_).
                1. Perform ? Set(_R_, *"lastIndex"*, 𝔽(_nextIndex_), *true*).
              1. Perform ? Yield(_match_).
          1. Return ! CreateIteratorFromClosure(_closure_, *"%RegExpStringIteratorPrototype%"*, %RegExpStringIteratorPrototype%).
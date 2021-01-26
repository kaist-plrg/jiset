          1. Let _rx_ be the *this* value.
          1. If Type(_rx_) is not Object, throw a *TypeError* exception.
          1. Let _S_ be ? ToString(_string_).
          1. Let _lengthS_ be the number of code unit elements in _S_.
          1. Let _functionalReplace_ be IsCallable(_replaceValue_).
          1. If _functionalReplace_ is *false*, then
            1. Let _replaceValue_ be ? ToString(_replaceValue_).
          1. Let _global_ be ToBoolean(? Get(_rx_, `"global"`)).
          1. If _global_ is *true*, then
            1. Let _fullUnicode_ be ToBoolean(? Get(_rx_, `"unicode"`)).
            1. Perform ? Set(_rx_, `"lastIndex"`, 0, *true*).
          1. Let _results_ be a new empty List.
          1. Let _done_ be *false*.
          1. Repeat, while _done_ is *false*
            1. Let _result_ be ? RegExpExec(_rx_, _S_).
            1. If _result_ is *null*, set _done_ to *true*.
            1. Else _result_ is not *null*,
              1. Append _result_ to the end of _results_.
              1. If _global_ is *false*, set _done_ to *true*.
              1. Else,
                1. Let _matchStr_ be ? ToString(? Get(_result_, `"0"`)).
                1. If _matchStr_ is the empty String, then
                  1. Let _thisIndex_ be ? ToLength(? Get(_rx_, `"lastIndex"`)).
                  1. Let _nextIndex_ be AdvanceStringIndex(_S_, _thisIndex_, _fullUnicode_).
                  1. Perform ? Set(_rx_, `"lastIndex"`, _nextIndex_, *true*).
          1. Let _accumulatedResult_ be the empty String value.
          1. Let _nextSourcePosition_ be 0.
          1. Repeat, for each _result_ in _results_,
            1. Let _nCaptures_ be ? ToLength(? Get(_result_, `"length"`)).
            1. Let _nCaptures_ be max(_nCaptures_ - 1, 0).
            1. Let _matched_ be ? ToString(? Get(_result_, `"0"`)).
            1. Let _matchLength_ be the number of code units in _matched_.
            1. Let _position_ be ? ToInteger(? Get(_result_, `"index"`)).
            1. Let _position_ be max(min(_position_, _lengthS_), 0).
            1. Let _n_ be 1.
            1. Let _captures_ be a new empty List.
            1. Repeat while _n_ ≤ _nCaptures_
              1. Let _capN_ be ? Get(_result_, ! ToString(_n_)).
              1. If _capN_ is not *undefined*, then
                1. Let _capN_ be ? ToString(_capN_).
              1. Append _capN_ as the last element of _captures_.
              1. Let _n_ be _n_+1.
            1. If _functionalReplace_ is *true*, then
              1. Let _replacerArgs_ be « _matched_ ».
              1. Append in list order the elements of _captures_ to the end of the List _replacerArgs_.
              1. Append _position_ and _S_ as the last two elements of _replacerArgs_.
              1. Let _replValue_ be ? Call(_replaceValue_, *undefined*, _replacerArgs_).
              1. Let _replacement_ be ? ToString(_replValue_).
            1. Else,
              1. Let _replacement_ be GetSubstitution(_matched_, _S_, _position_, _captures_, _replaceValue_).
            1. If _position_ ≥ _nextSourcePosition_, then
              1. NOTE _position_ should not normally move backwards. If it does, it is an indication of an ill-behaving RegExp subclass or use of an access triggered side-effect to change the global flag or other characteristics of _rx_. In such cases, the corresponding substitution is ignored.
              1. Let _accumulatedResult_ be the String formed by concatenating the code units of the current value of _accumulatedResult_ with the substring of _S_ consisting of the code units from _nextSourcePosition_ (inclusive) up to _position_ (exclusive) and with the code units of _replacement_.
              1. Let _nextSourcePosition_ be _position_ + _matchLength_.
          1. If _nextSourcePosition_ ≥ _lengthS_, return _accumulatedResult_.
          1. Return the String formed by concatenating the code units of _accumulatedResult_ with the substring of _S_ consisting of the code units from _nextSourcePosition_ (inclusive) up through the final code unit of _S_ (inclusive).
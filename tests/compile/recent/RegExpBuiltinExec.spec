            1. Assert: _R_ is an initialized RegExp instance.
            1. Assert: Type(_S_) is String.
            1. Let _length_ be the number of code units in _S_.
            1. Let _lastIndex_ be ℝ(? ToLength(? Get(_R_, *"lastIndex"*))).
            1. Let _flags_ be _R_.[[OriginalFlags]].
            1. If _flags_ contains *"g"*, let _global_ be *true*; else let _global_ be *false*.
            1. If _flags_ contains *"y"*, let _sticky_ be *true*; else let _sticky_ be *false*.
            1. If _global_ is *false* and _sticky_ is *false*, set _lastIndex_ to 0.
            1. Let _matcher_ be _R_.[[RegExpMatcher]].
            1. If _flags_ contains *"u"*, let _fullUnicode_ be *true*; else let _fullUnicode_ be *false*.
            1. Let _matchSucceeded_ be *false*.
            1. Repeat, while _matchSucceeded_ is *false*,
              1. If _lastIndex_ > _length_, then
                1. If _global_ is *true* or _sticky_ is *true*, then
                  1. Perform ? Set(_R_, *"lastIndex"*, *+0*<sub>𝔽</sub>, *true*).
                1. Return *null*.
              1. Let _r_ be _matcher_(_S_, _lastIndex_).
              1. If _r_ is ~failure~, then
                1. If _sticky_ is *true*, then
                  1. Perform ? Set(_R_, *"lastIndex"*, *+0*<sub>𝔽</sub>, *true*).
                  1. Return *null*.
                1. Set _lastIndex_ to AdvanceStringIndex(_S_, _lastIndex_, _fullUnicode_).
              1. Else,
                1. Assert: _r_ is a State.
                1. Set _matchSucceeded_ to *true*.
            1. Let _e_ be _r_'s _endIndex_ value.
            1. If _fullUnicode_ is *true*, then
              1. _e_ is an index into the _Input_ character list, derived from _S_, matched by _matcher_. Let _eUTF_ be the smallest index into _S_ that corresponds to the character at element _e_ of _Input_. If _e_ is greater than or equal to the number of elements in _Input_, then _eUTF_ is the number of code units in _S_.
              1. Set _e_ to _eUTF_.
            1. If _global_ is *true* or _sticky_ is *true*, then
              1. Perform ? Set(_R_, *"lastIndex"*, 𝔽(_e_), *true*).
            1. Let _n_ be the number of elements in _r_'s _captures_ List. (This is the same value as <emu-xref href="#sec-notation"></emu-xref>'s _NcapturingParens_.)
            1. Assert: _n_ < 2<sup>32</sup> - 1.
            1. Let _A_ be ! ArrayCreate(_n_ + 1).
            1. Assert: The mathematical value of _A_'s *"length"* property is _n_ + 1.
            1. Perform ! CreateDataPropertyOrThrow(_A_, *"index"*, 𝔽(_lastIndex_)).
            1. Perform ! CreateDataPropertyOrThrow(_A_, *"input"*, _S_).
            1. Let _matchedSubstr_ be the substring of _S_ from _lastIndex_ to _e_.
            1. Perform ! CreateDataPropertyOrThrow(_A_, *"0"*, _matchedSubstr_).
            1. If _R_ contains any |GroupName|, then
              1. Let _groups_ be ! OrdinaryObjectCreate(*null*).
            1. Else,
              1. Let _groups_ be *undefined*.
            1. Perform ! CreateDataPropertyOrThrow(_A_, *"groups"*, _groups_).
            1. For each integer _i_ such that _i_ ≥ 1 and _i_ ≤ _n_, do
              1. Let _captureI_ be _i_<sup>th</sup> element of _r_'s _captures_ List.
              1. If _captureI_ is *undefined*, let _capturedValue_ be *undefined*.
              1. Else if _fullUnicode_ is *true*, then
                1. Assert: _captureI_ is a List of code points.
                1. Let _capturedValue_ be ! CodePointsToString(_captureI_).
              1. Else,
                1. Assert: _fullUnicode_ is *false*.
                1. Assert: _captureI_ is a List of code units.
                1. Let _capturedValue_ be the String value consisting of the code units of _captureI_.
              1. Perform ! CreateDataPropertyOrThrow(_A_, ! ToString(𝔽(_i_)), _capturedValue_).
              1. If the _i_<sup>th</sup> capture of _R_ was defined with a |GroupName|, then
                1. Let _s_ be the StringValue of the corresponding |RegExpIdentifierName|.
                1. Perform ! CreateDataPropertyOrThrow(_groups_, _s_, _capturedValue_).
            1. Return _A_.
          1. Let _rx_ be the *this* value.
          1. If Type(_rx_) is not Object, throw a *TypeError* exception.
          1. Let _S_ be ? ToString(_string_).
          1. Let _C_ be ? SpeciesConstructor(_rx_, %RegExp%).
          1. Let _flags_ be ? ToString(? Get(_rx_, *"flags"*)).
          1. If _flags_ contains *"u"*, let _unicodeMatching_ be *true*.
          1. Else, let _unicodeMatching_ be *false*.
          1. If _flags_ contains *"y"*, let _newFlags_ be _flags_.
          1. Else, let _newFlags_ be the string-concatenation of _flags_ and *"y"*.
          1. Let _splitter_ be ? Construct(_C_, « _rx_, _newFlags_ »).
          1. Let _A_ be ! ArrayCreate(0).
          1. Let _lengthA_ be 0.
          1. If _limit_ is *undefined*, let _lim_ be 2<sup>32</sup> - 1; else let _lim_ be ℝ(? ToUint32(_limit_)).
          1. If _lim_ is 0, return _A_.
          1. Let _size_ be the length of _S_.
          1. If _size_ is 0, then
            1. Let _z_ be ? RegExpExec(_splitter_, _S_).
            1. If _z_ is not *null*, return _A_.
            1. Perform ! CreateDataPropertyOrThrow(_A_, *"0"*, _S_).
            1. Return _A_.
          1. Let _p_ be 0.
          1. Let _q_ be _p_.
          1. Repeat, while _q_ < _size_,
            1. Perform ? Set(_splitter_, *"lastIndex"*, 𝔽(_q_), *true*).
            1. Let _z_ be ? RegExpExec(_splitter_, _S_).
            1. If _z_ is *null*, set _q_ to AdvanceStringIndex(_S_, _q_, _unicodeMatching_).
            1. Else,
              1. Let _e_ be ℝ(? ToLength(? Get(_splitter_, *"lastIndex"*))).
              1. Set _e_ to min(_e_, _size_).
              1. If _e_ = _p_, set _q_ to AdvanceStringIndex(_S_, _q_, _unicodeMatching_).
              1. Else,
                1. Let _T_ be the substring of _S_ from _p_ to _q_.
                1. Perform ! CreateDataPropertyOrThrow(_A_, ! ToString(𝔽(_lengthA_)), _T_).
                1. Set _lengthA_ to _lengthA_ + 1.
                1. If _lengthA_ = _lim_, return _A_.
                1. Set _p_ to _e_.
                1. Let _numberOfCaptures_ be ? LengthOfArrayLike(_z_).
                1. Set _numberOfCaptures_ to max(_numberOfCaptures_ - 1, 0).
                1. Let _i_ be 1.
                1. Repeat, while _i_ ≤ _numberOfCaptures_,
                  1. Let _nextCapture_ be ? Get(_z_, ! ToString(𝔽(_i_))).
                  1. Perform ! CreateDataPropertyOrThrow(_A_, ! ToString(𝔽(_lengthA_)), _nextCapture_).
                  1. Set _i_ to _i_ + 1.
                  1. Set _lengthA_ to _lengthA_ + 1.
                  1. If _lengthA_ = _lim_, return _A_.
                1. Set _q_ to _p_.
          1. Let _T_ be the substring of _S_ from _p_ to _size_.
          1. Perform ! CreateDataPropertyOrThrow(_A_, ! ToString(𝔽(_lengthA_)), _T_).
          1. Return _A_.
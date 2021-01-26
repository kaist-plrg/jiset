          1. Let _rx_ be the *this* value.
          1. If Type(_rx_) is not Object, throw a *TypeError* exception.
          1. Let _S_ be ? ToString(_string_).
          1. Let _C_ be ? SpeciesConstructor(_rx_, %RegExp%).
          1. Let _flags_ be ? ToString(? Get(_rx_, `"flags"`)).
          1. If _flags_ contains `"u"`, let _unicodeMatching_ be *true*.
          1. Else, let _unicodeMatching_ be *false*.
          1. If _flags_ contains `"y"`, let _newFlags_ be _flags_.
          1. Else, let _newFlags_ be the string that is the concatenation of _flags_ and `"y"`.
          1. Let _splitter_ be ? Construct(_C_, « _rx_, _newFlags_ »).
          1. Let _A_ be ArrayCreate(0).
          1. Let _lengthA_ be 0.
          1. If _limit_ is *undefined*, let _lim_ be 2<sup>32</sup>-1; else let _lim_ be ? ToUint32(_limit_).
          1. Let _size_ be the number of elements in _S_.
          1. Let _p_ be 0.
          1. If _lim_ = 0, return _A_.
          1. If _size_ = 0, then
            1. Let _z_ be ? RegExpExec(_splitter_, _S_).
            1. If _z_ is not *null*, return _A_.
            1. Perform ! CreateDataProperty(_A_, `"0"`, _S_).
            1. Return _A_.
          1. Let _q_ be _p_.
          1. Repeat, while _q_ < _size_
            1. Perform ? Set(_splitter_, `"lastIndex"`, _q_, *true*).
            1. Let _z_ be ? RegExpExec(_splitter_, _S_).
            1. If _z_ is *null*, let _q_ be AdvanceStringIndex(_S_, _q_, _unicodeMatching_).
            1. Else _z_ is not *null*,
              1. Let _e_ be ? ToLength(? Get(_splitter_, `"lastIndex"`)).
              1. Let _e_ be min(_e_, _size_).
              1. If _e_ = _p_, let _q_ be AdvanceStringIndex(_S_, _q_, _unicodeMatching_).
              1. Else _e_ ≠ _p_,
                1. Let _T_ be a String value equal to the substring of _S_ consisting of the elements at indices _p_ (inclusive) through _q_ (exclusive).
                1. Perform ! CreateDataProperty(_A_, ! ToString(_lengthA_), _T_).
                1. Let _lengthA_ be _lengthA_ + 1.
                1. If _lengthA_ = _lim_, return _A_.
                1. Let _p_ be _e_.
                1. Let _numberOfCaptures_ be ? ToLength(? Get(_z_, `"length"`)).
                1. Let _numberOfCaptures_ be max(_numberOfCaptures_-1, 0).
                1. Let _i_ be 1.
                1. Repeat, while _i_ ≤ _numberOfCaptures_,
                  1. Let _nextCapture_ be ? Get(_z_, ! ToString(_i_)).
                  1. Perform ! CreateDataProperty(_A_, ! ToString(_lengthA_), _nextCapture_).
                  1. Let _i_ be _i_ + 1.
                  1. Let _lengthA_ be _lengthA_ + 1.
                  1. If _lengthA_ = _lim_, return _A_.
                1. Let _q_ be _p_.
          1. Let _T_ be a String value equal to the substring of _S_ consisting of the elements at indices _p_ (inclusive) through _size_ (exclusive).
          1. Perform ! CreateDataProperty(_A_, ! ToString(_lengthA_), _T_).
          1. Return _A_.
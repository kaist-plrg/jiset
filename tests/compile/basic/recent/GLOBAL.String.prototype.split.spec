          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. If _separator_ is neither *undefined* nor *null*, then
            1. Let _splitter_ be ? GetMethod(_separator_, @@split).
            1. If _splitter_ is not *undefined*, then
              1. Return ? Call(_splitter_, _separator_, « _O_, _limit_ »).
          1. Let _S_ be ? ToString(_O_).
          1. Let _A_ be ! ArrayCreate(0).
          1. Let _lengthA_ be 0.
          1. If _limit_ is *undefined*, let _lim_ be 2<sup>32</sup> - 1; else let _lim_ be ℝ(? ToUint32(_limit_)).
          1. Let _R_ be ? ToString(_separator_).
          1. If _lim_ = 0, return _A_.
          1. If _separator_ is *undefined*, then
            1. Perform ! CreateDataPropertyOrThrow(_A_, *"0"*, _S_).
            1. Return _A_.
          1. Let _s_ be the length of _S_.
          1. If _s_ = 0, then
            1. If _R_ is not the empty String, then
              1. Perform ! CreateDataPropertyOrThrow(_A_, *"0"*, _S_).
            1. Return _A_.
          1. Let _p_ be 0.
          1. Let _q_ be _p_.
          1. Repeat, while _q_ ≠ _s_,
            1. Let _e_ be SplitMatch(_S_, _q_, _R_).
            1. If _e_ is ~not-matched~, set _q_ to _q_ + 1.
            1. Else,
              1. Assert: _e_ is a non-negative integer ≤ _s_.
              1. If _e_ = _p_, set _q_ to _q_ + 1.
              1. Else,
                1. Let _T_ be the substring of _S_ from _p_ to _q_.
                1. Perform ! CreateDataPropertyOrThrow(_A_, ! ToString(𝔽(_lengthA_)), _T_).
                1. Set _lengthA_ to _lengthA_ + 1.
                1. If _lengthA_ = _lim_, return _A_.
                1. Set _p_ to _e_.
                1. Set _q_ to _p_.
          1. Let _T_ be the substring of _S_ from _p_ to _s_.
          1. Perform ! CreateDataPropertyOrThrow(_A_, ! ToString(𝔽(_lengthA_)), _T_).
          1. Return _A_.
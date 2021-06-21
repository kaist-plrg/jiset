          1. [id="step-array-sort-comparefn"] If _comparefn_ is not *undefined* and IsCallable(_comparefn_) is *false*, throw a *TypeError* exception.
          1. Let _obj_ be ? ToObject(*this* value).
          1. [id="step-array-sort-len"] Let _len_ be ? LengthOfArrayLike(_obj_).
          1. Let _items_ be a new empty List.
          1. Let _k_ be 0.
          1. Repeat, while _k_ < _len_,
            1. Let _Pk_ be ! ToString(𝔽(_k_)).
            1. Let _kPresent_ be ? HasProperty(_obj_, _Pk_).
            1. If _kPresent_ is *true*, then
              1. Let _kValue_ be ? Get(_obj_, _Pk_).
              1. Append _kValue_ to _items_.
            1. Set _k_ to _k_ + 1.
          1. Let _itemCount_ be the number of elements in _items_.
          1. [id="step-array-sort"] Sort _items_ using an implementation-defined sequence of calls to SortCompare. If any such call returns an abrupt completion, stop before performing any further calls to SortCompare or steps in this algorithm and return that completion.
          1. Let _j_ be 0.
          1. Repeat, while _j_ < _itemCount_,
            1. Perform ? Set(_obj_, ! ToString(𝔽(_j_)), _items_[_j_], *true*).
            1. Set _j_ to _j_ + 1.
          1. Repeat, while _j_ < _len_,
            1. Perform ? DeletePropertyOrThrow(_obj_, ! ToString(𝔽(_j_))).
            1. Set _j_ to _j_ + 1.
          1. Return _obj_.
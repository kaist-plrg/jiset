            1. Let _r_ be the number of code units in _R_.
            1. Let _s_ be the number of code units in _S_.
            1. If _q_ + _r_ > _s_, return ~not-matched~.
            1. If there exists an integer _i_ between 0 (inclusive) and _r_ (exclusive) such that the code unit at index _q_ + _i_ within _S_ is different from the code unit at index _i_ within _R_, return ~not-matched~.
            1. Return _q_ + _r_.
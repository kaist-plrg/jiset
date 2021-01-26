          1. Assert: _fromBlock_ and _toBlock_ are distinct Data Block values.
          1. Assert: _fromIndex_, _toIndex_, and _count_ are integer values ≥ 0.
          1. Let _fromSize_ be the number of bytes in _fromBlock_.
          1. Assert: _fromIndex_+_count_ ≤ _fromSize_.
          1. Let _toSize_ be the number of bytes in _toBlock_.
          1. Assert: _toIndex_+_count_ ≤ _toSize_.
          1. Repeat, while _count_>0
            1. Set _toBlock_[_toIndex_] to the value of _fromBlock_[_fromIndex_].
            1. Increment _toIndex_ and _fromIndex_ each by 1.
            1. Decrement _count_ by 1.
          1. Return NormalCompletion(~empty~).
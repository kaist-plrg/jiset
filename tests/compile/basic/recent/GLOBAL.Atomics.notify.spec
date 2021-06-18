        1. Let _buffer_ be ? ValidateIntegerTypedArray(_typedArray_, *true*).
        1. Let _indexedPosition_ be ? ValidateAtomicAccess(_typedArray_, _index_).
        1. If _count_ is *undefined*, let _c_ be +∞.
        1. Else,
          1. Let _intCount_ be ? ToIntegerOrInfinity(_count_).
          1. Let _c_ be max(_intCount_, 0).
        1. Let _block_ be _buffer_.[[ArrayBufferData]].
        1. Let _arrayTypeName_ be _typedArray_.[[TypedArrayName]].
        1. If IsSharedArrayBuffer(_buffer_) is *false*, return *+0*<sub>𝔽</sub>.
        1. Let _WL_ be GetWaiterList(_block_, _indexedPosition_).
        1. Let _n_ be 0.
        1. Perform EnterCriticalSection(_WL_).
        1. Let _S_ be RemoveWaiters(_WL_, _c_).
        1. Repeat, while _S_ is not an empty List,
          1. Let _W_ be the first agent in _S_.
          1. Remove _W_ from the front of _S_.
          1. Perform NotifyWaiter(_WL_, _W_).
          1. Set _n_ to _n_ + 1.
        1. Perform LeaveCriticalSection(_WL_).
        1. Return 𝔽(_n_).
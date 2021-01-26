        1. Let _buffer_ be ? ValidateSharedIntegerTypedArray(_typedArray_, *true*).
        1. Let _i_ be ? ValidateAtomicAccess(_typedArray_, _index_).
        1. Let _v_ be ? ToInt32(_value_).
        1. Let _q_ be ? ToNumber(_timeout_).
        1. If _q_ is *NaN*, let _t_ be *+∞*, else let _t_ be max(_q_, 0).
        1. Let _B_ be AgentCanSuspend().
        1. If _B_ is *false*, throw a *TypeError* exception.
        1. Let _block_ be _buffer_.[[ArrayBufferData]].
        1. Let _offset_ be _typedArray_.[[ByteOffset]].
        1. Let _indexedPosition_ be (_i_ × 4) + _offset_.
        1. Let _WL_ be GetWaiterList(_block_, _indexedPosition_).
        1. Perform EnterCriticalSection(_WL_).
        1. Let _w_ be ! AtomicLoad(_typedArray_, _i_).
        1. If _v_ is not equal to _w_, then
          1. Perform LeaveCriticalSection(_WL_).
          1. Return the String `"not-equal"`.
        1. Let _W_ be AgentSignifier().
        1. Perform AddWaiter(_WL_, _W_).
        1. Let _awoken_ be Suspend(_WL_, _W_, _t_).
        1. Perform RemoveWaiter(_WL_, _W_).
        1. Perform LeaveCriticalSection(_WL_).
        1. If _awoken_ is *true*, return the String `"ok"`.
        1. Return the String `"timed-out"`.
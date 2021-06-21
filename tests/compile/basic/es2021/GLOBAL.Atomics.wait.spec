        1. Let _buffer_ be ? ValidateIntegerTypedArray(_typedArray_, *true*).
        1. If IsSharedArrayBuffer(_buffer_) is *false*, throw a *TypeError* exception.
        1. Let _indexedPosition_ be ? ValidateAtomicAccess(_typedArray_, _index_).
        1. Let _arrayTypeName_ be _typedArray_.[[TypedArrayName]].
        1. If _arrayTypeName_ is *"BigInt64Array"*, let _v_ be ? ToBigInt64(_value_).
        1. Otherwise, let _v_ be ? ToInt32(_value_).
        1. Let _q_ be ? ToNumber(_timeout_).
        1. If _q_ is *NaN* or *+∞*<sub>𝔽</sub>, let _t_ be +∞; else if _q_ is *-∞*<sub>𝔽</sub>, let _t_ be 0; else let _t_ be max(ℝ(_q_), 0).
        1. Let _B_ be AgentCanSuspend().
        1. If _B_ is *false*, throw a *TypeError* exception.
        1. Let _block_ be _buffer_.[[ArrayBufferData]].
        1. Let _WL_ be GetWaiterList(_block_, _indexedPosition_).
        1. Perform EnterCriticalSection(_WL_).
        1. Let _elementType_ be the Element Type value in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _arrayTypeName_.
        1. Let _w_ be ! GetValueFromBuffer(_buffer_, _indexedPosition_, _elementType_, *true*, ~SeqCst~).
        1. If _v_ ≠ _w_, then
          1. Perform LeaveCriticalSection(_WL_).
          1. Return the String *"not-equal"*.
        1. Let _W_ be AgentSignifier().
        1. Perform AddWaiter(_WL_, _W_).
        1. Let _notified_ be SuspendAgent(_WL_, _W_, _t_).
        1. If _notified_ is *true*, then
          1. Assert: _W_ is not on the list of waiters in _WL_.
        1. Else,
          1. Perform RemoveWaiter(_WL_, _W_).
        1. Perform LeaveCriticalSection(_WL_).
        1. If _notified_ is *true*, return the String *"ok"*.
        1. Return the String *"timed-out"*.
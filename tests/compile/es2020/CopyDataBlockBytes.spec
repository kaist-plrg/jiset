          1. Assert: _fromBlock_ and _toBlock_ are distinct Data Block or Shared Data Block values.
          1. Assert: _fromIndex_, _toIndex_, and _count_ are integer values ≥ 0.
          1. Let _fromSize_ be the number of bytes in _fromBlock_.
          1. Assert: _fromIndex_ + _count_ ≤ _fromSize_.
          1. Let _toSize_ be the number of bytes in _toBlock_.
          1. Assert: _toIndex_ + _count_ ≤ _toSize_.
          1. Repeat, while _count_ > 0
            1. If _fromBlock_ is a Shared Data Block, then
              1. Let _execution_ be the [[CandidateExecution]] field of the surrounding agent's Agent Record.
              1. Let _eventList_ be the [[EventList]] field of the element in _execution_.[[EventsRecords]] whose [[AgentSignifier]] is AgentSignifier().
              1. Let _bytes_ be a List of length 1 that contains a nondeterministically chosen byte value.
              1. NOTE: In implementations, _bytes_ is the result of a non-atomic read instruction on the underlying hardware. The nondeterminism is a semantic prescription of the memory model to describe observable behaviour of hardware with weak consistency.
              1. Let _readEvent_ be ReadSharedMemory { [[Order]]: ~Unordered~, [[NoTear]]: *true*, [[Block]]: _fromBlock_, [[ByteIndex]]: _fromIndex_, [[ElementSize]]: 1 }.
              1. Append _readEvent_ to _eventList_.
              1. Append Chosen Value Record { [[Event]]: _readEvent_, [[ChosenValue]]: _bytes_ } to _execution_.[[ChosenValues]].
              1. If _toBlock_ is a Shared Data Block, then
                1. Append WriteSharedMemory { [[Order]]: ~Unordered~, [[NoTear]]: *true*, [[Block]]: _toBlock_, [[ByteIndex]]: _toIndex_, [[ElementSize]]: 1, [[Payload]]: _bytes_ } to _eventList_.
              1. Else,
                1. Set _toBlock_[_toIndex_] to _bytes_[0].
            1. Else,
              1. Assert: _toBlock_ is not a Shared Data Block.
              1. Set _toBlock_[_toIndex_] to _fromBlock_[_fromIndex_].
            1. Set _toIndex_ to _toIndex_ + 1.
            1. Set _fromIndex_ to _fromIndex_ + 1.
            1. Set _count_ to _count_ - 1.
          1. Return NormalCompletion(~empty~).
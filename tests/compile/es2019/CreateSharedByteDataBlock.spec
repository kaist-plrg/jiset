          1. Assert: _size_ ≥ 0.
          1. Let _db_ be a new Shared Data Block value consisting of _size_ bytes. If it is impossible to create such a Shared Data Block, throw a *RangeError* exception.
          1. Let _execution_ be the [[CandidateExecution]] field of the surrounding agent's Agent Record.
          1. Let _eventList_ be the [[EventList]] field of the element in _execution_.[[EventsRecords]] whose [[AgentSignifier]] is AgentSignifier().
          1. Let _zero_ be « 0 ».
          1. For each index _i_ of _db_, do
            1. Append WriteSharedMemory { [[Order]]: `"Init"`, [[NoTear]]: *true*, [[Block]]: _db_, [[ByteIndex]]: _i_, [[ElementSize]]: 1, [[Payload]]: _zero_ } to _eventList_.
          1. Return _db_.
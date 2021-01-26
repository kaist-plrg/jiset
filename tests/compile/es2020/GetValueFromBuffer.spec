          1. Assert: IsDetachedBuffer(_arrayBuffer_) is *false*.
          1. Assert: There are sufficient bytes in _arrayBuffer_ starting at _byteIndex_ to represent a value of _type_.
          1. Assert: ! IsNonNegativeInteger(_byteIndex_) is *true*.
          1. Let _block_ be _arrayBuffer_.[[ArrayBufferData]].
          1. Let _elementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for Element Type _type_.
          1. If IsSharedArrayBuffer(_arrayBuffer_) is *true*, then
            1. Let _execution_ be the [[CandidateExecution]] field of the surrounding agent's Agent Record.
            1. Let _eventList_ be the [[EventList]] field of the element in _execution_.[[EventsRecords]] whose [[AgentSignifier]] is AgentSignifier().
            1. If _isTypedArray_ is *true* and IsNoTearConfiguration(_type_, _order_) is *true*, let _noTear_ be *true*; otherwise let _noTear_ be *false*.
            1. Let _rawValue_ be a List of length _elementSize_ of nondeterministically chosen byte values.
            1. NOTE: In implementations, _rawValue_ is the result of a non-atomic or atomic read instruction on the underlying hardware. The nondeterminism is a semantic prescription of the memory model to describe observable behaviour of hardware with weak consistency.
            1. Let _readEvent_ be ReadSharedMemory { [[Order]]: _order_, [[NoTear]]: _noTear_, [[Block]]: _block_, [[ByteIndex]]: _byteIndex_, [[ElementSize]]: _elementSize_ }.
            1. Append _readEvent_ to _eventList_.
            1. Append Chosen Value Record { [[Event]]: _readEvent_, [[ChosenValue]]: _rawValue_ } to _execution_.[[ChosenValues]].
          1. Else, let _rawValue_ be a List of _elementSize_ containing, in order, the _elementSize_ sequence of bytes starting with _block_[_byteIndex_].
          1. If _isLittleEndian_ is not present, set _isLittleEndian_ to the value of the [[LittleEndian]] field of the surrounding agent's Agent Record.
          1. Return RawBytesToNumeric(_type_, _rawValue_, _isLittleEndian_).
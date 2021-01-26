          1. Assert: IsSharedArrayBuffer(_arrayBuffer_) is *true*.
          1. Assert: There are sufficient bytes in _arrayBuffer_ starting at _byteIndex_ to represent a value of _type_.
          1. Assert: ! IsNonNegativeInteger(_byteIndex_) is *true*.
          1. Assert: Type(_value_) is BigInt if ! IsBigIntElementType(_type_) is *true*; otherwise, Type(_value_) is Number.
          1. Let _block_ be _arrayBuffer_.[[ArrayBufferData]].
          1. Let _elementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for Element Type _type_.
          1. If _isLittleEndian_ is not present, set _isLittleEndian_ to the value of the [[LittleEndian]] field of the surrounding agent's Agent Record.
          1. Let _rawBytes_ be NumericToRawBytes(_type_, _value_, _isLittleEndian_).
          1. Let _execution_ be the [[CandidateExecution]] field of the surrounding agent's Agent Record.
          1. Let _eventList_ be the [[EventList]] field of the element in _execution_.[[EventsRecords]] whose [[AgentSignifier]] is AgentSignifier().
          1. Let _rawBytesRead_ be a List of length _elementSize_ of nondeterministically chosen byte values.
          1. NOTE: In implementations, _rawBytesRead_ is the result of a load-link, of a load-exclusive, or of an operand of a read-modify-write instruction on the underlying hardware. The nondeterminism is a semantic prescription of the memory model to describe observable behaviour of hardware with weak consistency.
          1. Let _rmwEvent_ be ReadModifyWriteSharedMemory { [[Order]]: ~SeqCst~, [[NoTear]]: *true*, [[Block]]: _block_, [[ByteIndex]]: _byteIndex_, [[ElementSize]]: _elementSize_, [[Payload]]: _rawBytes_, [[ModifyOp]]: _op_ }.
          1. Append _rmwEvent_ to _eventList_.
          1. Append Chosen Value Record { [[Event]]: _rmwEvent_, [[ChosenValue]]: _rawBytesRead_ } to _execution_.[[ChosenValues]].
          1. Return RawBytesToNumeric(_type_, _rawBytesRead_, _isLittleEndian_).
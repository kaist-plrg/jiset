        1. Let _buffer_ be ? ValidateIntegerTypedArray(_typedArray_).
        1. Let _block_ be _buffer_.[[ArrayBufferData]].
        1. Let _indexedPosition_ be ? ValidateAtomicAccess(_typedArray_, _index_).
        1. Let _arrayTypeName_ be _typedArray_.[[TypedArrayName]].
        1. If _typedArray_.[[ContentType]] is ~BigInt~, then
          1. Let _expected_ be ? ToBigInt(_expectedValue_).
          1. Let _replacement_ be ? ToBigInt(_replacementValue_).
        1. Else,
          1. Let _expected_ be 𝔽(? ToIntegerOrInfinity(_expectedValue_)).
          1. Let _replacement_ be 𝔽(? ToIntegerOrInfinity(_replacementValue_)).
        1. If IsDetachedBuffer(_buffer_) is *true*, throw a *TypeError* exception.
        1. NOTE: The above check is not redundant with the check in ValidateIntegerTypedArray because the call to ToBigInt or ToIntegerOrInfinity on the preceding lines can have arbitrary side effects, which could cause the buffer to become detached.
        1. Let _elementType_ be the Element Type value in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _arrayTypeName_.
        1. Let _elementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for Element Type _elementType_.
        1. Let _isLittleEndian_ be the value of the [[LittleEndian]] field of the surrounding agent's Agent Record.
        1. Let _expectedBytes_ be NumericToRawBytes(_elementType_, _expected_, _isLittleEndian_).
        1. Let _replacementBytes_ be NumericToRawBytes(_elementType_, _replacement_, _isLittleEndian_).
        1. If IsSharedArrayBuffer(_buffer_) is *true*, then
          1. Let _execution_ be the [[CandidateExecution]] field of the surrounding agent's Agent Record.
          1. Let _eventList_ be the [[EventList]] field of the element in _execution_.[[EventsRecords]] whose [[AgentSignifier]] is AgentSignifier().
          1. Let _rawBytesRead_ be a List of length _elementSize_ whose elements are nondeterministically chosen byte values.
          1. NOTE: In implementations, _rawBytesRead_ is the result of a load-link, of a load-exclusive, or of an operand of a read-modify-write instruction on the underlying hardware. The nondeterminism is a semantic prescription of the memory model to describe observable behaviour of hardware with weak consistency.
          1. NOTE: The comparison of the expected value and the read value is performed outside of the read-modify-write modification function to avoid needlessly strong synchronization when the expected value is not equal to the read value.
          1. If ByteListEqual(_rawBytesRead_, _expectedBytes_) is *true*, then
            1. Let _second_ be a new read-modify-write modification function with parameters (_oldBytes_, _newBytes_) that captures nothing and performs the following steps atomically when called:
              1. Return _newBytes_.
            1. Let _event_ be ReadModifyWriteSharedMemory { [[Order]]: ~SeqCst~, [[NoTear]]: *true*, [[Block]]: _block_, [[ByteIndex]]: _indexedPosition_, [[ElementSize]]: _elementSize_, [[Payload]]: _replacementBytes_, [[ModifyOp]]: _second_ }.
          1. Else,
            1. Let _event_ be ReadSharedMemory { [[Order]]: ~SeqCst~, [[NoTear]]: *true*, [[Block]]: _block_, [[ByteIndex]]: _indexedPosition_, [[ElementSize]]: _elementSize_ }.
          1. Append _event_ to _eventList_.
          1. Append Chosen Value Record { [[Event]]: _event_, [[ChosenValue]]: _rawBytesRead_ } to _execution_.[[ChosenValues]].
        1. Else,
          1. Let _rawBytesRead_ be a List of length _elementSize_ whose elements are the sequence of _elementSize_ bytes starting with _block_[_indexedPosition_].
          1. If ByteListEqual(_rawBytesRead_, _expectedBytes_) is *true*, then
            1. Store the individual bytes of _replacementBytes_ into _block_, starting at _block_[_indexedPosition_].
        1. Return RawBytesToNumeric(_elementType_, _rawBytesRead_, _isLittleEndian_).
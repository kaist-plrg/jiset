          1. Assert: IsDetachedBuffer(_arrayBuffer_) is *false*.
          1. Assert: There are sufficient bytes in _arrayBuffer_ starting at _byteIndex_ to represent a value of _type_.
          1. Assert: ! IsNonNegativeInteger(_byteIndex_) is *true*.
          1. Assert: Type(_value_) is BigInt if ! IsBigIntElementType(_type_) is *true*; otherwise, Type(_value_) is Number.
          1. Let _block_ be _arrayBuffer_.[[ArrayBufferData]].
          1. Let _elementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for Element Type _type_.
          1. If _isLittleEndian_ is not present, set _isLittleEndian_ to the value of the [[LittleEndian]] field of the surrounding agent's Agent Record.
          1. Let _rawBytes_ be NumericToRawBytes(_type_, _value_, _isLittleEndian_).
          1. If IsSharedArrayBuffer(_arrayBuffer_) is *true*, then
            1. Let _execution_ be the [[CandidateExecution]] field of the surrounding agent's Agent Record.
            1. Let _eventList_ be the [[EventList]] field of the element in _execution_.[[EventsRecords]] whose [[AgentSignifier]] is AgentSignifier().
            1. If _isTypedArray_ is *true* and IsNoTearConfiguration(_type_, _order_) is *true*, let _noTear_ be *true*; otherwise let _noTear_ be *false*.
            1. Append WriteSharedMemory { [[Order]]: _order_, [[NoTear]]: _noTear_, [[Block]]: _block_, [[ByteIndex]]: _byteIndex_, [[ElementSize]]: _elementSize_, [[Payload]]: _rawBytes_ } to _eventList_.
          1. Else, store the individual bytes of _rawBytes_ into _block_, in order, starting at _block_[_byteIndex_].
          1. Return NormalCompletion(*undefined*).
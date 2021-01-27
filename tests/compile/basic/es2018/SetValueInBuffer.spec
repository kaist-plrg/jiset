          1. Assert: IsDetachedBuffer(_arrayBuffer_) is *false*.
          1. Assert: There are sufficient bytes in _arrayBuffer_ starting at _byteIndex_ to represent a value of _type_.
          1. Assert: _byteIndex_ is an integer value ≥ 0.
          1. Assert: Type(_value_) is Number.
          1. Let _block_ be _arrayBuffer_.[[ArrayBufferData]].
          1. Let _elementSize_ be the Number value of the Element Size value specified in <emu-xref href="#table-49"></emu-xref> for Element Type _type_.
          1. If _isLittleEndian_ is not present, set _isLittleEndian_ to the value of the [[LittleEndian]] field of the surrounding agent's Agent Record.
          1. Let _rawBytes_ be NumberToRawBytes(_type_, _value_, _isLittleEndian_).
          1. If IsSharedArrayBuffer(_arrayBuffer_) is *true*, then
            1. Let _execution_ be the [[CandidateExecution]] field of the surrounding agent's Agent Record.
            1. Let _eventList_ be the [[EventList]] field of the element in _execution_.[[EventLists]] whose [[AgentSignifier]] is AgentSignifier().
            1. If _isTypedArray_ is *true* and _type_ is `"Int8"`, `"Uint8"`, `"Int16"`, `"Uint16"`, `"Int32"`, or `"Uint32"`, let _noTear_ be *true*; otherwise let _noTear_ be *false*.
            1. Append WriteSharedMemory { [[Order]]: _order_, [[NoTear]]: _noTear_, [[Block]]: _block_, [[ByteIndex]]: _byteIndex_, [[ElementSize]]: _elementSize_, [[Payload]]: _rawBytes_ } to _eventList_.
          1. Else, store the individual bytes of _rawBytes_ into _block_, in order, starting at _block_[_byteIndex_].
          1. Return NormalCompletion(*undefined*).
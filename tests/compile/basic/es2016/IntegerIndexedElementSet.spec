          1. Assert: Type(_index_) is Number.
          1. Assert: _O_ is an Object that has [[ViewedArrayBuffer]], [[ArrayLength]], [[ByteOffset]], and [[TypedArrayName]] internal slots.
          1. Let _numValue_ be ? ToNumber(_value_).
          1. Let _buffer_ be the value of _O_'s [[ViewedArrayBuffer]] internal slot.
          1. If IsDetachedBuffer(_buffer_) is *true*, throw a *TypeError* exception.
          1. If IsInteger(_index_) is *false*, return *false*.
          1. If _index_ = *-0*, return *false*.
          1. Let _length_ be the value of _O_'s [[ArrayLength]] internal slot.
          1. If _index_ < 0 or _index_ ≥ _length_, return *false*.
          1. Let _offset_ be the value of _O_'s [[ByteOffset]] internal slot.
          1. Let _arrayTypeName_ be the String value of _O_'s [[TypedArrayName]] internal slot.
          1. Let _elementSize_ be the Number value of the Element Size value specified in <emu-xref href="#table-49"></emu-xref> for _arrayTypeName_.
          1. Let _indexedPosition_ be (_index_ × _elementSize_) + _offset_.
          1. Let _elementType_ be the String value of the Element Type value in <emu-xref href="#table-49"></emu-xref> for _arrayTypeName_.
          1. Perform SetValueInBuffer(_buffer_, _indexedPosition_, _elementType_, _numValue_).
          1. Return *true*.
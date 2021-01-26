          1. Assert: Type(_index_) is Number.
          1. Assert: _O_ is an Object that has [[ViewedArrayBuffer]], [[ArrayLength]], [[ByteOffset]], and [[TypedArrayName]] internal slots.
          1. Let _buffer_ be _O_.[[ViewedArrayBuffer]].
          1. If IsDetachedBuffer(_buffer_) is *true*, throw a *TypeError* exception.
          1. If IsInteger(_index_) is *false*, return *undefined*.
          1. If _index_ = *-0*, return *undefined*.
          1. Let _length_ be _O_.[[ArrayLength]].
          1. If _index_ < 0 or _index_ ≥ _length_, return *undefined*.
          1. Let _offset_ be _O_.[[ByteOffset]].
          1. Let _arrayTypeName_ be the String value of _O_.[[TypedArrayName]].
          1. Let _elementSize_ be the Number value of the Element Size value specified in <emu-xref href="#table-49"></emu-xref> for _arrayTypeName_.
          1. Let _indexedPosition_ be (_index_ × _elementSize_) + _offset_.
          1. Let _elementType_ be the String value of the Element Type value in <emu-xref href="#table-49"></emu-xref> for _arrayTypeName_.
          1. Return GetValueFromBuffer(_buffer_, _indexedPosition_, _elementType_, *true*, `"Unordered"`).
          1. Assert: _O_ is an Integer-Indexed exotic object.
          1. Assert: Type(_index_) is Number.
          1. Let _buffer_ be _O_.[[ViewedArrayBuffer]].
          1. If IsDetachedBuffer(_buffer_) is *true*, throw a *TypeError* exception.
          1. If ! IsValidIntegerIndex(_O_, _index_) is *false*, return *undefined*.
          1. Let _offset_ be _O_.[[ByteOffset]].
          1. Let _arrayTypeName_ be the String value of _O_.[[TypedArrayName]].
          1. Let _elementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _arrayTypeName_.
          1. Let _indexedPosition_ be (_index_ × _elementSize_) + _offset_.
          1. Let _elementType_ be the Element Type value in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _arrayTypeName_.
          1. Return GetValueFromBuffer(_buffer_, _indexedPosition_, _elementType_, *true*, ~Unordered~).
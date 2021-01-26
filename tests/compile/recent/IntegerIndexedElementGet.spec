          1. Assert: _O_ is an Integer-Indexed exotic object.
          1. If ! IsValidIntegerIndex(_O_, _index_) is *false*, return *undefined*.
          1. Let _offset_ be _O_.[[ByteOffset]].
          1. Let _arrayTypeName_ be the String value of _O_.[[TypedArrayName]].
          1. Let _elementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _arrayTypeName_.
          1. Let _indexedPosition_ be (ℝ(_index_) × _elementSize_) + _offset_.
          1. Let _elementType_ be the Element Type value in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _arrayTypeName_.
          1. Return GetValueFromBuffer(_O_.[[ViewedArrayBuffer]], _indexedPosition_, _elementType_, *true*, ~Unordered~).
          1. Assert: _O_ is an Integer-Indexed exotic object.
          1. If _O_.[[ContentType]] is ~BigInt~, let _numValue_ be ? ToBigInt(_value_).
          1. Otherwise, let _numValue_ be ? ToNumber(_value_).
          1. If ! IsValidIntegerIndex(_O_, _index_) is *true*, then
            1. Let _offset_ be _O_.[[ByteOffset]].
            1. Let _arrayTypeName_ be the String value of _O_.[[TypedArrayName]].
            1. Let _elementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _arrayTypeName_.
            1. Let _indexedPosition_ be (ℝ(_index_) × _elementSize_) + _offset_.
            1. Let _elementType_ be the Element Type value in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _arrayTypeName_.
            1. Perform SetValueInBuffer(_O_.[[ViewedArrayBuffer]], _indexedPosition_, _elementType_, _numValue_, *true*, ~Unordered~).
          1. Return NormalCompletion(*undefined*).
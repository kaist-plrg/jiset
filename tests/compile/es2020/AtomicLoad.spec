          1. Let _buffer_ be ? ValidateSharedIntegerTypedArray(_typedArray_).
          1. Let _i_ be ? ValidateAtomicAccess(_typedArray_, _index_).
          1. Let _arrayTypeName_ be _typedArray_.[[TypedArrayName]].
          1. Let _elementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _arrayTypeName_.
          1. Let _elementType_ be the Element Type value in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _arrayTypeName_.
          1. Let _offset_ be _typedArray_.[[ByteOffset]].
          1. Let _indexedPosition_ be (_i_ × _elementSize_) + _offset_.
          1. Return GetValueFromBuffer(_buffer_, _indexedPosition_, _elementType_, *true*, ~SeqCst~).
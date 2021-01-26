        1. Let _buffer_ be ? ValidateSharedIntegerTypedArray(_typedArray_).
        1. Let _i_ be ? ValidateAtomicAccess(_typedArray_, _index_).
        1. Let _expected_ be ? ToInteger(_expectedValue_).
        1. Let _replacement_ be ? ToInteger(_replacementValue_).
        1. Let _arrayTypeName_ be _typedArray_.[[TypedArrayName]].
        1. Let _elementType_ be the String value of the Element Type value in <emu-xref href="#table-49"></emu-xref> for _arrayTypeName_.
        1. Let _isLittleEndian_ be the value of the [[LittleEndian]] field of the surrounding agent's Agent Record.
        1. Let _expectedBytes_ be NumberToRawBytes(_elementType_, _expected_, _isLittleEndian_).
        1. Let _elementSize_ be the Number value of the Element Size value specified in <emu-xref href="#table-49"></emu-xref> for _arrayTypeName_.
        1. Let _offset_ be _typedArray_.[[ByteOffset]].
        1. Let _indexedPosition_ be (_i_ × _elementSize_) + _offset_.
        1. Let `compareExchange` denote a semantic function of two List of byte values arguments that returns the second argument if the first argument is element-wise equal to _expectedBytes_.
        1. Return GetModifySetValueInBuffer(_buffer_, _indexedPosition_, _elementType_, _replacement_, `compareExchange`).
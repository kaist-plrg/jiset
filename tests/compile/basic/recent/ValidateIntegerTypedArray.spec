          1. If _waitable_ is not present, set _waitable_ to *false*.
          1. Let _buffer_ be ? ValidateTypedArray(_typedArray_).
          1. Let _typeName_ be _typedArray_.[[TypedArrayName]].
          1. Let _type_ be the Element Type value in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _typeName_.
          1. If _waitable_ is *true*, then
            1. If _typeName_ is not *"Int32Array"* or *"BigInt64Array"*, throw a *TypeError* exception.
          1. Else,
            1. If ! IsUnclampedIntegerElementType(_type_) is *false* and ! IsBigIntElementType(_type_) is *false*, throw a *TypeError* exception.
          1. Return _buffer_.
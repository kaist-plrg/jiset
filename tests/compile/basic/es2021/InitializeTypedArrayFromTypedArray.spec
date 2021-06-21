            1. Assert: _O_ is an Object that has a [[TypedArrayName]] internal slot.
            1. Assert: _srcArray_ is an Object that has a [[TypedArrayName]] internal slot.
            1. Let _srcData_ be _srcArray_.[[ViewedArrayBuffer]].
            1. If IsDetachedBuffer(_srcData_) is *true*, throw a *TypeError* exception.
            1. Let _constructorName_ be the String value of _O_.[[TypedArrayName]].
            1. Let _elementType_ be the Element Type value in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _constructorName_.
            1. Let _elementLength_ be _srcArray_.[[ArrayLength]].
            1. Let _srcName_ be the String value of _srcArray_.[[TypedArrayName]].
            1. Let _srcType_ be the Element Type value in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _srcName_.
            1. Let _srcElementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _srcName_.
            1. Let _srcByteOffset_ be _srcArray_.[[ByteOffset]].
            1. Let _elementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _constructorName_.
            1. Let _byteLength_ be _elementSize_ × _elementLength_.
            1. If IsSharedArrayBuffer(_srcData_) is *false*, then
              1. Let _bufferConstructor_ be ? SpeciesConstructor(_srcData_, %ArrayBuffer%).
            1. Else,
              1. Let _bufferConstructor_ be %ArrayBuffer%.
            1. If _elementType_ is the same as _srcType_, then
              1. Let _data_ be ? CloneArrayBuffer(_srcData_, _srcByteOffset_, _byteLength_, _bufferConstructor_).
            1. Else,
              1. Let _data_ be ? AllocateArrayBuffer(_bufferConstructor_, _byteLength_).
              1. If IsDetachedBuffer(_srcData_) is *true*, throw a *TypeError* exception.
              1. If _srcArray_.[[ContentType]] ≠ _O_.[[ContentType]], throw a *TypeError* exception.
              1. Let _srcByteIndex_ be _srcByteOffset_.
              1. Let _targetByteIndex_ be 0.
              1. Let _count_ be _elementLength_.
              1. Repeat, while _count_ > 0,
                1. Let _value_ be GetValueFromBuffer(_srcData_, _srcByteIndex_, _srcType_, *true*, ~Unordered~).
                1. Perform SetValueInBuffer(_data_, _targetByteIndex_, _elementType_, _value_, *true*, ~Unordered~).
                1. Set _srcByteIndex_ to _srcByteIndex_ + _srcElementSize_.
                1. Set _targetByteIndex_ to _targetByteIndex_ + _elementSize_.
                1. Set _count_ to _count_ - 1.
            1. Set _O_.[[ViewedArrayBuffer]] to _data_.
            1. Set _O_.[[ByteLength]] to _byteLength_.
            1. Set _O_.[[ByteOffset]] to 0.
            1. Set _O_.[[ArrayLength]] to _elementLength_.
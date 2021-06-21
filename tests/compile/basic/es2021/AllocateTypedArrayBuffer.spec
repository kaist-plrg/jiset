            1. Assert: _O_ is an Object that has a [[ViewedArrayBuffer]] internal slot.
            1. Assert: _O_.[[ViewedArrayBuffer]] is *undefined*.
            1. Let _constructorName_ be the String value of _O_.[[TypedArrayName]].
            1. Let _elementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _constructorName_.
            1. Let _byteLength_ be _elementSize_ × _length_.
            1. Let _data_ be ? AllocateArrayBuffer(%ArrayBuffer%, _byteLength_).
            1. Set _O_.[[ViewedArrayBuffer]] to _data_.
            1. Set _O_.[[ByteLength]] to _byteLength_.
            1. Set _O_.[[ByteOffset]] to 0.
            1. Set _O_.[[ArrayLength]] to _length_.
            1. Return _O_.
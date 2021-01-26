            1. Assert: _O_ is an Object that has a [[ViewedArrayBuffer]] internal slot.
            1. Assert: The [[ViewedArrayBuffer]] internal slot of _O_ is *undefined*.
            1. Assert: _length_ ≥ 0.
            1. Let _constructorName_ be the String value of _O_'s [[TypedArrayName]] internal slot.
            1. Let _elementSize_ be the Element Size value in <emu-xref href="#table-49"></emu-xref> for _constructorName_.
            1. Let _byteLength_ be _elementSize_ × _length_.
            1. Let _data_ be ? AllocateArrayBuffer(%ArrayBuffer%, _byteLength_).
            1. Set _O_'s [[ViewedArrayBuffer]] internal slot to _data_.
            1. Set _O_'s [[ByteLength]] internal slot to _byteLength_.
            1. Set _O_'s [[ByteOffset]] internal slot to 0.
            1. Set _O_'s [[ArrayLength]] internal slot to _length_.
            1. Return _O_.
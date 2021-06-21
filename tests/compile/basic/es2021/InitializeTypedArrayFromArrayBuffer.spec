            1. Assert: _O_ is an Object that has a [[TypedArrayName]] internal slot.
            1. Assert: _buffer_ is an Object that has an [[ArrayBufferData]] internal slot.
            1. Let _constructorName_ be the String value of _O_.[[TypedArrayName]].
            1. Let _elementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _constructorName_.
            1. Let _offset_ be ? ToIndex(_byteOffset_).
            1. If _offset_ modulo _elementSize_ ≠ 0, throw a *RangeError* exception.
            1. If _length_ is not *undefined*, then
              1. Let _newLength_ be ? ToIndex(_length_).
            1. If IsDetachedBuffer(_buffer_) is *true*, throw a *TypeError* exception.
            1. Let _bufferByteLength_ be _buffer_.[[ArrayBufferByteLength]].
            1. If _length_ is *undefined*, then
              1. If _bufferByteLength_ modulo _elementSize_ ≠ 0, throw a *RangeError* exception.
              1. Let _newByteLength_ be _bufferByteLength_ - _offset_.
              1. If _newByteLength_ < 0, throw a *RangeError* exception.
            1. Else,
              1. Let _newByteLength_ be _newLength_ × _elementSize_.
              1. If _offset_ + _newByteLength_ > _bufferByteLength_, throw a *RangeError* exception.
            1. Set _O_.[[ViewedArrayBuffer]] to _buffer_.
            1. Set _O_.[[ByteLength]] to _newByteLength_.
            1. Set _O_.[[ByteOffset]] to _offset_.
            1. Set _O_.[[ArrayLength]] to _newByteLength_ / _elementSize_.
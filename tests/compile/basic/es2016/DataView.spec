          1. If NewTarget is *undefined*, throw a *TypeError* exception.
          1. If Type(_buffer_) is not Object, throw a *TypeError* exception.
          1. If _buffer_ does not have an [[ArrayBufferData]] internal slot, throw a *TypeError* exception.
          1. Let _numberOffset_ be ? ToNumber(_byteOffset_).
          1. Let _offset_ be ToInteger(_numberOffset_).
          1. If _numberOffset_ ≠ _offset_ or _offset_ < 0, throw a *RangeError* exception.
          1. If IsDetachedBuffer(_buffer_) is *true*, throw a *TypeError* exception.
          1. Let _bufferByteLength_ be the value of _buffer_'s [[ArrayBufferByteLength]] internal slot.
          1. If _offset_ > _bufferByteLength_, throw a *RangeError* exception.
          1. If _byteLength_ is *undefined*, then
            1. Let _viewByteLength_ be _bufferByteLength_ - _offset_.
          1. Else,
            1. Let _viewByteLength_ be ? ToLength(_byteLength_).
            1. If _offset_+_viewByteLength_ > _bufferByteLength_, throw a *RangeError* exception.
          1. Let _O_ be ? OrdinaryCreateFromConstructor(NewTarget, `"%DataViewPrototype%"`, « [[DataView]], [[ViewedArrayBuffer]], [[ByteLength]], [[ByteOffset]] »).
          1. Set _O_'s [[DataView]] internal slot to *true*.
          1. Set _O_'s [[ViewedArrayBuffer]] internal slot to _buffer_.
          1. Set _O_'s [[ByteLength]] internal slot to _viewByteLength_.
          1. Set _O_'s [[ByteOffset]] internal slot to _offset_.
          1. Return _O_.
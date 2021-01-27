          1. If NewTarget is *undefined*, throw a *TypeError* exception.
          1. Perform ? RequireInternalSlot(_buffer_, [[ArrayBufferData]]).
          1. Let _offset_ be ? ToIndex(_byteOffset_).
          1. If IsDetachedBuffer(_buffer_) is *true*, throw a *TypeError* exception.
          1. Let _bufferByteLength_ be _buffer_.[[ArrayBufferByteLength]].
          1. If _offset_ > _bufferByteLength_, throw a *RangeError* exception.
          1. If _byteLength_ is *undefined*, then
            1. Let _viewByteLength_ be _bufferByteLength_ - _offset_.
          1. Else,
            1. Let _viewByteLength_ be ? ToIndex(_byteLength_).
            1. If _offset_ + _viewByteLength_ > _bufferByteLength_, throw a *RangeError* exception.
          1. Let _O_ be ? OrdinaryCreateFromConstructor(NewTarget, *"%DataView.prototype%"*, « [[DataView]], [[ViewedArrayBuffer]], [[ByteLength]], [[ByteOffset]] »).
          1. If IsDetachedBuffer(_buffer_) is *true*, throw a *TypeError* exception.
          1. Set _O_.[[ViewedArrayBuffer]] to _buffer_.
          1. Set _O_.[[ByteLength]] to _viewByteLength_.
          1. Set _O_.[[ByteOffset]] to _offset_.
          1. Return _O_.
          1. Let _O_ be the *this* value.
          1. If Type(_O_) is not Object, throw a *TypeError* exception.
          1. If _O_ does not have a [[DataView]] internal slot, throw a *TypeError* exception.
          1. Assert: _O_ has a [[ViewedArrayBuffer]] internal slot.
          1. Let _buffer_ be _O_.[[ViewedArrayBuffer]].
          1. If IsDetachedBuffer(_buffer_) is *true*, throw a *TypeError* exception.
          1. Let _offset_ be _O_.[[ByteOffset]].
          1. Return _offset_.
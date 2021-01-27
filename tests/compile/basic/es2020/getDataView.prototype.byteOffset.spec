          1. Let _O_ be the *this* value.
          1. Perform ? RequireInternalSlot(_O_, [[DataView]]).
          1. Assert: _O_ has a [[ViewedArrayBuffer]] internal slot.
          1. Let _buffer_ be _O_.[[ViewedArrayBuffer]].
          1. If IsDetachedBuffer(_buffer_) is *true*, throw a *TypeError* exception.
          1. Let _offset_ be _O_.[[ByteOffset]].
          1. Return _offset_.
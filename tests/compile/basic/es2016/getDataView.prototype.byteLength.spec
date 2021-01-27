          1. Let _O_ be the *this* value.
          1. If Type(_O_) is not Object, throw a *TypeError* exception.
          1. If _O_ does not have a [[ViewedArrayBuffer]] internal slot, throw a *TypeError* exception.
          1. Let _buffer_ be the value of _O_'s [[ViewedArrayBuffer]] internal slot.
          1. If IsDetachedBuffer(_buffer_) is *true*, throw a *TypeError* exception.
          1. Let _size_ be the value of _O_'s [[ByteLength]] internal slot.
          1. Return _size_.
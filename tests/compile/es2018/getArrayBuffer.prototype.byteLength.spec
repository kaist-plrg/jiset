          1. Let _O_ be the *this* value.
          1. If Type(_O_) is not Object, throw a *TypeError* exception.
          1. If _O_ does not have an [[ArrayBufferData]] internal slot, throw a *TypeError* exception.
          1. If IsSharedArrayBuffer(_O_) is *true*, throw a *TypeError* exception.
          1. If IsDetachedBuffer(_O_) is *true*, throw a *TypeError* exception.
          1. Let _length_ be _O_.[[ArrayBufferByteLength]].
          1. Return _length_.
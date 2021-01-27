          1. If _onlyInt32_ is not present, set _onlyInt32_ to *false*.
          1. If Type(_typedArray_) is not Object, throw a *TypeError* exception.
          1. If _typedArray_ does not have a [[TypedArrayName]] internal slot, throw a *TypeError* exception.
          1. Let _typeName_ be _typedArray_.[[TypedArrayName]].
          1. If _onlyInt32_ is *true*, then
            1. If _typeName_ is not `"Int32Array"`, throw a *TypeError* exception.
          1. Else,
            1. If _typeName_ is not `"Int8Array"`, `"Uint8Array"`, `"Int16Array"`, `"Uint16Array"`, `"Int32Array"`, or `"Uint32Array"`, throw a *TypeError* exception.
          1. Assert: _typedArray_ has a [[ViewedArrayBuffer]] internal slot.
          1. Let _buffer_ be _typedArray_.[[ViewedArrayBuffer]].
          1. If IsSharedArrayBuffer(_buffer_) is *false*, throw a *TypeError* exception.
          1. Return _buffer_.
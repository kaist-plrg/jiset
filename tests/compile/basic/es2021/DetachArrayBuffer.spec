          1. Assert: Type(_arrayBuffer_) is Object and it has [[ArrayBufferData]], [[ArrayBufferByteLength]], and [[ArrayBufferDetachKey]] internal slots.
          1. Assert: IsSharedArrayBuffer(_arrayBuffer_) is *false*.
          1. If _key_ is not present, set _key_ to *undefined*.
          1. If SameValue(_arrayBuffer_.[[ArrayBufferDetachKey]], _key_) is *false*, throw a *TypeError* exception.
          1. Set _arrayBuffer_.[[ArrayBufferData]] to *null*.
          1. Set _arrayBuffer_.[[ArrayBufferByteLength]] to 0.
          1. Return NormalCompletion(*null*).
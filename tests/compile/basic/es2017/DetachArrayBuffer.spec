          1. Assert: Type(_arrayBuffer_) is Object and it has [[ArrayBufferData]] and [[ArrayBufferByteLength]] internal slots.
          1. Assert: IsSharedArrayBuffer(_arrayBuffer_) is *false*.
          1. Set _arrayBuffer_.[[ArrayBufferData]] to *null*.
          1. Set _arrayBuffer_.[[ArrayBufferByteLength]] to 0.
          1. Return NormalCompletion(*null*).
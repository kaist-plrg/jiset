          1. Let _obj_ be ? OrdinaryCreateFromConstructor(_constructor_, *"%ArrayBuffer.prototype%"*, « [[ArrayBufferData]], [[ArrayBufferByteLength]], [[ArrayBufferDetachKey]] »).
          1. Assert: ! IsNonNegativeInteger(_byteLength_) is *true*.
          1. Let _block_ be ? CreateByteDataBlock(_byteLength_).
          1. Set _obj_.[[ArrayBufferData]] to _block_.
          1. Set _obj_.[[ArrayBufferByteLength]] to _byteLength_.
          1. Return _obj_.
          1. Let _obj_ be ? OrdinaryCreateFromConstructor(_constructor_, *"%SharedArrayBuffer.prototype%"*, « [[ArrayBufferData]], [[ArrayBufferByteLength]] »).
          1. Let _block_ be ? CreateSharedByteDataBlock(_byteLength_).
          1. Set _obj_.[[ArrayBufferData]] to _block_.
          1. Set _obj_.[[ArrayBufferByteLength]] to _byteLength_.
          1. Return _obj_.
          1. Let _obj_ be ? OrdinaryCreateFromConstructor(_constructor_, `"%ArrayBufferPrototype%"`, « [[ArrayBufferData]], [[ArrayBufferByteLength]] »).
          1. Assert: _byteLength_ is an integer value ≥ 0.
          1. Let _block_ be ? CreateByteDataBlock(_byteLength_).
          1. Set _obj_'s [[ArrayBufferData]] internal slot to _block_.
          1. Set _obj_'s [[ArrayBufferByteLength]] internal slot to _byteLength_.
          1. Return _obj_.
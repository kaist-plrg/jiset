            1. Let _proto_ be ? GetPrototypeFromConstructor(_newTarget_, _defaultProto_).
            1. Let _obj_ be IntegerIndexedObjectCreate(_proto_, « [[ViewedArrayBuffer]], [[TypedArrayName]], [[ByteLength]], [[ByteOffset]], [[ArrayLength]] »).
            1. Assert: The [[ViewedArrayBuffer]] internal slot of _obj_ is *undefined*.
            1. Set _obj_'s [[TypedArrayName]] internal slot to _constructorName_.
            1. If _length_ was not passed, then
              1. Set _obj_'s [[ByteLength]] internal slot to 0.
              1. Set _obj_'s [[ByteOffset]] internal slot to 0.
              1. Set _obj_'s [[ArrayLength]] internal slot to 0.
            1. Else,
              1. Perform ? AllocateTypedArrayBuffer(_obj_, _length_).
            1. Return _obj_.
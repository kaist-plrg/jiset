            1. Let _proto_ be ? GetPrototypeFromConstructor(_newTarget_, _defaultProto_).
            1. Let _obj_ be ! IntegerIndexedObjectCreate(_proto_).
            1. Assert: _obj_.[[ViewedArrayBuffer]] is *undefined*.
            1. Set _obj_.[[TypedArrayName]] to _constructorName_.
            1. If _constructorName_ is *"BigInt64Array"* or *"BigUint64Array"*, set _obj_.[[ContentType]] to ~BigInt~.
            1. Otherwise, set _obj_.[[ContentType]] to ~Number~.
            1. If _length_ is not present, then
              1. Set _obj_.[[ByteLength]] to 0.
              1. Set _obj_.[[ByteOffset]] to 0.
              1. Set _obj_.[[ArrayLength]] to 0.
            1. Else,
              1. Perform ? AllocateTypedArrayBuffer(_obj_, _length_).
            1. Return _obj_.
        1. Let _or_ be a new read-modify-write modification function with parameters (_xBytes_, _yBytes_) that captures nothing and performs the following steps atomically when called:
          1. Return ByteListBitwiseOp(`|`, _xBytes_, _yBytes_).
        1. Return ? AtomicReadModifyWrite(_typedArray_, _index_, _value_, _or_).
        1. Let _second_ be a new read-modify-write modification function with parameters (_oldBytes_, _newBytes_) that captures nothing and performs the following steps atomically when called:
          1. Return _newBytes_.
        1. Return ? AtomicReadModifyWrite(_typedArray_, _index_, _value_, _second_).
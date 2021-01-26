        1. Let _key_ be ? ToPrimitive(_argument_, hint String).
        1. If Type(_key_) is Symbol, then
          1. Return _key_.
        1. Return ! ToString(_key_).
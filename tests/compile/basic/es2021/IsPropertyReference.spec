          1. Assert: _V_ is a Reference Record.
          1. If _V_.[[Base]] is ~unresolvable~, return *false*.
          1. If Type(_V_.[[Base]]) is Boolean, String, Symbol, BigInt, Number, or Object, return *true*; otherwise return *false*.
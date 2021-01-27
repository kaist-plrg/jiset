        1. Assert: 0 ≤ _cp_ ≤ 0x10FFFF.
        1. If _cp_ ≤ 65535, return _cp_.
        1. Let _cu1_ be floor((_cp_ - 65536) / 1024) + 0xD800.
        1. Let _cu2_ be ((_cp_ - 65536) modulo 1024) + 0xDC00.
        1. Return the code unit sequence consisting of _cu1_ followed by _cu2_.
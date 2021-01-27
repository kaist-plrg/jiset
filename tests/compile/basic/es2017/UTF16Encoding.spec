        1. Assert: 0 ≤ _cp_ ≤ 0x10FFFF.
        1. If _cp_ ≤ 0xFFFF, return _cp_.
        1. Let _cu1_ be floor((_cp_ - 0x10000) / 0x400) + 0xD800.
        1. Let _cu2_ be ((_cp_ - 0x10000) modulo 0x400) + 0xDC00.
        1. Return the code unit sequence consisting of _cu1_ followed by _cu2_.
        1. Assert: 0 ≤ _cp_ ≤ 0x10FFFF.
        1. If _cp_ ≤ 0xFFFF, return the String value consisting of the code unit whose value is _cp_.
        1. Let _cu1_ be the code unit whose value is floor((_cp_ - 0x10000) / 0x400) + 0xD800.
        1. Let _cu2_ be the code unit whose value is ((_cp_ - 0x10000) modulo 0x400) + 0xDC00.
        1. Return the string-concatenation of _cu1_ and _cu2_.
        1. Assert: 0xD800 ≤ _lead_ ≤ 0xDBFF and 0xDC00 ≤ _trail_ ≤ 0xDFFF.
        1. Let _cp_ be (_lead_ - 0xD800) × 1024 + (_trail_ - 0xDC00) + 0x10000.
        1. Return the code point _cp_.
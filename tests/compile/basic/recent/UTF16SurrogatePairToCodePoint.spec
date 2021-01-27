        1. Assert: _lead_ is a <emu-xref href="#leading-surrogate"></emu-xref> and _trail_ is a <emu-xref href="#trailing-surrogate"></emu-xref>.
        1. Let _cp_ be (_lead_ - 0xD800) × 0x400 + (_trail_ - 0xDC00) + 0x10000.
        1. Return the code point _cp_.
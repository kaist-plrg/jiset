          1. Let _elementSize_ be the Number value of the Element Size value specified in <emu-xref href="#table-49"></emu-xref> for Element Type _type_.
          1. If _isLittleEndian_ is *false*, reverse the order of the elements of _rawBytes_.
          1. If _type_ is `"Float32"`, then
            1. Let _value_ be the byte elements of _rawBytes_ concatenated and interpreted as a little-endian bit string encoding of an IEEE 754-2008 binary32 value.
            1. If _value_ is an IEEE 754-2008 binary32 NaN value, return the *NaN* Number value.
            1. Return the Number value that corresponds to _value_.
          1. If _type_ is `"Float64"`, then
            1. Let _value_ be the byte elements of _rawBytes_ concatenated and interpreted as a little-endian bit string encoding of an IEEE 754-2008 binary64 value.
            1. If _value_ is an IEEE 754-2008 binary64 NaN value, return the *NaN* Number value.
            1. Return the Number value that corresponds to _value_.
          1. If the first code unit of _type_ is the code unit 0x0055 (LATIN CAPITAL LETTER U), then
            1. Let _intValue_ be the byte elements of _rawBytes_ concatenated and interpreted as a bit string encoding of an unsigned little-endian binary number.
          1. Else,
            1. Let _intValue_ be the byte elements of _rawBytes_ concatenated and interpreted as a bit string encoding of a binary little-endian 2's complement number of bit length _elementSize_ × 8.
          1. Return the Number value that corresponds to _intValue_.
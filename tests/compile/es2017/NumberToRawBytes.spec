          1. If _type_ is `"Float32"`, then
            1. Let _rawBytes_ be a List containing the 4 bytes that are the result of converting _value_ to IEEE 754-2008 binary32 format using “Round to nearest, ties to even” rounding mode. If _isLittleEndian_ is *false*, the bytes are arranged in big endian order. Otherwise, the bytes are arranged in little endian order. If _value_ is *NaN*, _rawBytes_ may be set to any implementation chosen IEEE 754-2008 binary32 format Not-a-Number encoding. An implementation must always choose the same encoding for each implementation distinguishable *NaN* value.
          1. Else if _type_ is `"Float64"`, then
            1. Let _rawBytes_ be a List containing the 8 bytes that are the IEEE 754-2008 binary64 format encoding of _value_. If _isLittleEndian_ is *false*, the bytes are arranged in big endian order. Otherwise, the bytes are arranged in little endian order. If _value_ is *NaN*, _rawBytes_ may be set to any implementation chosen IEEE 754-2008 binary64 format Not-a-Number encoding. An implementation must always choose the same encoding for each implementation distinguishable *NaN* value.
          1. Else,
            1. Let _n_ be the Number value of the Element Size specified in <emu-xref href="#table-49"></emu-xref> for Element Type _type_.
            1. Let _convOp_ be the abstract operation named in the Conversion Operation column in <emu-xref href="#table-49"></emu-xref> for Element Type _type_.
            1. Let _intValue_ be _convOp_(_value_).
            1. If _intValue_ ≥ 0, then
              1. Let _rawBytes_ be a List containing the _n_-byte binary encoding of _intValue_. If _isLittleEndian_ is *false*, the bytes are ordered in big endian order. Otherwise, the bytes are ordered in little endian order.
            1. Else,
              1. Let _rawBytes_ be a List containing the _n_-byte binary 2's complement encoding of _intValue_. If _isLittleEndian_ is *false*, the bytes are ordered in big endian order. Otherwise, the bytes are ordered in little endian order.
          1. Return _rawBytes_.
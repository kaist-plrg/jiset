          1. Assert: IsDetachedBuffer(_arrayBuffer_) is *false*.
          1. Assert: There are sufficient bytes in _arrayBuffer_ starting at _byteIndex_ to represent a value of _type_.
          1. Assert: _byteIndex_ is an integer value ≥ 0.
          1. Let _block_ be _arrayBuffer_'s [[ArrayBufferData]] internal slot.
          1. Let _elementSize_ be the Number value of the Element Size value specified in <emu-xref href="#table-49"></emu-xref> for Element Type _type_.
          1. Let _rawValue_ be a List of _elementSize_ containing, in order, the _elementSize_ sequence of bytes starting with _block_[_byteIndex_].
          1. If _isLittleEndian_ is not present, set _isLittleEndian_ to either *true* or *false*. The choice is implementation dependent and should be the alternative that is most efficient for the implementation. An implementation must use the same value each time this step is executed and the same value must be used for the corresponding step in the SetValueInBuffer abstract operation.
          1. If _isLittleEndian_ is *false*, reverse the order of the elements of _rawValue_.
          1. If _type_ is `"Float32"`, then
            1. Let _value_ be the byte elements of _rawValue_ concatenated and interpreted as a little-endian bit string encoding of an IEEE 754-2008 binary32 value.
            1. If _value_ is an IEEE 754-2008 binary32 NaN value, return the *NaN* Number value.
            1. Return the Number value that corresponds to _value_.
          1. If _type_ is `"Float64"`, then
            1. Let _value_ be the byte elements of _rawValue_ concatenated and interpreted as a little-endian bit string encoding of an IEEE 754-2008 binary64 value.
            1. If _value_ is an IEEE 754-2008 binary64 NaN value, return the *NaN* Number value.
            1. Return the Number value that corresponds to _value_.
          1. If the first code unit of _type_ is `"U"`, then
            1. Let _intValue_ be the byte elements of _rawValue_ concatenated and interpreted as a bit string encoding of an unsigned little-endian binary number.
          1. Else,
            1. Let _intValue_ be the byte elements of _rawValue_ concatenated and interpreted as a bit string encoding of a binary little-endian 2's complement number of bit length _elementSize_ × 8.
          1. Return the Number value that corresponds to _intValue_.
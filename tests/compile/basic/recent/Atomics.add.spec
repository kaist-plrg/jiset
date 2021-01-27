        1. Let _type_ be the Element Type value in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _typedArray_.[[TypedArrayName]].
        1. Let _isLittleEndian_ be the value of the [[LittleEndian]] field of the surrounding agent's Agent Record.
        1. Let _add_ be a new read-modify-write modification function with parameters (_xBytes_, _yBytes_) that captures _type_ and _isLittleEndian_ and performs the following steps atomically when called:
          1. Let _x_ be RawBytesToNumeric(_type_, _xBytes_, _isLittleEndian_).
          1. Let _y_ be RawBytesToNumeric(_type_, _yBytes_, _isLittleEndian_).
          1. Let _T_ be Type(_x_).
          1. Let _sum_ be _T_::add(_x_, _y_).
          1. Let _sumBytes_ be NumericToRawBytes(_type_, _sum_, _isLittleEndian_).
          1. Assert: _sumBytes_, _xBytes_, and _yBytes_ have the same number of elements.
          1. Return _sumBytes_.
        1. Return ? AtomicReadModifyWrite(_typedArray_, _index_, _value_, _add_).
          1. If NewTarget is *undefined*, throw a *TypeError* exception.
          1. Let _numberLength_ be ? ToNumber(_length_).
          1. Let _byteLength_ be ToLength(_numberLength_).
          1. If SameValueZero(_numberLength_, _byteLength_) is *false*, throw a *RangeError* exception.
          1. Return ? AllocateArrayBuffer(NewTarget, _byteLength_).
          1. If NewTarget is not *undefined*, throw a *TypeError* exception.
          1. Let _prim_ be ? ToPrimitive(_value_, hint Number).
          1. If Type(_prim_) is Number, return ? NumberToBigInt(_prim_).
          1. Otherwise, return ? ToBigInt(_value_).
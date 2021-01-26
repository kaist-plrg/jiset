        1. Let _primValue_ be ? ToPrimitive(_value_, hint Number).
        1. If Type(_primValue_) is BigInt, return _primValue_.
        1. Return ? ToNumber(_primValue_).
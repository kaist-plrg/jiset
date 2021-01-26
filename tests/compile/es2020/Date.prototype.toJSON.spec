          1. Let _O_ be ? ToObject(*this* value).
          1. Let _tv_ be ? ToPrimitive(_O_, hint Number).
          1. If Type(_tv_) is Number and _tv_ is not finite, return *null*.
          1. Return ? Invoke(_O_, *"toISOString"*).
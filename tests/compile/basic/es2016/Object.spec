          1. If NewTarget is neither *undefined* nor the active function, then
            1. Return ? OrdinaryCreateFromConstructor(NewTarget, `"%ObjectPrototype%"`).
          1. If _value_ is *null*, *undefined* or not supplied, return ObjectCreate(%ObjectPrototype%).
          1. Return ToObject(_value_).
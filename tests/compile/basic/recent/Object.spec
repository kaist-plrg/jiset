          1. If NewTarget is neither *undefined* nor the active function, then
            1. Return ? OrdinaryCreateFromConstructor(NewTarget, *"%Object.prototype%"*).
          1. If _value_ is *undefined* or *null*, return ! OrdinaryObjectCreate(%Object.prototype%).
          1. Return ! ToObject(_value_).
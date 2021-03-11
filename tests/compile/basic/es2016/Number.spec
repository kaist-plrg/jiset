          1. If no arguments were passed to this function invocation, let _n_ be *+0*.
          1. Else, let _n_ be ? ToNumber(_value_).
          1. If NewTarget is *undefined*, return _n_.
          1. Let _O_ be ? OrdinaryCreateFromConstructor(NewTarget, `"%NumberPrototype%"`, « [[NumberData]] »).
          1. Set the value of _O_'s [[NumberData]] internal slot to _n_.
          1. Return _O_.
          1. If _value_ is present, then
            1. Let _prim_ be ? ToNumeric(_value_).
            1. If Type(_prim_) is BigInt, let _n_ be the Number value for the mathematical value of _prim_.
            1. Otherwise, let _n_ be _prim_.
          1. Else,
            1. Let _n_ be *+0*.
          1. If NewTarget is *undefined*, return _n_.
          1. Let _O_ be ? OrdinaryCreateFromConstructor(NewTarget, *"%Number.prototype%"*, « [[NumberData]] »).
          1. Set _O_.[[NumberData]] to _n_.
          1. Return _O_.
          1. Let _b_ be ! ToBoolean(_value_).
          1. If NewTarget is *undefined*, return _b_.
          1. Let _O_ be ? OrdinaryCreateFromConstructor(NewTarget, *"%Boolean.prototype%"*, « [[BooleanData]] »).
          1. Set _O_.[[BooleanData]] to _b_.
          1. Return _O_.
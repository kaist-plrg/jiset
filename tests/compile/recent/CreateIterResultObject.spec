        1. Assert: Type(_done_) is Boolean.
        1. Let _obj_ be ! OrdinaryObjectCreate(%Object.prototype%).
        1. Perform ! CreateDataPropertyOrThrow(_obj_, *"value"*, _value_).
        1. Perform ! CreateDataPropertyOrThrow(_obj_, *"done"*, _done_).
        1. Return _obj_.
        1. Assert: Type(_done_) is Boolean.
        1. Let _obj_ be ObjectCreate(%ObjectPrototype%).
        1. Perform CreateDataProperty(_obj_, `"value"`, _value_).
        1. Perform CreateDataProperty(_obj_, `"done"`, _done_).
        1. Return _obj_.
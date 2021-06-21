            1. Let _O_ be the *this* value.
            1. Assert: Type(_O_) is Object.
            1. Assert: _O_ is an extensible ordinary object.
            1. Let _propertyKey_ be ? ToPropertyKey(_key_).
            1. Perform ! CreateDataPropertyOrThrow(_O_, _propertyKey_, _value_).
            1. Return *undefined*.
        1. Let _propertyNameReference_ be the result of evaluating _expression_.
        1. Let _propertyNameValue_ be ? GetValue(_propertyNameReference_).
        1. Let _bv_ be ? RequireObjectCoercible(_baseValue_).
        1. Let _propertyKey_ be ? ToPropertyKey(_propertyNameValue_).
        1. Return the Reference Record { [[Base]]: _bv_, [[ReferencedName]]: _propertyKey_, [[Strict]]: _strict_, [[ThisValue]]: ~empty~ }.
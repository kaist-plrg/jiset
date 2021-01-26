        1. Let _propertyNameReference_ be the result of evaluating _expression_.
        1. Let _propertyNameValue_ be ? GetValue(_propertyNameReference_).
        1. Let _bv_ be ? RequireObjectCoercible(_baseValue_).
        1. Let _propertyKey_ be ? ToPropertyKey(_propertyNameValue_).
        1. Return a value of type Reference whose base value component is _bv_, whose referenced name component is _propertyKey_, and whose strict reference flag is _strict_.
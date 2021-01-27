        1. Assert: _identifierName_ is an |IdentifierName|.
        1. Let _bv_ be ? RequireObjectCoercible(_baseValue_).
        1. Let _propertyNameString_ be StringValue of _identifierName_.
        1. Return a value of type Reference whose base value component is _bv_, whose referenced name component is _propertyNameString_, and whose strict reference flag is _strict_.
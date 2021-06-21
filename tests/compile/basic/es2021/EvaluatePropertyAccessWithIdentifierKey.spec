        1. Assert: _identifierName_ is an |IdentifierName|.
        1. Let _bv_ be ? RequireObjectCoercible(_baseValue_).
        1. Let _propertyNameString_ be StringValue of _identifierName_.
        1. Return the Reference Record { [[Base]]: _bv_, [[ReferencedName]]: _propertyNameString_, [[Strict]]: _strict_, [[ThisValue]]: ~empty~ }.
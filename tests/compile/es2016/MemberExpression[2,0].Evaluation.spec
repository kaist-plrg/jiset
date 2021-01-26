          1. Let _baseReference_ be the result of evaluating |MemberExpression|.
          1. Let _baseValue_ be ? GetValue(_baseReference_).
          1. Let _bv_ be ? RequireObjectCoercible(_baseValue_).
          1. Let _propertyNameString_ be StringValue of |IdentifierName|.
          1. If the code matched by the syntactic production that is being evaluated is strict mode code, let _strict_ be *true*, else let _strict_ be *false*.
          1. Return a value of type Reference whose base value is _bv_, whose referenced name is _propertyNameString_, and whose strict reference flag is _strict_.
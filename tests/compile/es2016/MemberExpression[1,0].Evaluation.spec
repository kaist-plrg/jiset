          1. Let _baseReference_ be the result of evaluating |MemberExpression|.
          1. Let _baseValue_ be ? GetValue(_baseReference_).
          1. Let _propertyNameReference_ be the result of evaluating |Expression|.
          1. Let _propertyNameValue_ be ? GetValue(_propertyNameReference_).
          1. Let _bv_ be ? RequireObjectCoercible(_baseValue_).
          1. Let _propertyKey_ be ? ToPropertyKey(_propertyNameValue_).
          1. If the code matched by the syntactic production that is being evaluated is strict mode code, let _strict_ be *true*, else let _strict_ be *false*.
          1. Return a value of type Reference whose base value is _bv_, whose referenced name is _propertyKey_, and whose strict reference flag is _strict_.
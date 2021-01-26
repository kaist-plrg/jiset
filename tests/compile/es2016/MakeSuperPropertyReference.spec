          1. Let _env_ be GetThisEnvironment( ).
          1. If _env_.HasSuperBinding() is *false*, throw a *ReferenceError* exception.
          1. Let _actualThis_ be ? _env_.GetThisBinding().
          1. Let _baseValue_ be ? _env_.GetSuperBase().
          1. Let _bv_ be ? RequireObjectCoercible(_baseValue_).
          1. Return a value of type Reference that is a Super Reference whose base value is _bv_, whose referenced name is _propertyKey_, whose thisValue is _actualThis_, and whose strict reference flag is _strict_.
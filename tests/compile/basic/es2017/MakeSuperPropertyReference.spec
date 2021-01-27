          1. Let _env_ be GetThisEnvironment( ).
          1. Assert: _env_.HasSuperBinding() is *true*.
          1. Let _actualThis_ be ? _env_.GetThisBinding().
          1. Let _baseValue_ be ? _env_.GetSuperBase().
          1. Let _bv_ be ? RequireObjectCoercible(_baseValue_).
          1. Return a value of type Reference that is a Super Reference whose base value component is _bv_, whose referenced name component is _propertyKey_, whose thisValue component is _actualThis_, and whose strict reference flag is _strict_.
          1. Let _env_ be GetThisEnvironment().
          1. Assert: _env_.HasSuperBinding() is *true*.
          1. Let _baseValue_ be ? _env_.GetSuperBase().
          1. Let _bv_ be ? RequireObjectCoercible(_baseValue_).
          1. Return the Reference Record { [[Base]]: _bv_, [[ReferencedName]]: _propertyKey_, [[Strict]]: _strict_, [[ThisValue]]: _actualThis_ }.
          1. NOTE: This returns a Super Reference Record.
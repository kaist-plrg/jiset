          1. Let _env_ be GetThisEnvironment().
          1. Let _actualThis_ be ? _env_.GetThisBinding().
          1. Let _propertyNameReference_ be the result of evaluating |Expression|.
          1. Let _propertyNameValue_ be ? GetValue(_propertyNameReference_).
          1. Let _propertyKey_ be ? ToPropertyKey(_propertyNameValue_).
          1. If the code matched by this |SuperProperty| is strict mode code, let _strict_ be *true*, else let _strict_ be *false*.
          1. Return ? MakeSuperPropertyReference(_actualThis_, _propertyKey_, _strict_).
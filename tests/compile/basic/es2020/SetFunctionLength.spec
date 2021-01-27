        1. Assert: _F_ is an extensible object that does not have a *"length"* own property.
        1. Assert: Type(_length_) is Number.
        1. Assert: ! IsNonNegativeInteger(_length_) is *true*.
        1. Return ! DefinePropertyOrThrow(_F_, *"length"*, PropertyDescriptor { [[Value]]: _length_, [[Writable]]: *false*, [[Enumerable]]: *false*, [[Configurable]]: *true* }).
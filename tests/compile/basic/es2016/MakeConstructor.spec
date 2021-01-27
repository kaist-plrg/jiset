        1. Assert: _F_ is an ECMAScript function object.
        1. Assert: _F_ has a [[Construct]] internal method.
        1. Assert: _F_ is an extensible object that does not have a `prototype` own property.
        1. If the _writablePrototype_ argument was not provided, let _writablePrototype_ be *true*.
        1. If the _prototype_ argument was not provided, then
          1. Let _prototype_ be ObjectCreate(%ObjectPrototype%).
          1. Perform ! DefinePropertyOrThrow(_prototype_, `"constructor"`, PropertyDescriptor{[[Value]]: _F_, [[Writable]]: _writablePrototype_, [[Enumerable]]: *false*, [[Configurable]]: *true* }).
        1. Perform ! DefinePropertyOrThrow(_F_, `"prototype"`, PropertyDescriptor{[[Value]]: _prototype_, [[Writable]]: _writablePrototype_, [[Enumerable]]: *false*, [[Configurable]]: *false*}).
        1. Return NormalCompletion(*undefined*).
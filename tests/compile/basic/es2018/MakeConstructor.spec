        1. Assert: _F_ is an ECMAScript function object.
        1. Assert: IsConstructor(_F_) is *true*.
        1. Assert: _F_ is an extensible object that does not have a `prototype` own property.
        1. If _writablePrototype_ is not present, set _writablePrototype_ to *true*.
        1. If _prototype_ is not present, then
          1. Set _prototype_ to ObjectCreate(%ObjectPrototype%).
          1. Perform ! DefinePropertyOrThrow(_prototype_, `"constructor"`, PropertyDescriptor { [[Value]]: _F_, [[Writable]]: _writablePrototype_, [[Enumerable]]: *false*, [[Configurable]]: *true* }).
        1. Perform ! DefinePropertyOrThrow(_F_, `"prototype"`, PropertyDescriptor { [[Value]]: _prototype_, [[Writable]]: _writablePrototype_, [[Enumerable]]: *false*, [[Configurable]]: *false* }).
        1. Return NormalCompletion(*undefined*).
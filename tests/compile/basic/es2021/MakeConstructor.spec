        1. Assert: _F_ is an ECMAScript function object or a built-in function object.
        1. If _F_ is an ECMAScript function object, then
          1. Assert: IsConstructor(_F_) is *false*.
          1. Assert: _F_ is an extensible object that does not have a *"prototype"* own property.
          1. Set _F_.[[Construct]] to the definition specified in <emu-xref href="#sec-ecmascript-function-objects-construct-argumentslist-newtarget"></emu-xref>.
        1. Set _F_.[[ConstructorKind]] to ~base~.
        1. If _writablePrototype_ is not present, set _writablePrototype_ to *true*.
        1. If _prototype_ is not present, then
          1. Set _prototype_ to ! OrdinaryObjectCreate(%Object.prototype%).
          1. Perform ! DefinePropertyOrThrow(_prototype_, *"constructor"*, PropertyDescriptor { [[Value]]: _F_, [[Writable]]: _writablePrototype_, [[Enumerable]]: *false*, [[Configurable]]: *true* }).
        1. Perform ! DefinePropertyOrThrow(_F_, *"prototype"*, PropertyDescriptor { [[Value]]: _prototype_, [[Writable]]: _writablePrototype_, [[Enumerable]]: *false*, [[Configurable]]: *false* }).
        1. Return NormalCompletion(*undefined*).
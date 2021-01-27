        1. Assert: _realm_.[[Intrinsics]].[[%ThrowTypeError%]] exists and has been initialized.
        1. Let _thrower_ be _realm_.[[Intrinsics]].[[%ThrowTypeError%]].
        1. Perform ! DefinePropertyOrThrow(_F_, *"caller"*, PropertyDescriptor { [[Get]]: _thrower_, [[Set]]: _thrower_, [[Enumerable]]: *false*, [[Configurable]]: *true* }).
        1. Return ! DefinePropertyOrThrow(_F_, *"arguments"*, PropertyDescriptor { [[Get]]: _thrower_, [[Set]]: _thrower_, [[Enumerable]]: *false*, [[Configurable]]: *true* }).
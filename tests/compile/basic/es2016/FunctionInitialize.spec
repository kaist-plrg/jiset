        1. Assert: _F_ is an extensible object that does not have a `length` own property.
        1. Let _len_ be the ExpectedArgumentCount of _ParameterList_.
        1. Perform ! DefinePropertyOrThrow(_F_, `"length"`, PropertyDescriptor{[[Value]]: _len_, [[Writable]]: *false*, [[Enumerable]]: *false*, [[Configurable]]: *true*}).
        1. Let _Strict_ be the value of the [[Strict]] internal slot of _F_.
        1. Set the [[Environment]] internal slot of _F_ to the value of _Scope_.
        1. Set the [[FormalParameters]] internal slot of _F_ to _ParameterList_.
        1. Set the [[ECMAScriptCode]] internal slot of _F_ to _Body_.
        1. Set the [[ScriptOrModule]] internal slot of _F_ to GetActiveScriptOrModule().
        1. If _kind_ is ~Arrow~, set the [[ThisMode]] internal slot of _F_ to ~lexical~.
        1. Else if _Strict_ is *true*, set the [[ThisMode]] internal slot of _F_ to ~strict~.
        1. Else set the [[ThisMode]] internal slot of _F_ to ~global~.
        1. Return _F_.
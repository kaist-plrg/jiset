        1. Assert: _F_ is an extensible object that does not have a `length` own property.
        1. Let _len_ be the ExpectedArgumentCount of _ParameterList_.
        1. Perform ! DefinePropertyOrThrow(_F_, `"length"`, PropertyDescriptor{[[Value]]: _len_, [[Writable]]: *false*, [[Enumerable]]: *false*, [[Configurable]]: *true*}).
        1. Let _Strict_ be _F_.[[Strict]].
        1. Set _F_.[[Environment]] to _Scope_.
        1. Set _F_.[[FormalParameters]] to _ParameterList_.
        1. Set _F_.[[ECMAScriptCode]] to _Body_.
        1. Set _F_.[[ScriptOrModule]] to GetActiveScriptOrModule().
        1. If _kind_ is ~Arrow~, set _F_.[[ThisMode]] to ~lexical~.
        1. Else if _Strict_ is *true*, set _F_.[[ThisMode]] to ~strict~.
        1. Else, set _F_.[[ThisMode]] to ~global~.
        1. Return _F_.
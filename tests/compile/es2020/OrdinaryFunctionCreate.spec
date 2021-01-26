        1. Assert: Type(_functionPrototype_) is Object.
        1. Let _internalSlotsList_ be the internal slots listed in <emu-xref href="#table-27"></emu-xref>.
        1. Let _F_ be ! OrdinaryObjectCreate(_functionPrototype_, _internalSlotsList_).
        1. Set _F_.[[Call]] to the definition specified in <emu-xref href="#sec-ecmascript-function-objects-call-thisargument-argumentslist"></emu-xref>.
        1. Set _F_.[[FormalParameters]] to _ParameterList_.
        1. Set _F_.[[ECMAScriptCode]] to _Body_.
        1. If the source text matching _Body_ is strict mode code, let _Strict_ be *true*; else let _Strict_ be *false*.
        1. Set _F_.[[Strict]] to _Strict_.
        1. If _thisMode_ is ~lexical-this~, set _F_.[[ThisMode]] to ~lexical~.
        1. Else if _Strict_ is *true*, set _F_.[[ThisMode]] to ~strict~.
        1. Else, set _F_.[[ThisMode]] to ~global~.
        1. Set _F_.[[IsClassConstructor]] to *false*.
        1. Set _F_.[[Environment]] to _Scope_.
        1. Set _F_.[[ScriptOrModule]] to GetActiveScriptOrModule().
        1. Set _F_.[[Realm]] to the current Realm Record.
        1. Set _F_.[[HomeObject]] to *undefined*.
        1. Let _len_ be the ExpectedArgumentCount of _ParameterList_.
        1. Perform ! SetFunctionLength(_F_, _len_).
        1. Return _F_.
        1. Let _len_ be the ExpectedArgumentCount of _ParameterList_.
        1. Perform ! SetFunctionLength(_F_, _len_).
        1. Let _Strict_ be _F_.[[Strict]].
        1. Set _F_.[[Environment]] to _Scope_.
        1. Set _F_.[[FormalParameters]] to _ParameterList_.
        1. Set _F_.[[ECMAScriptCode]] to _Body_.
        1. Set _F_.[[ScriptOrModule]] to GetActiveScriptOrModule().
        1. If _kind_ is ~Arrow~, set _F_.[[ThisMode]] to ~lexical~.
        1. Else if _Strict_ is *true*, set _F_.[[ThisMode]] to ~strict~.
        1. Else, set _F_.[[ThisMode]] to ~global~.
        1. Return _F_.
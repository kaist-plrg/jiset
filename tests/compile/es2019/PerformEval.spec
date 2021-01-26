          1. Assert: If _direct_ is *false*, then _strictCaller_ is also *false*.
          1. If Type(_x_) is not String, return _x_.
          1. Let _thisEnvRec_ be ! GetThisEnvironment().
          1. If _thisEnvRec_ is a function Environment Record, then
            1. Let _F_ be _thisEnvRec_.[[FunctionObject]].
            1. Let _inFunction_ be *true*.
            1. Let _inMethod_ be _thisEnvRec_.HasSuperBinding().
            1. If _F_.[[ConstructorKind]] is `"derived"`, let _inDerivedConstructor_ be *true*; otherwise, let _inDerivedConstructor_ be *false*.
          1. Else,
            1. Let _inFunction_ be *false*.
            1. Let _inMethod_ be *false*.
            1. Let _inDerivedConstructor_ be *false*.
          1. Let _script_ be the ECMAScript code that is the result of parsing _x_, interpreted as UTF-16 encoded Unicode text as described in <emu-xref href="#sec-ecmascript-language-types-string-type"></emu-xref>, for the goal symbol |Script|. If _inFunction_ is *false*, additional early error rules from <emu-xref href="#sec-performeval-rules-outside-functions"></emu-xref> are applied. If _inMethod_ is *false*, additional early error rules from <emu-xref href="#sec-performeval-rules-outside-methods"></emu-xref> are applied. If _inDerivedConstructor_ is *false*, additional early error rules from <emu-xref href="#sec-performeval-rules-outside-constructors"></emu-xref> are applied. If the parse fails, throw a *SyntaxError* exception. If any early errors are detected, throw a *SyntaxError* or a *ReferenceError* exception, depending on the type of the error (but see also clause <emu-xref href="#sec-error-handling-and-language-extensions"></emu-xref>). Parsing and early error detection may be interweaved in an implementation-dependent manner.
          1. If _script_ Contains |ScriptBody| is *false*, return *undefined*.
          1. Let _body_ be the |ScriptBody| of _script_.
          1. If _strictCaller_ is *true*, let _strictEval_ be *true*.
          1. Else, let _strictEval_ be IsStrict of _script_.
          1. Let _ctx_ be the running execution context.
          1. NOTE: If _direct_ is *true*, _ctx_ will be the execution context that performed the direct eval. If _direct_ is *false*, _ctx_ will be the execution context for the invocation of the `eval` function.
          1. If _direct_ is *true*, then
            1. Let _lexEnv_ be NewDeclarativeEnvironment(_ctx_'s LexicalEnvironment).
            1. Let _varEnv_ be _ctx_'s VariableEnvironment.
          1. Else,
            1. Let _lexEnv_ be NewDeclarativeEnvironment(_evalRealm_.[[GlobalEnv]]).
            1. Let _varEnv_ be _evalRealm_.[[GlobalEnv]].
          1. If _strictEval_ is *true*, set _varEnv_ to _lexEnv_.
          1. If _ctx_ is not already suspended, suspend _ctx_.
          1. Let _evalCxt_ be a new ECMAScript code execution context.
          1. Set the _evalCxt_'s Function to *null*.
          1. Set the _evalCxt_'s Realm to _evalRealm_.
          1. Set the _evalCxt_'s ScriptOrModule to _ctx_'s ScriptOrModule.
          1. Set the _evalCxt_'s VariableEnvironment to _varEnv_.
          1. Set the _evalCxt_'s LexicalEnvironment to _lexEnv_.
          1. Push _evalCxt_ on to the execution context stack; _evalCxt_ is now the running execution context.
          1. Let _result_ be EvalDeclarationInstantiation(_body_, _varEnv_, _lexEnv_, _strictEval_).
          1. If _result_.[[Type]] is ~normal~, then
            1. Set _result_ to the result of evaluating _body_.
          1. If _result_.[[Type]] is ~normal~ and _result_.[[Value]] is ~empty~, then
            1. Set _result_ to NormalCompletion(*undefined*).
          1. Suspend _evalCxt_ and remove it from the execution context stack.
          1. Resume the context that is now on the top of the execution context stack as the running execution context.
          1. Return Completion(_result_).
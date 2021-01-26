          1. Assert: If _direct_ is *false*, then _strictCaller_ is also *false*.
          1. If Type(_x_) is not String, return _x_.
          1. Let _script_ be the ECMAScript code that is the result of parsing _x_, interpreted as UTF-16 encoded Unicode text as described in <emu-xref href="#sec-ecmascript-language-types-string-type"></emu-xref>, for the goal symbol |Script|. If the parse fails, throw a *SyntaxError* exception. If any early errors are detected, throw a *SyntaxError* or a *ReferenceError* exception, depending on the type of the error (but see also clause <emu-xref href="#sec-error-handling-and-language-extensions"></emu-xref>). Parsing and early error detection may be interweaved in an implementation dependent manner.
          1. If _script_ Contains |ScriptBody| is *false*, return *undefined*.
          1. Let _body_ be the |ScriptBody| of _script_.
          1. If _strictCaller_ is *true*, let _strictEval_ be *true*.
          1. Else, let _strictEval_ be IsStrict of _script_.
          1. Let _ctx_ be the running execution context. If _direct_ is *true*, _ctx_ will be the execution context that performed the direct eval. If _direct_ is *false*, _ctx_ will be the execution context for the invocation of the `eval` function.
          1. If _direct_ is *true*, then
            1. Let _lexEnv_ be NewDeclarativeEnvironment(_ctx_'s LexicalEnvironment).
            1. Let _varEnv_ be _ctx_'s VariableEnvironment.
          1. Else,
            1. Let _lexEnv_ be NewDeclarativeEnvironment(_evalRealm_.[[GlobalEnv]]).
            1. Let _varEnv_ be _evalRealm_.[[GlobalEnv]].
          1. If _strictEval_ is *true*, let _varEnv_ be _lexEnv_.
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
            1. Let _result_ be the result of evaluating _body_.
          1. If _result_.[[Type]] is ~normal~ and _result_.[[Value]] is ~empty~, then
            1. Let _result_ be NormalCompletion(*undefined*).
          1. Suspend _evalCxt_ and remove it from the execution context stack.
          1. Resume the context that is now on the top of the execution context stack as the running execution context.
          1. Return Completion(_result_).
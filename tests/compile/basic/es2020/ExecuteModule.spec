            1. Let _module_ be this Source Text Module Record.
            1. Suspend the currently running execution context.
            1. Let _moduleContext_ be _module_.[[Context]].
            1. Push _moduleContext_ onto the execution context stack; _moduleContext_ is now the running execution context.
            1. Let _result_ be the result of evaluating _module_.[[ECMAScriptCode]].
            1. Suspend _moduleContext_ and remove it from the execution context stack.
            1. Resume the context that is now on the top of the execution context stack as the running execution context.
            1. Return Completion(_result_).
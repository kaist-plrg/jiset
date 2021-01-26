          1. Let _asyncContext_ be _F_.[[AsyncContext]].
          1. Let _prevContext_ be the running execution context.
          1. Suspend _prevContext_.
          1. Push _asyncContext_ onto the execution context stack; _asyncContext_ is now the running execution context.
          1. Resume the suspended evaluation of _asyncContext_ using NormalCompletion(_value_) as the result of the operation that suspended it. Let _result_ be the value returned by the resumed computation.
          1. Assert: When we reach this step, _asyncContext_ has already been removed from the execution context stack and _prevContext_ is the currently running execution context.
          1. Return Completion(_result_).
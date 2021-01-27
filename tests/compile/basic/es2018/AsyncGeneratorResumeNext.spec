          1. Assert: _generator_ is an AsyncGenerator instance.
          1. Let _state_ be _generator_.[[AsyncGeneratorState]].
          1. Assert: _state_ is not `"executing"`.
          1. If _state_ is `"awaiting-return"`, return *undefined*.
          1. Let _queue_ be _generator_.[[AsyncGeneratorQueue]].
          1. If _queue_ is an empty List, return *undefined*.
          1. Let _next_ be the value of the first element of _queue_.
          1. Assert: _next_ is an AsyncGeneratorRequest record.
          1. Let _completion_ be _next_.[[Completion]].
          1. If _completion_ is an abrupt completion, then
            1. If _state_ is `"suspendedStart"`, then
              1. Set _generator_.[[AsyncGeneratorState]] to `"completed"`.
              1. Set _state_ to `"completed"`.
            1. If _state_ is `"completed"`, then
              1. If _completion_.[[Type]] is ~return~, then
                1. Set _generator_.[[AsyncGeneratorState]] to `"awaiting-return"`.
                1. Let _promiseCapability_ be ! NewPromiseCapability(%Promise%).
                1. Perform ! Call(_promiseCapability_.[[Resolve]], *undefined*, « _completion_.[[Value]] »).
                1. Let _stepsFulfilled_ be the algorithm steps defined in <emu-xref href="#async-generator-resume-next-return-processor-fulfilled" title></emu-xref>.
                1. Let _onFulfilled_ be CreateBuiltinFunction(_stepsFulfilled_, « [[Generator]] »).
                1. Set _onFulfilled_.[[Generator]] to _generator_.
                1. Let _stepsRejected_ be the algorithm steps defined in <emu-xref href="#async-generator-resume-next-return-processor-rejected" title></emu-xref>.
                1. Let _onRejected_ be CreateBuiltinFunction(_stepsRejected_, « [[Generator]] »).
                1. Set _onRejected_.[[Generator]] to _generator_.
                1. Let _throwawayCapability_ be ! NewPromiseCapability(%Promise%).
                1. Set _throwawayCapability_.[[Promise]].[[PromiseIsHandled]] to *true*.
                1. Perform ! PerformPromiseThen(_promiseCapability_.[[Promise]], _onFulfilled_, _onRejected_, _throwawayCapability_).
                1. Return *undefined*.
              1. Else,
                1. Assert: _completion_.[[Type]] is ~throw~.
                1. Perform ! AsyncGeneratorReject(_generator_, _completion_.[[Value]]).
                1. Return *undefined*.
          1. Else if _state_ is `"completed"`, return ! AsyncGeneratorResolve(_generator_, *undefined*, *true*).
          1. Assert: _state_ is either `"suspendedStart"` or `"suspendedYield"`.
          1. Let _genContext_ be _generator_.[[AsyncGeneratorContext]].
          1. Let _callerContext_ be the running execution context.
          1. Suspend _callerContext_.
          1. Set _generator_.[[AsyncGeneratorState]] to `"executing"`.
          1. Push _genContext_ onto the execution context stack; _genContext_ is now the running execution context.
          1. Resume the suspended evaluation of _genContext_ using _completion_ as the result of the operation that suspended it. Let _result_ be the completion record returned by the resumed computation.
          1. Assert: _result_ is never an abrupt completion.
          1. Assert: When we return here, _genContext_ has already been removed from the execution context stack and _callerContext_ is the currently running execution context.
          1. Return *undefined*.
          1. Let _asyncContext_ be the running execution context.
          1. Let _promise_ be ? PromiseResolve(%Promise%, « _value_ »).
          1. Let _stepsFulfilled_ be the algorithm steps defined in <emu-xref href="#await-fulfilled" title></emu-xref>.
          1. Let _onFulfilled_ be CreateBuiltinFunction(_stepsFulfilled_, « [[AsyncContext]] »).
          1. Set _onFulfilled_.[[AsyncContext]] to _asyncContext_.
          1. Let _stepsRejected_ be the algorithm steps defined in <emu-xref href="#await-rejected" title></emu-xref>.
          1. Let _onRejected_ be CreateBuiltinFunction(_stepsRejected_, « [[AsyncContext]] »).
          1. Set _onRejected_.[[AsyncContext]] to _asyncContext_.
          1. Perform ! PerformPromiseThen(_promise_, _onFulfilled_, _onRejected_).
          1. Remove _asyncContext_ from the execution context stack and restore the execution context that is at the top of the execution context stack as the running execution context.
          1. Set the code evaluation state of _asyncContext_ such that when evaluation is resumed with a Completion _completion_, the following steps of the algorithm that invoked Await will be performed, with _completion_ available.
          1. Return.
          1. NOTE: This returns to the evaluation of the operation that had most previously resumed evaluation of _asyncContext_.
          1. Let _asyncContext_ be the running execution context.
          1. Let _promiseCapability_ be ! NewPromiseCapability(%Promise%).
          1. Perform ! Call(_promiseCapability_.[[Resolve]], *undefined*, « _promise_ »).
          1. Let _stepsFulfilled_ be the algorithm steps defined in <emu-xref href="#await-fulfilled" title></emu-xref>.
          1. Let _onFulfilled_ be CreateBuiltinFunction(_stepsFulfilled_, « [[AsyncContext]] »).
          1. Set _onFulfilled_.[[AsyncContext]] to _asyncContext_.
          1. Let _stepsRejected_ be the algorithm steps defined in <emu-xref href="#await-rejected" title></emu-xref>.
          1. Let _onRejected_ be CreateBuiltinFunction(_stepsRejected_, « [[AsyncContext]] »).
          1. Set _onRejected_.[[AsyncContext]] to _asyncContext_.
          1. Let _throwawayCapability_ be ! NewPromiseCapability(%Promise%).
          1. Set _throwawayCapability_.[[Promise]].[[PromiseIsHandled]] to *true*.
          1. Perform ! PerformPromiseThen(_promiseCapability_.[[Promise]], _onFulfilled_, _onRejected_, _throwawayCapability_).
          1. Remove _asyncContext_ from the execution context stack and restore the execution context that is at the top of the execution context stack as the running execution context.
          1. Set the code evaluation state of _asyncContext_ such that when evaluation is resumed with a Completion _completion_, the following steps of the algorithm that invoked Await will be performed, with _completion_ available.
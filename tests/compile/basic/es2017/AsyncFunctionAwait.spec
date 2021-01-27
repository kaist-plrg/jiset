          1. Let _asyncContext_ be the running execution context.
          1. Let _promiseCapability_ be ! NewPromiseCapability(%Promise%).
          1. Let _resolveResult_ be ! Call(_promiseCapability_.[[Resolve]], *undefined*, « _value_ »).
          1. Let _onFulfilled_ be a new built-in function object as defined in <emu-xref href="#sec-async-functions-abstract-operations-awaited-fulfilled" title></emu-xref>.
          1. Let _onRejected_ be a new built-in function object as defined in <emu-xref href="#sec-async-functions-abstract-operations-awaited-rejected" title></emu-xref>.
          1. Set _onFulfilled_.[[AsyncContext]] to _asyncContext_.
          1. Set _onRejected_.[[AsyncContext]] to _asyncContext_.
          1. Let _throwawayCapability_ be ! NewPromiseCapability(%Promise%).
          1. Set _throwawayCapability_.[[Promise]].[[PromiseIsHandled]] to *true*.
          1. Perform ! PerformPromiseThen(_promiseCapability_.[[Promise]], _onFulfilled_, _onRejected_, _throwawayCapability_).
          1. Remove _asyncContext_ from the execution context stack and restore the execution context that is at the top of the execution context stack as the running execution context.
          1. Set the code evaluation state of _asyncContext_ such that when evaluation is resumed with a Completion _resumptionValue_ the following steps will be performed:
            1. Return _resumptionValue_.
          1. Return.
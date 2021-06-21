          1. Assert: _generator_ is an AsyncGenerator instance.
          1. Let _queue_ be _generator_.[[AsyncGeneratorQueue]].
          1. Assert: _queue_ is not an empty List.
          1. Let _next_ be the first element of _queue_.
          1. Remove the first element from _queue_.
          1. Let _promiseCapability_ be _next_.[[Capability]].
          1. Perform ! Call(_promiseCapability_.[[Reject]], *undefined*, « _exception_ »).
          1. Perform ! AsyncGeneratorResumeNext(_generator_).
          1. Return *undefined*.
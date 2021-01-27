          1. Assert: _generator_ is an AsyncGenerator instance.
          1. Let _queue_ be _generator_.[[AsyncGeneratorQueue]].
          1. Assert: _queue_ is not an empty List.
          1. Remove the first element from _queue_ and let _next_ be the value of that element.
          1. Let _promiseCapability_ be _next_.[[Capability]].
          1. Let _iteratorResult_ be ! CreateIterResultObject(_value_, _done_).
          1. Perform ! Call(_promiseCapability_.[[Resolve]], *undefined*, « _iteratorResult_ »).
          1. Perform ! AsyncGeneratorResumeNext(_generator_).
          1. Return *undefined*.
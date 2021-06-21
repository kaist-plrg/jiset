          1. Let _promiseCapability_ be ! NewPromiseCapability(%Promise%).
          1. Let _check_ be AsyncGeneratorValidate(_generator_, _generatorBrand_).
          1. If _check_ is an abrupt completion, then
            1. Let _badGeneratorError_ be a newly created *TypeError* object.
            1. Perform ! Call(_promiseCapability_.[[Reject]], *undefined*, « _badGeneratorError_ »).
            1. Return _promiseCapability_.[[Promise]].
          1. Let _queue_ be _generator_.[[AsyncGeneratorQueue]].
          1. Let _request_ be AsyncGeneratorRequest { [[Completion]]: _completion_, [[Capability]]: _promiseCapability_ }.
          1. Append _request_ to the end of _queue_.
          1. Let _state_ be _generator_.[[AsyncGeneratorState]].
          1. If _state_ is not ~executing~, then
            1. Perform ! AsyncGeneratorResumeNext(_generator_).
          1. Return _promiseCapability_.[[Promise]].
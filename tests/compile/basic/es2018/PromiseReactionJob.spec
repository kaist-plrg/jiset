          1. Assert: _reaction_ is a PromiseReaction Record.
          1. Let _promiseCapability_ be _reaction_.[[Capability]].
          1. Let _type_ be _reaction_.[[Type]].
          1. Let _handler_ be _reaction_.[[Handler]].
          1. If _handler_ is *undefined*, then
            1. If _type_ is `"Fulfill"`, let _handlerResult_ be NormalCompletion(_argument_).
            1. Else,
              1. Assert: _type_ is `"Reject"`.
              1. Let _handlerResult_ be ThrowCompletion(_argument_).
          1. Else, let _handlerResult_ be Call(_handler_, *undefined*, « _argument_ »).
          1. If _handlerResult_ is an abrupt completion, then
            1. Let _status_ be Call(_promiseCapability_.[[Reject]], *undefined*, « _handlerResult_.[[Value]] »).
          1. Else,
            1. Let _status_ be Call(_promiseCapability_.[[Resolve]], *undefined*, « _handlerResult_.[[Value]] »).
          1. Return Completion(_status_).
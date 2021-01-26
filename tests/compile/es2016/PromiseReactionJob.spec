          1. Assert: _reaction_ is a PromiseReaction Record.
          1. Let _promiseCapability_ be _reaction_.[[Capabilities]].
          1. Let _handler_ be _reaction_.[[Handler]].
          1. If _handler_ is `"Identity"`, let _handlerResult_ be NormalCompletion(_argument_).
          1. Else if _handler_ is `"Thrower"`, let _handlerResult_ be Completion{[[Type]]: ~throw~, [[Value]]: _argument_, [[Target]]: ~empty~}.
          1. Else, let _handlerResult_ be Call(_handler_, *undefined*, « _argument_ »).
          1. If _handlerResult_ is an abrupt completion, then
            1. Let _status_ be Call(_promiseCapability_.[[Reject]], *undefined*, « _handlerResult_.[[Value]] »).
            1. NextJob Completion(_status_).
          1. Let _status_ be Call(_promiseCapability_.[[Resolve]], *undefined*, « _handlerResult_.[[Value]] »).
          1. NextJob Completion(_status_).
          1. If NewTarget is *undefined*, throw a *TypeError* exception.
          1. If IsCallable(_executor_) is *false*, throw a *TypeError* exception.
          1. Let _promise_ be ? OrdinaryCreateFromConstructor(NewTarget, `"%PromisePrototype%"`, « [[PromiseState]], [[PromiseResult]], [[PromiseFulfillReactions]], [[PromiseRejectReactions]], [[PromiseIsHandled]] »).
          1. Set _promise_.[[PromiseState]] to `"pending"`.
          1. Set _promise_.[[PromiseFulfillReactions]] to a new empty List.
          1. Set _promise_.[[PromiseRejectReactions]] to a new empty List.
          1. Set _promise_.[[PromiseIsHandled]] to *false*.
          1. Let _resolvingFunctions_ be CreateResolvingFunctions(_promise_).
          1. Let _completion_ be Call(_executor_, *undefined*, « _resolvingFunctions_.[[Resolve]], _resolvingFunctions_.[[Reject]] »).
          1. If _completion_ is an abrupt completion, then
            1. Perform ? Call(_resolvingFunctions_.[[Reject]], *undefined*, « _completion_.[[Value]] »).
          1. Return _promise_.
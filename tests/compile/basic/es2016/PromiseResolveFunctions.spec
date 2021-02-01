            1. Assert: _F_ has a [[Promise]] internal slot whose value is an Object.
            1. Let _promise_ be the value of _F_'s [[Promise]] internal slot.
            1. Let _alreadyResolved_ be the value of _F_'s [[AlreadyResolved]] internal slot.
            1. If _alreadyResolved_.[[Value]] is *true*, return *undefined*.
            1. Set _alreadyResolved_.[[Value]] to *true*.
            1. If SameValue(_resolution_, _promise_) is *true*, then
              1. Let _selfResolutionError_ be a newly created *TypeError* object.
              1. Return RejectPromise(_promise_, _selfResolutionError_).
            1. If Type(_resolution_) is not Object, then
              1. Return FulfillPromise(_promise_, _resolution_).
            1. Let _then_ be Get(_resolution_, `"then"`).
            1. If _then_ is an abrupt completion, then
              1. Return RejectPromise(_promise_, _then_.[[Value]]).
            1. Let _thenAction_ be _then_.[[Value]].
            1. If IsCallable(_thenAction_) is *false*, then
              1. Return FulfillPromise(_promise_, _resolution_).
            1. Perform EnqueueJob(`"PromiseJobs"`, PromiseResolveThenableJob, « _promise_, _resolution_, _thenAction_ »).
            1. Return *undefined*.
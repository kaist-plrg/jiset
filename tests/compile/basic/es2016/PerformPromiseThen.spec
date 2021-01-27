            1. Assert: IsPromise(_promise_) is *true*.
            1. Assert: _resultCapability_ is a PromiseCapability record.
            1. If IsCallable(_onFulfilled_) is *false*, then
              1. Let _onFulfilled_ be `"Identity"`.
            1. If IsCallable(_onRejected_) is *false*, then
              1. Let _onRejected_ be `"Thrower"`.
            1. Let _fulfillReaction_ be the PromiseReaction { [[Capabilities]]: _resultCapability_, [[Handler]]: _onFulfilled_ }.
            1. Let _rejectReaction_ be the PromiseReaction { [[Capabilities]]: _resultCapability_, [[Handler]]: _onRejected_}.
            1. If the value of _promise_'s [[PromiseState]] internal slot is `"pending"`, then
              1. Append _fulfillReaction_ as the last element of the List that is the value of _promise_'s [[PromiseFulfillReactions]] internal slot.
              1. Append _rejectReaction_ as the last element of the List that is the value of _promise_'s [[PromiseRejectReactions]] internal slot.
            1. Else if the value of _promise_'s [[PromiseState]] internal slot is `"fulfilled"`, then
              1. Let _value_ be the value of _promise_'s [[PromiseResult]] internal slot.
              1. Perform EnqueueJob(`"PromiseJobs"`, PromiseReactionJob, « _fulfillReaction_, _value_ »).
            1. Else,
              1. Assert: The value of _promise_'s [[PromiseState]] internal slot is `"rejected"`.
              1. Let _reason_ be the value of _promise_'s [[PromiseResult]] internal slot.
              1. If the value of _promise_'s [[PromiseIsHandled]] internal slot is *false*, perform HostPromiseRejectionTracker(_promise_, `"handle"`).
              1. Perform EnqueueJob(`"PromiseJobs"`, PromiseReactionJob, « _rejectReaction_, _reason_ »).
            1. Set _promise_'s [[PromiseIsHandled]] internal slot to *true*.
            1. Return _resultCapability_.[[Promise]].
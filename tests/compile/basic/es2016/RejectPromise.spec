          1. Assert: the value of _promise_'s [[PromiseState]] internal slot is `"pending"`.
          1. Let _reactions_ be the value of _promise_'s [[PromiseRejectReactions]] internal slot.
          1. Set the value of _promise_'s [[PromiseResult]] internal slot to _reason_.
          1. Set the value of _promise_'s [[PromiseFulfillReactions]] internal slot to *undefined*.
          1. Set the value of _promise_'s [[PromiseRejectReactions]] internal slot to *undefined*.
          1. Set the value of _promise_'s [[PromiseState]] internal slot to `"rejected"`.
          1. If the value of _promise_'s [[PromiseIsHandled]] internal slot is *false*, perform HostPromiseRejectionTracker(_promise_, `"reject"`).
          1. Return TriggerPromiseReactions(_reactions_, _reason_).
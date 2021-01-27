          1. Assert: The value of _promise_.[[PromiseState]] is `"pending"`.
          1. Let _reactions_ be _promise_.[[PromiseRejectReactions]].
          1. Set _promise_.[[PromiseResult]] to _reason_.
          1. Set _promise_.[[PromiseFulfillReactions]] to *undefined*.
          1. Set _promise_.[[PromiseRejectReactions]] to *undefined*.
          1. Set _promise_.[[PromiseState]] to `"rejected"`.
          1. If _promise_.[[PromiseIsHandled]] is *false*, perform HostPromiseRejectionTracker(_promise_, `"reject"`).
          1. Return TriggerPromiseReactions(_reactions_, _reason_).
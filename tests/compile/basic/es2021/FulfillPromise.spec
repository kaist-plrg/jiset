          1. Assert: The value of _promise_.[[PromiseState]] is ~pending~.
          1. Let _reactions_ be _promise_.[[PromiseFulfillReactions]].
          1. Set _promise_.[[PromiseResult]] to _value_.
          1. Set _promise_.[[PromiseFulfillReactions]] to *undefined*.
          1. Set _promise_.[[PromiseRejectReactions]] to *undefined*.
          1. Set _promise_.[[PromiseState]] to ~fulfilled~.
          1. Return TriggerPromiseReactions(_reactions_, _value_).
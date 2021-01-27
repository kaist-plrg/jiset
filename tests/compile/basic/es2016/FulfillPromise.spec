          1. Assert: the value of _promise_'s [[PromiseState]] internal slot is `"pending"`.
          1. Let _reactions_ be the value of _promise_'s [[PromiseFulfillReactions]] internal slot.
          1. Set the value of _promise_'s [[PromiseResult]] internal slot to _value_.
          1. Set the value of _promise_'s [[PromiseFulfillReactions]] internal slot to *undefined*.
          1. Set the value of _promise_'s [[PromiseRejectReactions]] internal slot to *undefined*.
          1. Set the value of _promise_'s [[PromiseState]] internal slot to `"fulfilled"`.
          1. Return TriggerPromiseReactions(_reactions_, _value_).
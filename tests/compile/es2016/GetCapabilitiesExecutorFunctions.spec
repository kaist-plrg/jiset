            1. Assert: _F_ has a [[Capability]] internal slot whose value is a PromiseCapability Record.
            1. Let _promiseCapability_ be the value of _F_'s [[Capability]] internal slot.
            1. If _promiseCapability_.[[Resolve]] is not *undefined*, throw a *TypeError* exception.
            1. If _promiseCapability_.[[Reject]] is not *undefined*, throw a *TypeError* exception.
            1. Set _promiseCapability_.[[Resolve]] to _resolve_.
            1. Set _promiseCapability_.[[Reject]] to _reject_.
            1. Return *undefined*.
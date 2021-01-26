            1. Assert: _F_ has a [[Promise]] internal slot whose value is an Object.
            1. Let _promise_ be the value of _F_'s [[Promise]] internal slot.
            1. Let _alreadyResolved_ be the value of _F_'s [[AlreadyResolved]] internal slot.
            1. If _alreadyResolved_.[[Value]] is *true*, return *undefined*.
            1. Set _alreadyResolved_.[[Value]] to *true*.
            1. Return RejectPromise(_promise_, _reason_).
            1. Let _F_ be the active function object.
            1. Assert: _F_ has a [[Promise]] internal slot whose value is an Object.
            1. Let _promise_ be _F_.[[Promise]].
            1. Let _alreadyResolved_ be _F_.[[AlreadyResolved]].
            1. If _alreadyResolved_.[[Value]] is *true*, return *undefined*.
            1. Set _alreadyResolved_.[[Value]] to *true*.
            1. Return RejectPromise(_promise_, _reason_).
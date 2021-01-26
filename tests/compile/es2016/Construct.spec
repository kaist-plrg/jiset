        1. If _newTarget_ was not passed, let _newTarget_ be _F_.
        1. If _argumentsList_ was not passed, let _argumentsList_ be a new empty List.
        1. Assert: IsConstructor(_F_) is *true*.
        1. Assert: IsConstructor(_newTarget_) is *true*.
        1. Return ? _F_.[[Construct]](_argumentsList_, _newTarget_).
        1. If _newTarget_ is not present, set _newTarget_ to _F_.
        1. If _argumentsList_ is not present, set _argumentsList_ to a new empty List.
        1. Assert: IsConstructor(_F_) is *true*.
        1. Assert: IsConstructor(_newTarget_) is *true*.
        1. Return ? _F_.[[Construct]](_argumentsList_, _newTarget_).
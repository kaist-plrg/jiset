            1. Let _envRec_ be the function Environment Record for which the method was invoked.
            1. Assert: _envRec_.[[ThisBindingStatus]] is not ~lexical~.
            1. If _envRec_.[[ThisBindingStatus]] is ~initialized~, throw a *ReferenceError* exception.
            1. Set _envRec_.[[ThisValue]] to _V_.
            1. Set _envRec_.[[ThisBindingStatus]] to ~initialized~.
            1. Return _V_.
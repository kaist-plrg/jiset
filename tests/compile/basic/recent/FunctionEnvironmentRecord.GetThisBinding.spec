            1. Assert: _envRec_.[[ThisBindingStatus]] is not ~lexical~.
            1. If _envRec_.[[ThisBindingStatus]] is ~uninitialized~, throw a *ReferenceError* exception.
            1. Return _envRec_.[[ThisValue]].
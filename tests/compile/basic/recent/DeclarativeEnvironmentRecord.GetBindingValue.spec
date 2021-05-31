            1. Assert: _envRec_ has a binding for _N_.
            1. If the binding for _N_ in _envRec_ is an uninitialized binding, throw a *ReferenceError* exception.
            1. Return the value currently bound to _N_ in _envRec_.
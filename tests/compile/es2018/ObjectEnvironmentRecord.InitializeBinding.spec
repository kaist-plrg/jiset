            1. Let _envRec_ be the object Environment Record for which the method was invoked.
            1. Assert: _envRec_ must have an uninitialized binding for _N_.
            1. Record that the binding for _N_ in _envRec_ has been initialized.
            1. Return ? _envRec_.SetMutableBinding(_N_, _V_, *false*).
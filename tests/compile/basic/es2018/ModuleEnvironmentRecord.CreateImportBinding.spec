            1. Let _envRec_ be the module Environment Record for which the method was invoked.
            1. Assert: _envRec_ does not already have a binding for _N_.
            1. Assert: _M_ is a Module Record.
            1. Assert: When _M_.[[Environment]] is instantiated it will have a direct binding for _N2_.
            1. Create an immutable indirect binding in _envRec_ for _N_ that references _M_ and _N2_ as its target binding and record that the binding is initialized.
            1. Return NormalCompletion(~empty~).
            1. Let _envRec_ be the declarative Environment Record for which the method was invoked.
            1. Assert: _envRec_ must have an uninitialized binding for _N_.
            1. Set the bound value for _N_ in _envRec_ to _V_.
            1. <emu-not-ref>Record</emu-not-ref> that the binding for _N_ in _envRec_ has been initialized.
            1. Return NormalCompletion(~empty~).
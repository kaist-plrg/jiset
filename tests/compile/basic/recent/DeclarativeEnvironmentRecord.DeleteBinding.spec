            1. Assert: _envRec_ has a binding for the name that is the value of _N_.
            1. If the binding for _N_ in _envRec_ cannot be deleted, return *false*.
            1. Remove the binding for _N_ from _envRec_.
            1. Return *true*.
            1. Let _envRec_ be the declarative Environment Record for which the method was invoked.
            1. Assert: _envRec_ does not already have a binding for _N_.
            1. Create an immutable binding in _envRec_ for _N_ and record that it is uninitialized. If _S_ is *true*, record that the newly created binding is a strict binding.
            1. Return NormalCompletion(~empty~).
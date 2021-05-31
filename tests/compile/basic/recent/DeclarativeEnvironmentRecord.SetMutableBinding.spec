            1. [id="step-setmutablebinding-missing-binding"] If _envRec_ does not have a binding for _N_, then
              1. If _S_ is *true*, throw a *ReferenceError* exception.
              1. Perform _envRec_.CreateMutableBinding(_N_, *true*).
              1. Perform _envRec_.InitializeBinding(_N_, _V_).
              1. Return NormalCompletion(~empty~).
            1. If the binding for _N_ in _envRec_ is a strict binding, set _S_ to *true*.
            1. If the binding for _N_ in _envRec_ has not yet been initialized, throw a *ReferenceError* exception.
            1. Else if the binding for _N_ in _envRec_ is a mutable binding, change its bound value to _V_.
            1. Else,
              1. Assert: This is an attempt to change the value of an immutable binding.
              1. If _S_ is *true*, throw a *TypeError* exception.
            1. Return NormalCompletion(~empty~).
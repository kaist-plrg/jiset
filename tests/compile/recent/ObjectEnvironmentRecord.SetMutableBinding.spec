            1. Let _bindings_ be the binding object for _envRec_.
            1. Let _stillExists_ be ? HasProperty(_bindings_, _N_).
            1. If _stillExists_ is *false* and _S_ is *true*, throw a *ReferenceError* exception.
            1. Return ? Set(_bindings_, _N_, _V_, _S_).
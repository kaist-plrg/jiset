            1. Let _envRec_ be the module Environment Record for which the method was invoked.
            1. Assert: _envRec_ has a binding for _N_.
            1. If the binding for _N_ is an indirect binding, then
              1. Let _M_ and _N2_ be the indirection values provided when this binding for _N_ was created.
              1. Let _targetEnv_ be _M_.[[Environment]].
              1. If _targetEnv_ is *undefined*, throw a *ReferenceError* exception.
              1. Let _targetER_ be _targetEnv_'s EnvironmentRecord.
              1. Return ? _targetER_.GetBindingValue(_N2_, _S_).
            1. If the binding for _N_ in _envRec_ is an uninitialized binding, throw a *ReferenceError* exception.
            1. Return the value currently bound to _N_ in _envRec_.
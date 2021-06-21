            1. Let _bindings_ be the binding object for _envRec_.
            1. Let _value_ be ? HasProperty(_bindings_, _N_).
            1. If _value_ is *false*, then
              1. If _S_ is *false*, return the value *undefined*; otherwise throw a *ReferenceError* exception.
            1. Return ? Get(_bindings_, _N_).
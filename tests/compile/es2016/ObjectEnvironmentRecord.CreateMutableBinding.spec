            1. Let _envRec_ be the object Environment Record for which the method was invoked.
            1. Let _bindings_ be the binding object for _envRec_.
            1. If _D_ is *true*, let _configValue_ be *true*; otherwise let _configValue_ be *false*.
            1. Return ? DefinePropertyOrThrow(_bindings_, _N_, PropertyDescriptor{[[Value]]: *undefined*, [[Writable]]: *true*, [[Enumerable]]: *true*, [[Configurable]]: _configValue_}).
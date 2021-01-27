            1. Let _envRec_ be the global Environment Record for which the method was invoked.
            1. Let _ObjRec_ be _envRec_.[[ObjectRecord]].
            1. Let _globalObject_ be the binding object for _ObjRec_.
            1. Let _hasProperty_ be ? HasOwnProperty(_globalObject_, _N_).
            1. If _hasProperty_ is *true*, return *true*.
            1. Return ? IsExtensible(_globalObject_).
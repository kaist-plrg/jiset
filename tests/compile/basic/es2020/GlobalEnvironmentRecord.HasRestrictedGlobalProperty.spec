            1. Let _envRec_ be the global Environment Record for which the method was invoked.
            1. Let _ObjRec_ be _envRec_.[[ObjectRecord]].
            1. Let _globalObject_ be the binding object for _ObjRec_.
            1. Let _existingProp_ be ? _globalObject_.[[GetOwnProperty]](_N_).
            1. If _existingProp_ is *undefined*, return *false*.
            1. If _existingProp_.[[Configurable]] is *true*, return *false*.
            1. Return *true*.
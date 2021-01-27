            1. Let _ObjRec_ be _envRec_.[[ObjectRecord]].
            1. Let _globalObject_ be the binding object for _ObjRec_.
            1. Let _existingProp_ be ? _globalObject_.[[GetOwnProperty]](_N_).
            1. If _existingProp_ is *undefined*, return ? IsExtensible(_globalObject_).
            1. If _existingProp_.[[Configurable]] is *true*, return *true*.
            1. If IsDataDescriptor(_existingProp_) is *true* and _existingProp_ has attribute values { [[Writable]]: *true*, [[Enumerable]]: *true* }, return *true*.
            1. Return *false*.